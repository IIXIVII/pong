package Game;

import org.json.JSONException;
import org.json.JSONObject;
import Common.Logger;
import Common.messages.ConnectMessage;
import Common.messages.GameMessage;
import Common.messages.ShutdownMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private int id = -1;
    String serverHost ;
    Integer serverPort;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private boolean running = false;

    // Constructeur avec paramètres
    public Client(String serverHost, Integer serverPort, String adminkey) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        try {
            this.connect(adminkey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Constructeur avec port par défaut
    public Client(String serverHost) {
        this(serverHost, 8085,""); // appel au constructeur principal
    }
    @Override
    public void run(){

        try {


            String message;
            while ((message = in.readLine()) != null && running) {
                this.processClientMessage(message);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Logger.log("Arret de l'ecouteur",Logger.LogType.INFO,"CLIENT");

    }


    public void send(GameMessage msg) {
        if (out != null) {
            out.println(msg.toJSONObject());
        }
    }


    public void processClientMessage(String rawMessage){
        GameMessage deserializedMessage = null;

        try {
            JSONObject jsonMsg = new JSONObject(rawMessage);
            String cmd = jsonMsg.getString("cmd");

            switch (cmd){
                case ShutdownMessage.CMD:

                    deserializedMessage = new ShutdownMessage(jsonMsg,this.id);
                    deserializedMessage.log("CLIENT");

                    this.running = false;

                default:
                    Logger.log("Commande client inconnue: " + cmd + " de Serveur ", Logger.LogType.WARNING, "SERVER");

                    return;
            }

        } catch (JSONException e) {
            Logger.log("Erreur JSON lors du traitement du message de Serveur : " + e.getMessage() + " | Brut: " + rawMessage, Logger.LogType.ERROR, "CLIENT");
        } catch (Exception e) { // Pour attraper toute autre erreur non prévue
            Logger.log("Erreur inattendue en traitant le message de Serveur : " + e.getMessage(), Logger.LogType.ERROR, "CLIENT");
            e.printStackTrace();
        }
    }



    private void connect(String adminKey) throws IOException {
        socket = new Socket(serverHost, serverPort);
        this.running = true;

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true); // auto-flush

        this.send(new ConnectMessage(adminKey));

        ConnectMessage connect = new ConnectMessage(new JSONObject(in.readLine()),-1);
        this.id = connect.getId();
        System.out.println(this.id);
        connect.log("CLIENT");

    }

    public int getId(){
        return this.id;
    }

}
