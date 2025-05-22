package Game;

import Common.Messages.QuitMessage;
import Server.Server;
import org.json.JSONException;
import org.json.JSONObject;
import Common.Tools.Logger;
import Common.Messages.ConnectMessage;
import Common.Messages.GameMessage;
import Common.Messages.ShutdownMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private static Client instance;

    private int id = -1;
    String serverHost ;
    Integer serverPort;
    Integer nbconnectedserver = 0;

    private boolean admin = false;


    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ArrayList<GameMessage> channelResponse = new ArrayList<>();

    private boolean running = false;

    // Constructeur avec paramètres
    private Client(String serverHost, Integer serverPort, String adminkey, boolean createserver) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        if (adminkey != "") this.admin = true;

        if (createserver) new Thread(new Server(serverPort,adminkey)).start();



        try {

            Thread.sleep(1000);
            this.connect(adminkey);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }





        new Thread(PongClientApp.client).start();
    }

    // Méthode pour obtenir l'instance unique
    public static synchronized Client getInstance(String serverHost, Integer serverPort, String adminKey, boolean createServer) throws IOException {
        if (instance == null) {
            instance = new Client(serverHost, serverPort, adminKey,createServer);
        }
        return instance;
    }

    @Override
    public void run(){

        try {


            String message;
            while ((message = in.readLine()) != null && running) {
                this.processServerReponse(message);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Logger.log("Arret de l'ecouteur",Logger.LogType.INFO,"CLIENT");

    }

    private GameMessage findMessageByCmd(String cmd) {
        for (GameMessage msg : channelResponse) {
            if (msg.getCmd().equals(cmd)) {
                return msg; // trouvé
            }
        }
        return null; // pas trouvé
    }


    public void send(GameMessage msg) {
        if (out != null) {
            out.println(msg.toJSONObject());
        }
    }


    private void processServerReponse(String rawMessage){
        GameMessage deserializedMessage = null;

        try {
            JSONObject jsonMsg = new JSONObject(rawMessage);
            String cmd = jsonMsg.getString("cmd");

            switch (cmd){
                case ShutdownMessage.CMD:

                    deserializedMessage = new ShutdownMessage(jsonMsg,this.id);
                    deserializedMessage.log("CLIENT");


                    this.running = false;
                    return;
                case QuitMessage.CMD:
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
        this.nbconnectedserver = connect.getNbconnected();
        connect.log("CLIENT");

    }





    public boolean quit(){
        GameMessage message;
        if (this.admin){
            message = new ShutdownMessage(this.id,"Déconnection de l'admin");
        } else {
            message = new QuitMessage(this.id,"Déconnection demandé");
        }

        this.send(message);

        while (running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public int getId(){
        return this.id;
    }

}
