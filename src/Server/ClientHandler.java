// Refactored ClientHandler.java
package Server;

import Common.Messages.GameMessage;
import Common.Tools.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;
    private int id;
    private boolean isAdmin = false;


    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (running) {
                GameMessage<?> msg = (GameMessage<?>) in.readObject();
                server.process(this, msg);
            }
        } catch (IOException e) {
            Logger.log("Erreur d'entrée/sortie : " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
        } catch (ClassNotFoundException e) {
            Logger.log("Classe non trouvée lors de la désérialisation : " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
        } finally {
            stop();
        }
    }

    public void send(GameMessage<?> message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            Logger.log("Erreur envoi message: " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
