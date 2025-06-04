package Game.Ui;

import Common.GameStateDto;
import Common.Tools.Logger;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Gère la navigation entre les différents écrans (JPanel) de l'application.
 * Utilise un CardLayout pour afficher un seul écran à la fois.
 */
public class ScreenManager {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Map<ScreenName, Screen> screens; // Map associant les noms d'écran à leurs instances
    private Screen currentScreen; // L'écran actuellement visible

    /**
     * Constructeur du ScreenManager.
     * @param frame La JFrame principale de l'application.
     */
    public ScreenManager(JFrame frame) {
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.mainPanel.setBackground(UiStyle.BACKGROUND_COLOR);
        this.screens = new HashMap<>();
        frame.add(mainPanel);
    }

    /**
     * Enregistre un nouvel écran dans la navigation
     * L'écran est ajouté au CardLayout.
     * @param screen L'instance de l'écran à enregistrer.
     */
    public void register(Screen screen) {
        screens.put(screen.getScreenName(), screen);
        mainPanel.add(screen.getScreenName().toString(), screen.getPanel());
    }

    /**
     * Affiche l'écran spécifié par son nom.
     * Gère les appels aux méthodes onHide() de l'ancien écran et onShow() du nouvel écran.
     * @param screenName Le nom de l'écran à afficher.
     */
    public void switchTo(ScreenName screenName) {
        Screen nextScreen = screens.get(screenName);
        if (nextScreen == null) {
            Logger.log("Screen with name " + screenName + "not found.", Logger.LogType.ERROR, "SCREEN MANAGER");
            return;
        }

        if (currentScreen != null && currentScreen != nextScreen) {
            currentScreen.onHide();
        }

        cardLayout.show(mainPanel, screenName.toString());
        currentScreen = nextScreen;
        currentScreen.onShow();
        // Demander le focus pour le panel après un court délai pour s'assurer qu'il est visible et prêt
        SwingUtilities.invokeLater(() -> {
            if (currentScreen != null && currentScreen.getPanel() != null) {
                currentScreen.getPanel().requestFocusInWindow();
            }
        });
    }

    /**
     * Met à jour l'état de tous les écrans enregistrés.
     * Utile pour propager les changements d'état du jeu à toutes les vues.
     * @param newState Le nouvel état du jeu.
     */
    public void updateScreens(GameStateDto newState) {
        for (Screen screen : screens.values()) {
            screen.updateState(newState);
        }
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }
}
