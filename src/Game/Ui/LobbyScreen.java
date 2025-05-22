package Game.Ui;
import Common.Messages.QuitMessage;
import Game.PongClientApp;
import Game.Ui.Style.*;

import javax.swing.*;
import java.awt.*;

public class LobbyScreen extends BaseScreen {
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel playersLabel;
    private JButton startGameButton;
    private JButton backButton;

    public LobbyScreen(ScreenName screenName, PongClientApp manager) {
        super(screenName, manager);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        titleLabel = new JLabel("GAME LOBBY");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE.deriveFont(40f), UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        playersLabel = new JLabel("PLAYERS: 1/2");
        UiStyle.styleLabel(playersLabel, UiStyle.FONT_SUBTITLE.deriveFont(22f), UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(10, 20, 10, 20);
        add(playersLabel, gbc);

        statusLabel = new JLabel("Waiting for opponent...");
        UiStyle.styleLabel(statusLabel, UiStyle.FONT_LABEL.deriveFont(18f), UiStyle.ACCENT_COLOR);
        add(statusLabel, gbc);

        startGameButton = new JButton("START GAME");
        UiStyle.styleButton(startGameButton);
        startGameButton.addActionListener(e -> manager.switchToScreen(ScreenName.GAME));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 80, 10, 80); // Wider buttons
        add(startGameButton, gbc);

        backButton = new JButton("LEAVE LOBBY");
        UiStyle.styleButton(backButton);
        backButton.addActionListener(e -> {
            this.onbackButtonPressed();

            manager.switchToScreen(ScreenName.TITLE);
        });
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make back button same width
        gbc.insets = new Insets(10, 80, 20, 80);
        add(backButton, gbc);
    }

    public void onbackButtonPressed(){
        PongClientApp.client.quit();

    }


}