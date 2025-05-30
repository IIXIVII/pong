package Game.Ui;

import Common.GameStateDto;
import Common.Tools.Logger;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Map<ScreenName, Screen> screens;
    private Screen currentScreen;

    public ScreenManager(JFrame frame) {
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.mainPanel.setBackground(UiStyle.BACKGROUND_COLOR);
        this.screens = new HashMap<>();
        frame.add(mainPanel);
    }

    public void register(Screen screen) {
        screens.put(screen.getScreenName(), screen);
        mainPanel.add(screen.getScreenName().toString(), screen.getPanel());
    }

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
        // Request focus after a short delay to ensure the panel is visible and ready
        SwingUtilities.invokeLater(() -> {
            if (currentScreen != null && currentScreen.getPanel() != null) {
                currentScreen.getPanel().requestFocusInWindow();
            }
        });
    }

    public void updateScreens(GameStateDto newState) {
        for (Screen screen : screens.values()) {
            screen.updateState(newState);
        }
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }
}
