// Refactored Client.java
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

public class Client implements Runnable {

    private static Client instance;

    private int id = -1;
    private final String serverHost;
    private final int serverPort;
    private boolean host = false;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private PongClientApp app;

    private final BlockingQueue<GameMessage<?>> responses = new LinkedBlockingQueue<>();
    private volatile boolean running = false;

    private Client(PongClientApp app, String serverHost, int serverPort, String adminKey, boolean createServer) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.host = (adminKey != null && !adminKey.isEmpty());
        this.app = app;

        if (createServer) {
            new Thread(new Server(serverPort, adminKey)).start();
        }

        try {
            Thread.sleep(1000); // laisse le serveur démarrer
            connect(adminKey);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        new Thread(this).start();
    }

    public static synchronized Client getInstance(PongClientApp app,String host, int port, String adminKey, boolean createServer) {
        if (instance == null) {
            instance = new Client(app,host, port, adminKey, createServer);
        }
        return instance;
    }

    private void connect(String adminKey) throws IOException {
        socket = new Socket(serverHost, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        running = true;


        // Envoi de la connexion
        GameMessage<ConnectData> connectMsg = new GameMessage<>(CommandMessage.CONNECT, -1,
                "Demande de connexion", new ConnectData(adminKey, 0), GameStatus.WELCOME);
        send(connectMsg);

        // Lecture de la réponse de connexion
        try {
            @SuppressWarnings("unchecked")
            GameMessage<ConnectData> resp = (GameMessage<ConnectData>) in.readObject();
            this.id = resp.getId();
            this.host = resp.getData().getAdminKey().equals(adminKey);
            Logger.log("Connecté avec ID=" + id + " nbConnected=" + resp.getData().getNbConnected(),
                    Logger.LogType.INFO, "CLIENT");


        } catch (ClassNotFoundException e) {
            throw new IOException("Type de message inconnu", e);
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                @SuppressWarnings("unchecked")
                GameMessage<?> msg = (GameMessage<?>) in.readObject();
                Logger.log("Message reçu: " + msg.getCmd(), Logger.LogType.DEBUG, "CLIENT");
                responses.offer(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.log("Erreur réception: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT");
            shutdown();
        }
    }

    public GameMessage process(GameMessage msg){
        msg.log("client");
        switch (msg.getCmd()) {
            case CONNECT:
                Logger.log("passage au lobby",Logger.LogType.INFO,"CLIENT");
                break;
            case INFO_SERVER:
            case UPDATE_GAME_STATE:

                break;

            case START_GAME:
                Logger.log("Début de la partie reçu du serveur", Logger.LogType.INFO, "CLIENT");
                // Tu peux lancer une animation, reset les variables locales, etc.
                break;

            case SHUTDOWN:
            case QUIT:
                Logger.log("Déconnexion serveur", Logger.LogType.INFO, "CLIENT");
                this.quit();
                return msg;

            // Ajoute ici d'autres cas si tu attends d'autres messages (chat, score, etc.)

            default:
                Logger.log("Message non traité: " + msg.getCmd(), Logger.LogType.WARNING, "CLIENT");
                break;
        }
        return msg;
    }

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

    public boolean quit() {
        GameMessage<?> msg;
        if (host) {
            msg = new GameMessage<>(CommandMessage.SHUTDOWN, id, "Déconnexion admin", null,GameStatus.WELCOME);
        } else {
            msg = new GameMessage<>(CommandMessage.QUIT, id, "Déconnexion", null,GameStatus.WELCOME);
        }
        send(msg);
        Client.instance = null;
        return true;
    }

    public void startGame() {
       GameMessage<?> msg = new GameMessage<>(CommandMessage.START_GAME, id, "Début de la partie", null,GameStatus.PLAYING);
       send(msg);

    }

    public int getId() {
        return id;
    }

    public GameMessage<?> pollResponse() {
        return responses.poll();
    }

    public boolean getHost() {
        return host;
    }
}
