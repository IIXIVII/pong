

<<<<<<< HEAD
import Game.Client;
import Server.Server;

import java.util.Scanner;
=======
import src.Game.Client;
import src.Server.Server;
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538

public class GameApp {

    private Thread serverThread;

    public static void main(String[] args) {

        // Lancer le serveur dans un thread
        Thread serveurThread = new Thread(new Server());
        serveurThread.start();

<<<<<<< HEAD
=======


        Thread clientlocal = new Thread(new Client("localhost"));
        clientlocal.start();
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
        try {
            Thread.sleep(1000); // évite de boucler à vide
        } catch (InterruptedException e) {

        }

<<<<<<< HEAD
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

=======
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
>>>>>>> 4afd6c18e3d05ff972a1cc7ccd0a20f449fba538
    }
}
