package Game;

import Common.GameConfig.*;
import Common.GameStateDto;
import Common.Messages.GameMessage;
import Common.Tools.Logger;
import Game.Ui.*;
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
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Map<ScreenName, Screen> screens;
    private Screen currentScreenName;
    public Client client;

    private GameStateDto gameState = new GameStateDto();
    private Timer gameLoopTimer;


    public PongClientApp() {
        screens = new HashMap<>();
        GameMessage.debug = true;

        frame = new JFrame("PONG");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.quit();
                System.out.println("Application closing.");
                frame.dispose();
                System.exit(0);
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UiStyle.BACKGROUND_COLOR);

        // Initialize and add all screens
        addScreen(new TitleScreen(TITLE, this));
        addScreen(new LobbyScreen(LOBBY, this));
        addScreen(new GameScreen(GAME, this));
        addScreen(new EndingScreen(ENDING, this));

        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Show the initial screen
        switchToScreen(TITLE);
    }

    private void addScreen(Screen screen) {
        screens.put(screen.getScreenName(), screen);
        mainPanel.add(screen.getScreenName().toString(), screen.getPanel());
    }

    public void switchToScreen(ScreenName screenName) {
        currentScreenName = this.screens.get(screenName);

        if (currentScreenName != null) {
            cardLayout.show(mainPanel, screenName.toString());
            currentScreenName.getPanel().requestFocusInWindow();

            if (currentScreenName instanceof GameScreen ) {
                LocalDateTime now = LocalDateTime.now();

                if (this.gameState.StartTargetTime == null) {
                    Logger.log("StartTargetTime est null, lancement direct du jeu.", Logger.LogType.ERROR, "CLIENTAPP");
                    startGameLoop();
                    return;
                }

                long totalMillis = java.time.Duration.between(now, this.gameState.StartTargetTime).toMillis();

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

            } else if (currentScreenName instanceof LobbyScreen ) {

                startGameLoop();

            } else {
                stopGameLoop();
            }
        } else {
            System.err.println("Error: Screen with name '" + screenName + "' not found.");
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

    private void gameLoopTick() {
        if (currentScreenName instanceof GameScreen) {
            ((GameScreen) currentScreenName).tick();
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
                Logger.log("Received GameState - StartTargetTime: " + newState.StartTargetTime, Logger.LogType.DEBUG, "CLIENTAPP");
                Logger.log("Received GameState - Full: " + newState.toString(), Logger.LogType.DEBUG, "CLIENTAPP");

                gameState = newState;
                finalMsg1.log("CLIENTAPP");

                for (Screen screen : screens.values()) {
                    screen.updateState(newState, finalMsg1.currentStatus);
                }
            }






            if (finalMsg1 != null){
            // Changer d'écran en fonction du nouvel état du jeu
            switch (finalMsg1.currentStatus) {
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
