<<<<<<< HEAD
package Server;
=======
package src.Server;
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
<<<<<<< HEAD
import tool.*;

public class ClientHandler implements Runnable {
    static int nb_client = 0;
    private int id;
=======

public class ClientHandler  implements Runnable{

>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;

<<<<<<< HEAD
=======
        // Préparation des flux
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("[SERVEUR]Bienvenue !");
<<<<<<< HEAD
            Logger.log("Client connecté : " + clientSocket.getInetAddress() + " numero " + ClientHandler.nb_client, Logger.LogType.SUCCESS,"server");
        } catch (IOException e) {
            Logger.log("Erreur avec le client : " + e.getMessage(), Logger.LogType.ERROR,"server");
        }

        this.id = ClientHandler.nb_client;
        ClientHandler.nb_client++;
=======
            server.log("Client connecté : " + clientSocket.getInetAddress(), 1); // SUCCESS
        } catch (IOException e) {
            server.log("Erreur avec le client : " + e.getMessage(), 4); // ERROR
        }

>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
    }

    public void sendData(String message) {
        if (out != null) {
            out.println(message);
        }
    }

<<<<<<< HEAD
    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                Logger.log("Message du client : " + message, Logger.LogType.INFO,"server");
                server.getClient(1).sendData(message);

                if (message.equals("shutdown")){
                    server.shutdown();
                }

=======

    @Override
    public void run() {
        try {


            String message;
            // 🔁 boucle d'écoute permanente du client
            while ((message = in.readLine()) != null) {
                server.log("Message du client : " + message, 2); // INFO
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538

                if (message.equalsIgnoreCase("bye")) {
                    break;
                }
            }

        } catch (IOException e) {
<<<<<<< HEAD
            Logger.log("Erreur lors de la fermeture du socket client : " + e.getMessage(), Logger.LogType.ERROR,"server");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            server.removeClient(this);
=======
            server.log("Erreur lors de la fermeture du socket client : " + e.getMessage(), 4); // ERROR

        } finally {
            // Nettoyage à la fin
            try {
                clientSocket.close();
            } catch (IOException e) {}
                server.removeClient(this);

>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
        }
    }
}
