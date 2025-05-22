// Refactored Client.java
package Game;

import Common.Messages.ConnectData;
import Common.Messages.GameMessage;
import Common.Messages.CommandMessage;
import Common.Tools.Logger;
import Server.Server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

    private static Client instance;

    private int id = -1;
    private final String serverHost;
    private final int serverPort;
    private boolean admin = false;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final BlockingQueue<GameMessage<?>> responses = new LinkedBlockingQueue<>();
    private volatile boolean running = false;

    private Client(String serverHost, int serverPort, String adminKey, boolean createServer) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.admin = (adminKey != null && !adminKey.isEmpty());

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

    public static synchronized Client getInstance(String host, int port, String adminKey, boolean createServer) {
        if (instance == null) {
            instance = new Client(host, port, adminKey, createServer);
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
                "Demande de connexion", new ConnectData(adminKey, 0));
        send(connectMsg);

        // Lecture de la réponse de connexion
        try {
            @SuppressWarnings("unchecked")
            GameMessage<ConnectData> resp = (GameMessage<ConnectData>) in.readObject();
            this.id = resp.getId();
            this.admin = resp.getData().getAdminKey().equals(adminKey);
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
                if (msg.getCmd() == CommandMessage.SHUTDOWN) {
                    shutdown();
                } else if (msg.getCmd() == CommandMessage.QUIT) {
                    shutdown();
                } else if (msg.getCmd() == CommandMessage.START_GAME){

                } else {
                    responses.offer(msg);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.log("Erreur réception: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT");
            shutdown();
        }
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
        if (admin) {
            msg = new GameMessage<>(CommandMessage.SHUTDOWN, id, "Déconnexion admin", null);
        } else {
            msg = new GameMessage<>(CommandMessage.QUIT, id, "Déconnexion", null);
        }
        send(msg);
        Client.instance = null;
        return true;
    }

    public int getId() {
        return id;
    }

    public GameMessage<?> pollResponse() {
        return responses.poll();
    }
}
