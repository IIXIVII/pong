

import src.Game.Client;
import src.Server.Server;

public class GameApp {

    private Thread serverThread;

    public static void main(String[] args) {

        // Lancer le serveur dans un thread
        Thread serveurThread = new Thread(new Server());
        serveurThread.start();



        Thread clientlocal = new Thread(new Client("localhost"));
        clientlocal.start();
        try {
            Thread.sleep(1000); // évite de boucler à vide
        } catch (InterruptedException e) {

        }

        Thread clientlocal2 = new Thread(new Client("localhost"));
        clientlocal2.start();

        // Bloque le main thread
        while (true) {
            try {
                Thread.sleep(1000); // évite de boucler à vide
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
