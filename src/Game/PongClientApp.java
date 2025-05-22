package Game;

import Common.GameStateDto;
import Common.Messages.GameMessage;
import Game.Ui.*;
import Game.Ui.Style.*;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Game.Ui.ScreenName.*;

public class PongClientApp {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Map<ScreenName, Screen> screens;
    private Screen currentScreenName;
    public Client client;


    public PongClientApp() {
        screens = new HashMap<>();
        GameMessage.debug = true;

        frame = new JFrame("PONG");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //TODO Informer le serveur d'une déconnexion
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
        } else {
            System.err.println("Error: Screen with name '" + screens + "' not found.");
        }
    }

    // Callback pour les mises à jour du serveur
    private void handleServerUpdate(GameStateDto newState) {
        SwingUtilities.invokeLater(() -> { // Mises à jour de l'UI sur le thread EDT
            // Mettre à jour les données de chaque écran
            for (Screen screen : screens.values()) {
                screen.updateState(newState);
            }

            // Changer d'écran en fonction du nouvel état du jeu
            switch (newState.currentStatus) {
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
            }
        });
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongClientApp::new);
    }
}
