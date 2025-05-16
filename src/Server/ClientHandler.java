package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import tool.*;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("[SERVEUR]Bienvenue !");
            Logger.log("Client connecté : " + clientSocket.getInetAddress(), Logger.LogType.SUCCESS,"server");
        } catch (IOException e) {
            Logger.log("Erreur avec le client : " + e.getMessage(), Logger.LogType.ERROR,"server");
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
            while ((message = in.readLine()) != null) {
                Logger.log("Message du client : " + message, Logger.LogType.INFO,"server");

                if (message.equalsIgnoreCase("bye")) {
                    break;
                }
            }

        } catch (IOException e) {
            Logger.log("Erreur lors de la fermeture du socket client : " + e.getMessage(), Logger.LogType.ERROR,"server");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            server.removeClient(this);
        }
    }
}
