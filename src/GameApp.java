

import Game.Client;
import Server.Server;

import java.util.Scanner;

public class GameApp {

    private Thread serverThread;

    public static void main(String[] args) {

        // Lancer le serveur dans un thread
        Thread serveurThread = new Thread(new Server());
        serveurThread.start();

        try {
            Thread.sleep(1000); // évite de boucler à vide
        } catch (InterruptedException e) {

        }

        Client clientlocal = new Client("localhost");



        new Thread(clientlocal).start();
        try {
            Thread.sleep(1000); // évite de boucler à vide
        } catch (InterruptedException e) {

        }
        Client clientlocal2 = new Client("localhost");
        new Thread(clientlocal2).start();


        Scanner scanner = new Scanner(System.in);
        String input;

        int v = 0;

        do {
            System.out.println("Appuie uniquement sur Entrée pour continuer...");
            input = scanner.nextLine();
            clientlocal.send("lol" + input);
            v++;



        } while (v != 2);
        clientlocal.send("shutdown");

    }
}
