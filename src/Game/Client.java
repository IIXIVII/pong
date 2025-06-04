// Classe de client pour la connexion au serveur
package Game;

import Common.GameStatus;
import Common.Messages.CommandMessage;
import Common.Messages.ConnectData;
import Common.Messages.GameMessage;
import Common.Tools.Logger;
import Server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Cette classe représente un client dans le jeu, responsable de la connexion au serveur,
 * d'envoi et de réception de messages, ainsi que de gestion de l'état du jeu.
 */
public class Client implements Runnable {

    private static Client instance; // Instanciation unique de la classe

    private int id = -1; // Identifiant attribué par le serveur
    private final String serverHost; // Adresse IP du serveur
    private final int serverPort; // Numéro de port utilisé pour la connexion
    private boolean host = false; // Indique si le client est l'administrateur

    private Socket socket; // Connexion réseau avec le serveur
    private ObjectOutputStream out; // Flux d'écriture pour envoyer des messages
    private ObjectInputStream in; // Flux de lecture pour recevoir des messages

    private PongClientApp app; // Référence à l'application cliente

    private final BlockingQueue<GameMessage<?>> responses = new LinkedBlockingQueue<>(); // File d'attente de réponses reçues du serveur
    private volatile boolean running = false; // Indique si la connexion est active

    /**
     * Constructeur pour instancier le client.
     *
     * @param app        Référence à l'application cliente
     * @param serverHost Adresse IP du serveur
     * @param serverPort Numéro de port utilisé pour la connexion
     * @param adminKey   Clé d'administration (null si pas administrateur)
     * @param createServer Indique si le client doit créer un serveur (false par défaut)
     */
    private Client(PongClientApp app, String serverHost, int serverPort, String adminKey, boolean createServer) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.host = (adminKey != null && !adminKey.isEmpty()); // Vérifie si la clé d'administration est valide
        this.app = app;

        if (createServer) {
            new Thread(new Server(serverPort, adminKey)).start(); // Lancement du serveur
        }

        try {
            Thread.sleep(1000); // Attend 1 seconde pour permettre au serveur de démarrer
            connect(adminKey); // Établit la connexion avec le serveur
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Logger.log("Client administrateur ? :" + host, Logger.LogType.INFO, "CLIENT");

        new Thread(this).start(); // Lance le thread pour écouter les messages du serveur
    }

    /**
     * Méthode statique pour obtenir l'instance unique de la classe (singleton).
     *
     * @param app Référence à l'application cliente
     * @param host Adresse IP du serveur
     * @param port Numéro de port utilisé pour la connexion
     * @param adminKey Clé d'administration (null si pas administrateur)
     * @param createServer Indique si le client doit créer un serveur (false par défaut)
     * @return L'instance unique du client
     */
    public static synchronized Client getInstance(PongClientApp app, String host, int port, String adminKey, boolean createServer) {
        if (instance == null) {
            instance = new Client(app, host, port, adminKey, createServer);
        }
        return instance;
    }

