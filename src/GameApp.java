

import Common.Messages.*;
import Game.Client;
import Server.Server;

import java.util.Scanner;


public class GameApp {

    private Thread serverThread;

    public static void main(String[] args) {

        // Lancer le serveur dans un thread
        Thread serveurThread = new Thread(new Server(8085,"lol"));
        serveurThread.start();

        GameMessage.debug = true;
        try {
            Thread.sleep(1000); // évite de boucler à vide
        } catch (InterruptedException e) {

        }
        Client clientlocal = new Client("localhost",8085, "lol");



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

            v++;




        } while (v != 2);

        clientlocal.send(new ShutdownMessage(clientlocal.getId(), "arret du server demandé"));

    }
}
