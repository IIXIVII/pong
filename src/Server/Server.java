// Refactored Server.java
package Server;

import Common.GameConfig;
import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.CommandMessage;
import Common.Messages.ConnectData;
import Common.Messages.GameMessage;
import Common.PlayerInput;
import Common.Tools.Logger;
import Server.Game.GameLogic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class Server implements Runnable {
    private final GameStateDto gameState = new GameStateDto();
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

    }

    @Override
    public void run() {
        Logger.log("Serveur démarre !", Logger.LogType.DEBUG, "SERVER");
        this.running = true;

        try {
            serverSocket = new ServerSocket(port);
            Logger.log("Serveur démarré sur le port " + port, Logger.LogType.SUCCESS, "SERVER");

            while (running) {
                Socket socket = serverSocket.accept(); // Accepte une nouvelle connexion
                socket.setTcpNoDelay(true);


                synchronized (clients) { // Synchronisation pour la vérification et l'ajout
                    if (clients.size() < GameConfig.MAX_PLAYERS) {
                        ClientHandler handler = new ClientHandler(socket, this);
                        clients.add(handler);
                        new Thread(handler).start();
                        Logger.log("Nouveau client connecté: " + socket.getInetAddress() + " (" + clients.size() + "/" + GameConfig.MAX_PLAYERS + ")", Logger.LogType.SUCCESS, "SERVER");

                        // Si c'est le premier client, on met à jour l'état pour indiquer l'attente
                        if (clients.size() == 1) {
                            gameState.connectedPlayers = 1;
                            gameState.gameStatus = GameStatus.LOBBY_WAITING;
                            // Pas besoin de broadcast ici, le handler enverra un message de connexion
                        }
                        // La logique pour le deuxième joueur est déjà dans process() -> CONNECT

                    } else {
                        Logger.log("Serveur plein (" + clients.size() + "/" + GameConfig.MAX_PLAYERS + "). Connexion refusée pour: " + socket.getInetAddress(), Logger.LogType.WARNING, "SERVER");
                    }
                }
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
        Logger.log("Serveur fermé", Logger.LogType.SUCCESS, "SERVER");
    }

    public void process(ClientHandler client, GameMessage<?> msg) {
        //msg.log("SERVER");
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

                    this.gameState.connectedPlayers = 2;
                    this.gameState.message = "Les deux client sont connectés !";
                    this.broadcast(new GameMessage<>(CommandMessage.INFO_SERVER,-2,"Connection reussie",this.gameState,GameStatus.CONNECTING));
                }
            }
            case QUIT -> {
                removeClient(client);


                GameStateDto g = new GameStateDto(this.gameState);
                g.connectedPlayers = 1;
                g.gameStatus = GameStatus.LOBBY_WAITING;
                g.message = "Un joueur c'est déconnecté";

                this.broadcast(new GameMessage<>(CommandMessage.INFO_SERVER,-2,"Client déconnecté",g,GameStatus.LOBBY_WAITING));


            }
            case SHUTDOWN -> {
                if (client.isAdmin()) shutdownServer();
            }
            case START_GAME -> {

                this.gameState.StartTargetTime = LocalDateTime.now().plusSeconds(4);
                this.gameState.gameStatus = GameStatus.PLAYING;
                this.gameState.playing = true;
                this.gameLogic.initializeNewGame(this.gameState, this);
                // Create a copy to send to avoid reference issues
                GameStateDto gameStateCopy = new GameStateDto(this.gameState);
                gameStateCopy.playing = true;

                Logger.log("Start time: " + gameStateCopy.StartTargetTime, Logger.LogType.INFO, "SERVER");
                Logger.log("GameState: " + gameStateCopy.toString(), Logger.LogType.DEBUG, "SERVER");

                this.broadcast(new GameMessage<>(CommandMessage.START_GAME, -2, "Le jeu commence", gameStateCopy, GameStatus.PLAYING));
                this.gameState.playing = true;
                long delayMillis = java.time.Duration.between(LocalDateTime.now(), this.gameState.StartTargetTime).toMillis();
                if (delayMillis <= 0) delayMillis = 1; // Ensure positive delay if time already passed

                Logger.log("La logique de jeu démarrera dans " + delayMillis + "ms.", Logger.LogType.INFO, "SERVER");

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    Logger.log("Compte à rebours terminé. Démarrage du thread de logique de jeu.", Logger.LogType.INFO, "SERVER");
                    // Check if game status is still PLAYING (e.g. not cancelled by player disconnect)
                    synchronized(gameState) {
                        if (GameStatus.PLAYING.equals(this.gameState.gameStatus)) {
                            new Thread(this.gameLogic).start();
                        } else {
                            Logger.log("Démarrage du jeu annulé, statut actuel: " + this.gameState.gameStatus, Logger.LogType.INFO, "SERVER");
                        }
                    }
                    scheduler.shutdown();
                }, delayMillis, TimeUnit.MILLISECONDS);

            }
            case ACTION_PLAYER -> {
                this.gameLogic.actionPlayer((PlayerInput) msg.getData());
            }

            default -> Logger.log("Commande inconnue: " + msg.getCmd(), Logger.LogType.WARNING, "SERVER");
        }
    }

    public void broadcast(GameMessage<?> message) {
        //Logger.log("Broadcast: " + message.getCmd(), Logger.LogType.DEBUG, "SERVER");
        //message.log("SERVER");
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
