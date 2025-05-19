package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Common.Tools.Logger;
import Common.Messages.*;

public class ClientHandler implements Runnable {
    static int nb_client = 0;
    private int id;
    public boolean admin = false;


    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;

    private boolean running = false;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;


        try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);


            Logger.log("Client connecté : " + clientSocket.getInetAddress() + " numero " + ClientHandler.nb_client, Logger.LogType.SUCCESS,"server - handler");
        } catch (IOException e) {
            Logger.log("Erreur avec le client : " + e.getMessage(), Logger.LogType.ERROR,"server - handler");
        }

        this.id = ClientHandler.nb_client;
        ClientHandler.nb_client++;

        String message = in.readLine();
        server.processClientMessage(this,message);


    }

    public void sendMessage(GameMessage message) {
        if (out != null) {
            out.println(message.toJSONObject());
        }
    }

    @Override
    public void run() {
        this.running = true;
        try {
            String message;
            while (this.running && (message = in.readLine()) != null) {

                server.processClientMessage(this,message);


            }

        } catch (IOException e) {

            Logger.log("Erreur lors de la fermeture du socket client : " + e.getMessage(), Logger.LogType.ERROR,"server - handler");
        } finally {
            try {
                clientSocket.close();
                Logger.log("Socket client fermé", Logger.LogType.SUCCESS,"server - handler");
            } catch (IOException ignored) {}


        }
    }


    public int getId(){
        return this.id;
    }
    public boolean isAdmin(){return this.admin;}


    public void stop(){
        this.running = false;
    }


}
