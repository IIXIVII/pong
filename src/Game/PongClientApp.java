package Game;

import Common.GameConfig.*;
import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.GameMessage;
import Common.Tools.Logger;
import Game.Ui.*;
import Game.Ui.Screens.EndingScreen;
import Game.Ui.Screens.GameScreen;
import Game.Ui.Screens.LobbyScreen;
import Game.Ui.Screens.TitleScreen;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static Common.GameConfig.SERVER_TPS;
import static Game.Ui.ScreenName.*;



public class PongClientApp {
    private JFrame frame;
    public ScreenManager screenManager;
    public Client client;

    private GameStateDto gameState = new GameStateDto();
    private Timer gameLoopTimer;


    public PongClientApp() {
        frame = new JFrame("PONG");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.quit();
                }
                System.out.println("Application closing.");
                frame.dispose();
                System.exit(0);
            }
        });

        this.screenManager = new ScreenManager(frame);
        initializeAllScreens();

        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.screenManager.switchTo(TITLE);
    }

    private void initializeAllScreens() {
        screenManager.register(new TitleScreen(ScreenName.TITLE, this));
        screenManager.register(new LobbyScreen(ScreenName.LOBBY, this));
        screenManager.register(new GameScreen(ScreenName.GAME, this));
        screenManager.register(new EndingScreen(ScreenName.ENDING, this));
    }

    /**
     * Initialise le client réseau.
     * @param host L'adresse du serveur.
     * @param port Le port du serveur.
     * @param adminKey La clé d'admin (si hôte).
     * @param createServer True si ce client doit aussi créer le serveur.
     * @return true si l'initialisation a réussi, false sinon.
     */
    public boolean initializeClient(String host, int port, String adminKey, boolean createServer) {
        if (this.client != null) {
            this.client.quit();
        }
        try {
            this.client = Client.getInstance(this, host, port, adminKey, createServer);
            this.gameState.setGameStatus(GameStatus.CONNECTING); // Mettre à jour l'état local
            return true;
        } catch (RuntimeException e) {
            Logger.log("Échec de l'initialisation du client: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT_APP");
            this.client = null;
            this.gameState.setGameStatus(GameStatus.ERROR);
            this.gameState.setMessage("Erreur: " + e.getMessage());
            return false;
        }
    }

    public void switchToScreen(ScreenName screenName) {
        this.screenManager.switchTo(screenName);
        Screen currentScreen = this.screenManager.getCurrentScreen();
        if (currentScreen instanceof GameScreen ) {
            LocalDateTime now = LocalDateTime.now();

            if (this.gameState.getStartTargetTime() == null) {
                Logger.log("StartTargetTime est null, lancement direct du jeu.", Logger.LogType.ERROR, "CLIENTAPP");
                startGameLoop();
                return;
            }

            long totalMillis = java.time.Duration.between(now, this.gameState.getStartTargetTime()).toMillis();

            if (totalMillis <= 0) {
                startGameLoop(); // déjà en retard, on commence directement
                return;
            }

            final int[] countdownSeconds = {(int) (totalMillis / 1000)};  // Convertir millisecondes en secondes

            Logger.log("Début du compte à rebours : " + countdownSeconds[0] + "s", Logger.LogType.INFO, "CLIENTAPP");

            Timer countdownTimer = new Timer(1000, null);

            countdownTimer.addActionListener(e -> {
                if (countdownSeconds[0] > 0) {
                    System.out.println("Début dans " + countdownSeconds[0] + "s");
                    countdownSeconds[0]--;
                } else {
                    countdownTimer.stop();
                    System.out.println("GO !");
                    startGameLoop();
                }
            });

            countdownTimer.start();

        } else if (currentScreen instanceof LobbyScreen ) {

            startGameLoop();

        } else {
            stopGameLoop();
        }
    }

    private void startGameLoop() {
        gameLoopTimer = new Timer(SERVER_TPS, e -> handleServerUpdate());
        Logger.log("Client Prepare la loop",Logger.LogType.DEBUG,"CLIENTAPP");
        gameLoopTimer.start();
        Logger.log("Client execute la loop",Logger.LogType.DEBUG,"CLIENTAPP");
    }

    private void stopGameLoop() {
        if (gameLoopTimer != null) {
            Logger.log("Client arrete la loop",Logger.LogType.DEBUG,"CLIENTAPP");
            gameLoopTimer.stop();
        }
    }

    // Callback pour les mises à jour du serveur
    private void handleServerUpdate() {


        GameMessage<?> msg = client.pollResponse();
        GameMessage<?> finalMsg = null;

        while (msg != null) {
            client.process(msg);
            finalMsg = msg;
            msg =  client.pollResponse();
        }







        GameMessage<?> finalMsg1 = finalMsg;
        SwingUtilities.invokeLater(() -> { // Mises à jour de l'UI sur le thread EDT

            // Mettre à jour les données de chaque écran
            if (finalMsg1 != null && finalMsg1.getData() instanceof GameStateDto) {
                GameStateDto newState = (GameStateDto) finalMsg1.getData();

                // DEBUG: Log the received state
                Logger.log("Received GameState - StartTargetTime: " + newState.getStartTargetTime(), Logger.LogType.DEBUG, "CLIENTAPP");
                Logger.log("Received GameState - Full: " + newState.toString(), Logger.LogType.DEBUG, "CLIENTAPP");

                gameState = newState;
                finalMsg1.log("CLIENTAPP");

                screenManager.updateScreens(gameState);
            }

            if (finalMsg1 != null){
            // Changer d'écran en fonction du nouvel état du jeu
            switch (finalMsg1.getCurrentStatus()) {
                case WELCOME: // Si le serveur nous remet en Welcome (rare)
                    switchToScreen(TITLE);
                    break;
                case LOBBY_WAITING:
                case LOBBY_READY_TO_START:
                    switchToScreen(LOBBY);
                    break;
                case PLAYING:

                    switchToScreen(GAME);
                    break;
                case GAME_OVER:
                    switchToScreen(ENDING);
                    break;
                default:
                    // Si on est en CONNECTING, on reste sur l'écran actuel (Welcome ou Lobby)
                    // jusqu'à ce que le serveur confirme un nouvel état.
                    // Le WelcomeScreen gère l'affichage de "Connecting..."
                    break;
            }}
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongClientApp::new);
    }
}
