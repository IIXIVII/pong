// Refactored Server.java
package Server;

import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.CommandMessage;
import Common.Messages.ConnectData;
import Common.Messages.GameMessage;
import Common.Tools.Logger;
import Server.Game.GameLogic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Server implements Runnable {
    private final GameStateDto gameState;
    private String adminKey;
    private final int port;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private volatile boolean running = false;

    private final BlockingQueue<GameMessage<?>> responses = new LinkedBlockingQueue<>();

    private GameLogic gameLogic;

    public Server(int port, String adminKey) {
        this.port = port;
        this.adminKey = adminKey;
        this.gameLogic = new GameLogic();
        this.gameState = new GameStateDto();
    }

    @Override
    public void run() {
        Logger.log("Serveur démarre !", Logger.LogType.DEBUG, "SERVER");
        this.running = true;

        try {
            serverSocket = new ServerSocket(port);
            Logger.log("Serveur démarré sur le port " + port, Logger.LogType.SUCCESS, "SERVER");

            while (running) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                synchronized (clients) {
                    clients.add(handler);
                }
                new Thread(handler).start();
                Logger.log("Nouveau client connecté: " + socket.getInetAddress(), Logger.LogType.SUCCESS, "SERVER");
            }
        } catch (IOException e) {
            if (running) {
                Logger.log("Erreur serveur: " + e.getMessage(), Logger.LogType.ERROR, "SERVER");
            }
        }
    }

    public void shutdownServer() {
        running = false;
        // Broadcast shutdown message
        broadcast(new GameMessage<>(CommandMessage.SHUTDOWN, -1, "Arrêt du serveur", "", GameStatus.WELCOME));
        // Évite ConcurrentModificationException en copiant la liste
        new ArrayList<>(this.clients).forEach(this::removeClient);

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.log("Erreur fermeture du serveur: " + e.getMessage(), Logger.LogType.ERROR, "SERVER");
        }

    }

    public void process(ClientHandler client, GameMessage<?> msg) {
        Logger.log("Traitement message reçu", Logger.LogType.DEBUG, "SERVER");
        switch (msg.getCmd()) {
            case CONNECT -> {
                @SuppressWarnings("unchecked")
                GameMessage<ConnectData> connectMsg = (GameMessage<ConnectData>) msg;
                if (adminKey.equals(connectMsg.getData().getAdminKey())) {
                    client.setAdmin(true);
                    client.send(new GameMessage<>(CommandMessage.CONNECT, client.getId(), "Connexion validée !",
                            new ConnectData(adminKey, clients.size()), GameStatus.WELCOME));
                } else {
                    client.send(new GameMessage<>(CommandMessage.CONNECT, client.getId(), "Connexion validée !",
                            new ConnectData("", clients.size()), GameStatus.WELCOME));
                }


                if (this.clients.size() == 2) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    GameStateDto g = new GameStateDto();
                    g.connectedPlayers = 2;
                    g.message = "ceci ets un test car je ne comprend pas";
                    this.broadcast(new GameMessage<>(CommandMessage.INFO_SERVER,-2,"Connection reussie",g,GameStatus.CONNECTING));
                }
            }
            case QUIT -> {
                removeClient(client);
                broadcast(new GameMessage<>(CommandMessage.QUIT, client.getId(), "Client déconnecté", "", GameStatus.WELCOME));
                GameStateDto g = new GameStateDto();
                g.connectedPlayers = 1;
                g.message = "ceci ets un test car je ne comprend pas";
                this.broadcast(new GameMessage<>(CommandMessage.INFO_SERVER,-2,"Connection reussie",g,GameStatus.LOBBY_WAITING));
            }
            case SHUTDOWN -> {
                if (client.isAdmin()) shutdownServer();
            }
            case START_GAME -> {
                this.gameLogic =  new GameLogic();
                broadcast(new GameMessage<>(CommandMessage.START_GAME, -2, "Le jeu commence",GameStatus.PLAYING));
            }

            default -> Logger.log("Commande inconnue: " + msg.getCmd(), Logger.LogType.WARNING, "SERVER");
        }
    }

    public void broadcast(GameMessage<?> message) {
        Logger.log("Broadcast: " + message.getCmd(), Logger.LogType.DEBUG, "SERVER");
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        Logger.log("Client retiré: " + client.getId(), Logger.LogType.INFO, "SERVER");
    }
    public ClientHandler getClient(int index){
       return this.clients.get(index);
    }
    public int getNbClient(){
        return clients.size();
    }

}
