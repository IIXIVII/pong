package Server;

import tool.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Server implements Runnable {
    private final Integer port = 8085;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    @Override
    public void run() {
        Logger.log("Serveur démarre !", Logger.LogType.DEBUG,"SERVER");

        this.initServer();
        // Boucle d'écoute ou autre traitement serveur
        while (true) {

            Socket clientSocket = null; // Attend un client
            try {
                Logger.log("En attente d'un client...", Logger.LogType.INFO,"sever"); // INFO
                clientSocket = serverSocket.accept();
                Logger.log("Nouveau client : " + clientSocket.getInetAddress(), Logger.LogType.SUCCESS,"server");

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);

                new Thread(handler).start();

            } catch (IOException e) {
                Logger.log("Erreur lors de l'acceptation du client : " + e.getMessage(), Logger.LogType.ERROR,"server");
                throw new RuntimeException(e);
            }



        }
    }



    public void initServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            Logger.log("Serveur démarré sur le port " + port, Logger.LogType.SUCCESS,"server");
        } catch (IOException e) {
            Logger.log("Erreur serveur : " + e.getMessage(), Logger.LogType.ERROR,"server");
        }
    }

    public void removeClient(ClientHandler removeclient) {
        this.clients.remove(removeclient);
        Logger.log("Client retiré : " + removeclient, Logger.LogType.WARNING,"server");
    }


}



