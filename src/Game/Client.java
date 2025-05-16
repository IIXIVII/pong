package src.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    String serverHost ;
    Integer serverPort;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Constructeur avec paramètres
    public Client(String serverHost, Integer serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    // Constructeur avec port par défaut
    public Client(String serverHost) {
        this(serverHost, 8085); // appel au constructeur principal
    }
    @Override
    public void run(){

        try {
            this.connect();

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("📥 Message reçu : " + message);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }




    private void connect() throws IOException {
        socket = new Socket(serverHost, serverPort);
        System.out.println("✅ Connecté au serveur " + serverHost + ":" + serverPort);

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true); // auto-flush
    }

}
