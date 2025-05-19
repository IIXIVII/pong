package Server;

import Common.Tools.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import Common.Messages.*;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server implements Runnable {

    private String adminKey;
    private Integer port = 8085;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    //private ArrayList<Thread> threadClients = new ArrayList<>();
    private boolean running = false;
    private String gameLogic = null;
    public Server(Integer port, String adminKey) {
        this.port = port;
        this.adminKey = adminKey;
    }

    @Override
    public void run() {
        Logger.log("Serveur démarre !", Logger.LogType.DEBUG,"SERVER");
        this.running = true;

        if (!this.initServer()) {
            this.running = false;
            Logger.log("Arrêt du serveur car l'initialisation du socket a échoué.", Logger.LogType.ERROR, "SERVER");
            return;
        }

        // Boucle d'écoute ou autre traitement serveur
        while (clients.size() != 2 && this.running) {

            Socket clientSocket = null; // Attend un client
            try {
                Logger.log("En attente d'un client...", Logger.LogType.INFO,"server"); // INFO
                clientSocket = serverSocket.accept();


                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
                Logger.log("Nouveau client : " + clientSocket.getInetAddress(), Logger.LogType.SUCCESS,"server");




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


        new ArrayList<>(clients).forEach(client -> {
            client.stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.removeClient(client); // OK ici car tu ne modifies pas la liste source de la boucle
        });



        this.requestShutdown();
        Logger.log("Serveur est eteind", Logger.LogType.SUCCESS,"SERVER");
    }



    public boolean initServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            Logger.log("Serveur démarré sur le port " + port, Logger.LogType.SUCCESS,"server");
            return true;
        } catch (IOException e) {
            Logger.log("Erreur serveur : " + e.getMessage(), Logger.LogType.ERROR,"server");
            return false;
        }
    }

    public void requestShutdown() {
        running = false;
        this.broadcastMessageToAll(new ShutdownMessage(-1,"client doit se deconnecté"));

        Logger.log("Arrêt du serveur demandé...", Logger.LogType.WARNING,"server");
        try {
            Thread.sleep(1000);
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Débloque le .accept()
                Logger.log("Socket fermé", Logger.LogType.SUCCESS,"SERVER");
            }
        } catch (IOException e) {
            Logger.log("Erreur lors de la fermeture du serveur : " + e.getMessage(), Logger.LogType.ERROR,"server");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public void processClientMessage(ClientHandler clientHandler, String rawMessage) {
        GameMessage deserializedMessage = null;
        int senderPlayerId = clientHandler.getId(); // ID du joueur via le handler

        try {
            JSONObject jsonMsg = new JSONObject(rawMessage);
            String cmd = jsonMsg.getString("cmd");

            // Désérialisation basée sur la commande
            switch (cmd) {
                case MovePaddleMessage.CMD:
                    deserializedMessage = new MovePaddleMessage(jsonMsg, senderPlayerId);
                    break;
                case ConnectMessage.CMD :
                    deserializedMessage = new ConnectMessage(jsonMsg, senderPlayerId);
                    if (((ConnectMessage) deserializedMessage).getAdminKey() == this.adminKey){
                        clientHandler.admin = true;
                    }
                    deserializedMessage.log("server - handler");


                    ConnectMessage m = new ConnectMessage(clientHandler.getId(),"","Connexion validé !");
                    m.log("IFUGAJKEFJKH");
                    clientHandler.sendMessage(m);

                    return;

                case ShutdownMessage.CMD:
                    if (clientHandler.isAdmin()){
                        requestShutdown();
                        return;
                    } else {
                        Logger.log("Client n'a pas les permission pour shutdown le serveur", Logger.LogType.WARNING,"SERVER");
                    }


                // Ajouter d'autres types de messages que le client peut envoyer
                default:
                    Logger.log("Commande client inconnue: " + cmd + " de Joueur " + senderPlayerId, Logger.LogType.WARNING, "SERVER");

                    return;
            }

            if (deserializedMessage != null) {
                deserializedMessage.log("SERVER_RECV"); // Utilise la méthode log de GameMessage

                if (gameLogic != null) {
                    //gameLogic.processPlayerAction(deserializedMessage); // GameLogic traite l'action
                } else {
                    Logger.log("GameLogic non initialisé, message ignoré: " + cmd, Logger.LogType.WARNING, "SERVER");
                }
            }

        } catch (JSONException e) {
            Logger.log("Erreur JSON lors du traitement du message de Joueur " + senderPlayerId + ": " + e.getMessage() + " | Brut: " + rawMessage, Logger.LogType.ERROR, "SERVER");
        } catch (Exception e) { // Pour attraper toute autre erreur non prévue
            Logger.log("Erreur inattendue en traitant le message de Joueur " + senderPlayerId + ": " + e.getMessage(), Logger.LogType.ERROR, "SERVER");
            e.printStackTrace();
        }
    }


    public void removeClient(ClientHandler removeclient) {

        this.clients.remove(removeclient);
        Logger.log("Client retiré : " + removeclient, Logger.LogType.WARNING,"server");
    }

    public ClientHandler getClient(int index){
        return this.clients.get(index);
    }


    public void broadcastMessageToAll(GameMessage message) {
        if (message == null) return;
        message.log("SERVER - broadcast");

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }
}



