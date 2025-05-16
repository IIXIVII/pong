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

    private Integer port = 8085;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    //private ArrayList<Thread> threadClients = new ArrayList<>();
    private boolean running = false;

    public void Server(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        Logger.log("Serveur démarre !", Logger.LogType.DEBUG,"SERVER");
        this.running = true;

        this.initServer();
        // Boucle d'écoute ou autre traitement serveur
        while (clients.size() != 2 && this.running) {

            Socket clientSocket = null; // Attend un client
            try {
                Logger.log("En attente d'un client...", Logger.LogType.INFO,"server"); // INFO
                clientSocket = serverSocket.accept();
                Logger.log("Nouveau client : " + clientSocket.getInetAddress(), Logger.LogType.SUCCESS,"server");


                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);

//                threadClients.add(new Thread(handler));
//                threadClients.getLast().start();
                new Thread(handler).start();

            } catch (IOException e) {

                Logger.log("Erreur lors de l'acceptation du client : " + e.getMessage(), Logger.LogType.ERROR,"server");
                throw new RuntimeException(e);
            }

        }

        while (this.running){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        clients.forEach(client -> {
            client.stop();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.removeClient(client);
            }
        );


        this.shutdown();
        Logger.log("Serveur est eteind", Logger.LogType.SUCCESS,"SERVER");
    }



    public void initServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            Logger.log("Serveur démarré sur le port " + port, Logger.LogType.SUCCESS,"server");
        } catch (IOException e) {
            Logger.log("Erreur serveur : " + e.getMessage(), Logger.LogType.ERROR,"server");
        }
    }

    public void shutdown() {
        running = false;
        Logger.log("Arrêt du serveur demandé...", Logger.LogType.WARNING,"server");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Débloque le .accept()
                Logger.log("Socket fermé", Logger.LogType.SUCCESS,"SERVER");
            }
        } catch (IOException e) {
            Logger.log("Erreur lors de la fermeture du serveur : " + e.getMessage(), Logger.LogType.ERROR,"server");

        }
    }



    public void removeClient(ClientHandler removeclient) {

        this.clients.remove(removeclient);
        Logger.log("Client retiré : " + removeclient, Logger.LogType.WARNING,"server");
    }

    public ClientHandler getClient(int index){
        return this.clients.get(index);
    }

}



