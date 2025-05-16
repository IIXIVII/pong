<<<<<<< HEAD
package Server;

import tool.*;
=======
package src.Server;
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Server implements Runnable {
<<<<<<< HEAD
    private Integer port = 8085;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
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
=======
    private final Integer port = 8085;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    @Override
    public void run() {
        log("Serveur démarre !", 5);

        this.initServer();
        // Boucle d'écoute ou autre traitement serveur
        while (true) {

            Socket clientSocket = null; // Attend un client
            try {
                log("En attente d'un client...", 2);  // INFO
                clientSocket = serverSocket.accept();
                log("Nouveau client : " + clientSocket.getInetAddress(), 1); // SUCCESS
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);

                new Thread(handler).start();

            } catch (IOException e) {
<<<<<<< HEAD
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


        clients.forEach(client -> this.removeClient(client));


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
=======
                log("Erreur lors de l'acceptation du client : " + e.getMessage(), 4); // ERROR
                throw new RuntimeException(e);
            }



        }
    }


    public void initServer(){
        try  {
            this.serverSocket = new ServerSocket(port);
            log("Serveur démarré sur le port " + port, 1); // SUCCESS
        } catch (IOException e) {
            log("Erreur serveur : " + e.getMessage(), 4);  // ERROR
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
        }
    }


<<<<<<< HEAD
    public void removeClient(ClientHandler removeclient) {

        this.clients.remove(removeclient);
        Logger.log("Client retiré : " + removeclient, Logger.LogType.WARNING,"server");
    }

    public ClientHandler getClient(int index){
        return this.clients.get(index);
=======
    public void removeClient(ClientHandler removeclient){
        this.clients.remove(removeclient);
        log("Client retiré : " + removeclient, 3); // WARNING
    }

    public void log(String msg, int type){
        String logType;
        switch (type) {
            case 1:
                logType = "\u001B[32m[SUCCESS]\u001B[0m";// vert

                break;
            case 2:
                logType = "\u001B[34m[INFO]\u001B[0m";// Bleu

                break;
            case 3:
                logType = "\u001B[33m[WARNING]\u001B[0m";   // Jaune

                break;
            case 4:
                logType = "\u001B[31m[ERROR]\u001B[0m";     // Rouge
                break;
            case 5:
                logType = "\u001B[35m[DEBUT]\u001B[0m";     // Magenta
                break;

            default:
                logType = "\u001B[0m[UNKNOWN]\u001B[0m";   // Magenta
        }

        System.out.println(logType + "\u001B[33m" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\u001B[0m-\u001B[2m SERVER \u001B[0m- " + msg);

>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
    }

}



