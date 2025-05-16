package src.Server;

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
        log("Serveur démarre !", 5);

        this.initServer();
        // Boucle d'écoute ou autre traitement serveur
        while (true) {

            Socket clientSocket = null; // Attend un client
            try {
                log("En attente d'un client...", 2);  // INFO
                clientSocket = serverSocket.accept();
                log("Nouveau client : " + clientSocket.getInetAddress(), 1); // SUCCESS

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);

                new Thread(handler).start();

            } catch (IOException e) {
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
        }
    }


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

    }

}



