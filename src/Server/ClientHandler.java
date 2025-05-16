package src.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler  implements Runnable{

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;

        // Préparation des flux
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("[SERVEUR]Bienvenue !");
            server.log("Client connecté : " + clientSocket.getInetAddress(), 1); // SUCCESS
        } catch (IOException e) {
            server.log("Erreur avec le client : " + e.getMessage(), 4); // ERROR
        }

    }

    public void sendData(String message) {
        if (out != null) {
            out.println(message);
        }
    }


    @Override
    public void run() {
        try {


            String message;
            // 🔁 boucle d'écoute permanente du client
            while ((message = in.readLine()) != null) {
                server.log("Message du client : " + message, 2); // INFO

                if (message.equalsIgnoreCase("bye")) {
                    break;
                }
            }

        } catch (IOException e) {
            server.log("Erreur lors de la fermeture du socket client : " + e.getMessage(), 4); // ERROR

        } finally {
            // Nettoyage à la fin
            try {
                clientSocket.close();
            } catch (IOException e) {}
                server.removeClient(this);

        }
    }
}