    /**
     * Établit la connexion avec le serveur.
     *
     * @param adminKey Clé d'administration (null si pas administrateur)
     * @throws IOException Si une erreur de réseau se produit
     */
    private void connect(String adminKey) throws IOException {
        socket = new Socket(serverHost, serverPort);
        socket.setTcpNoDelay(true); // Active la transmission de données sans retard

        out = new ObjectOutputStream(socket.getOutputStream()); // Flux d'écriture pour envoyer des messages
        in = new ObjectInputStream(socket.getInputStream()); // Flux de lecture pour recevoir des messages
        running = true; // Indique que la connexion est active

        // Envoi de la demande de connexion
        GameMessage<ConnectData> connectMsg = new GameMessage<>(CommandMessage.CONNECT, -1,
                "Demande de connexion", new ConnectData(adminKey, 0), GameStatus.WELCOME);
        send(connectMsg);

        // Lecture de la réponse de connexion
        try {
            @SuppressWarnings("unchecked")
            GameMessage<ConnectData> resp = (GameMessage<ConnectData>) in.readObject();
            this.id = resp.getId(); // Récupère l'identifiant attribué par le serveur
            this.host = resp.getData().getAdminKey().equals(adminKey) && resp.getData().getAdminKey().length() != 0; // Vérifie si la clé d'administration est valide
            Logger.log("Connecté avec ID=" + id + " nbConnected=" + resp.getData().getNbConnected(),Logger.LogType.INFO, "CLIENT");
            app.gameState.gameStatus = resp.getCurrentStatus();
            app.gameState.connectedPlayers = resp.getData().getNbConnected();

        } catch (ClassNotFoundException e) {
            throw new IOException("Type de message inconnu", e);
        }
    }
// Récupération du message reçu du serveur et ajout à la file d'attente de réponses
    @Override
    public void run() {
        try {
            while (running) {
                @SuppressWarnings("unchecked")
                GameMessage<?> msg = (GameMessage<?>) in.readObject(); // Lecture du message reçu du serveur
                //Logger.log("Message reçu: " + msg.getCmd(), Logger.LogType.DEBUG, "CLIENT");
                responses.offer(msg); // Ajoute le message à la file d'attente de réponses
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.log("Erreur réception: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT");
            shutdown();
        }
    }

/**
 * Méthode pour traiter les messages reçus du serveur.
 *
 * @param msg Message à traiter
 * @return Le message traité (même objet que le paramètre)
 */
    public GameMessage process(GameMessage msg){
        msg.log("client");
        switch (msg.getCmd()) {
            case UPDATE_GAME_STATE:
                //Logger.log(app.gameState.toString(), Logger.LogType.DEBUG, "client");

                break;
            case CONNECT:
                Logger.log("passage au lobby",Logger.LogType.INFO,"CLIENT");
                break;
            case INFO_SERVER:
                Logger.log("info du server",Logger.LogType.SUCCESS,"CLIENT");
                break;



            case START_GAME:
                Logger.log("Début de la partie reçu du serveur", Logger.LogType.INFO, "CLIENT");

                break;

            case SHUTDOWN:
            case QUIT:
                Logger.log("L'autre client s'est déconnecté du serveur", Logger.LogType.INFO, "CLIENT");

                break;


            // Ajoute ici d'autres cas si tu attends d'autres messages (chat, score, etc.)

            default:
                Logger.log("Message non traité: " + msg.getCmd(), Logger.LogType.WARNING, "CLIENT");
                break;
        }
        return msg;
    }

/**
 * Envoie un message au serveur.
 *
 * @param msg Message à envoyer
 */
    public void send(GameMessage<?> msg) {
        try {
            out.writeObject(msg);
            out.flush();
            Logger.log("Message envoyé: " + msg.getCmd(), Logger.LogType.DEBUG, "CLIENT");
        } catch (IOException e) {
            Logger.log("Erreur envoi: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT");
            shutdown();
        }
    }

    private void shutdown() {
        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
        Logger.log("Client arrêté", Logger.LogType.INFO, "CLIENT");
    }

/**
 * Met fin à la connexion avec le serveur.
 *
 * @return Vrai si la déconnexion a réussi
 */
    public boolean quit() {
        GameMessage<?> msg;
        if (host) {
            msg = new GameMessage<>(CommandMessage.SHUTDOWN, id, "Déconnexion admin", null,GameStatus.WELCOME);
        } else {
            msg = new GameMessage<>(CommandMessage.QUIT, id, "Déconnexion", null,GameStatus.WELCOME);
        }
        app.gameState.gameStatus = GameStatus.WELCOME;
        app.gameState.connectedPlayers = 0;
        send(msg);
        Client.instance = null;
        Logger.log("Déconnexion du serveur, retour a l'etat normal:" + app.gameState.gameStatus, Logger.LogType.SUCCESS, "CLIENT");
        return true;
    }

/**
 * Lance la partie en envoyant un message au serveur pour démarrer la partie.
 */
    public void startGame() {
       GameMessage<?> msg = new GameMessage<>(CommandMessage.START_GAME, id, "Début de la partie", null,GameStatus.PLAYING);
       send(msg);

    }

/**
 * Récupère l'identifiant attribué par le serveur.
 *
 * @return L'identifiant du client
 */
    public int getId() {
        return id;
    }

/**
 * Récupère un message en attente de traitement du serveur.
 *
 * @return Le message reçu (ou null si la file d'attente est vide)
 */
    public GameMessage<?> pollResponse() {
        return responses.poll();
    }

/**
 * Indique si le client est l'administrateur.
 *
 * @return Vrai si le client est administrateur, faux sinon
 */
    public boolean getHost() {
        return host;
    }
}

