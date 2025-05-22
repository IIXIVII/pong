package Game.Ui;

import Common.GameStateDto;
import Common.GameStatus;
import Game.Client;
import Game.PongClientApp;
import Game.Ui.Style.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TitleScreen extends BaseScreen {
    private JButton hostButton;
    private JButton joinButton;
    private JTextField ipAddressField; // For join IP, styled for terminal
    private JLabel messageLabel;

    public TitleScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titlePart1 = new JLabel("Pong", SwingConstants.CENTER);
        UiStyle.styleLabel(titlePart1, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        add(titlePart1, gbc);

        JLabel titlePart2 = new JLabel("Super Ultimate Mega Champion Edition", SwingConstants.CENTER);
        UiStyle.styleLabel(titlePart2, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(titlePart2, gbc);

        messageLabel = new JLabel(" ", SwingConstants.CENTER); // Espace pour les messages
        UiStyle.styleLabel(messageLabel, UiStyle.FONT_PRIMARY, UiStyle.DEFAULT_COLOR);
        add(messageLabel, gbc);

        // HOST button
        hostButton = new JButton("HOST GAME");
        UiStyle.styleButton(hostButton);
        hostButton.addActionListener(e -> {
            this.onHostButtonPressed();
            app.switchToScreen(ScreenName.LOBBY);
        });
        gbc.insets = new Insets(10, 50, 10, 50);
        add(hostButton, gbc);

        // IP Address TextField
        ipAddressField = new JTextField("localhost");
        UiStyle.styleTextField(ipAddressField);
        gbc.insets = new Insets(10, 50, 0, 50);
        add(ipAddressField, gbc);

        // JOIN Button
        joinButton = new JButton("JOIN");
        UiStyle.styleButton(joinButton);
        joinButton.addActionListener(e -> {
            this.onJoinButtonPressed();
            app.switchToScreen(ScreenName.LOBBY);
        });
        gbc.insets = new Insets(0, 50, 20, 50);
        add(joinButton, gbc);
    }

    @Override
    public void updateState(GameStateDto newState) {
        super.updateState(newState); // Met à jour currentLocalState et repaint
        if (newState.currentStatus == GameStatus.CONNECTING) {
            messageLabel.setText(newState.message);
            hostButton.setEnabled(false);
            joinButton.setEnabled(false);
        } else if (newState.currentStatus == GameStatus.WELCOME || newState.currentStatus == GameStatus.ERROR) {
            messageLabel.setText(newState.message != null ? newState.message : " ");
            hostButton.setEnabled(true);
            joinButton.setEnabled(true);
        } else {
            messageLabel.setText(" "); // Vider si on est dans un autre état (normalement géré par changement d'écran)
        }
    }

    private void onHostButtonPressed() {
        app.client = Client.getInstance("localhost",8085,"daz",true);
    }
    private void onJoinButtonPressed() {
        app.client = Client.getInstance(this.ipAddressField.getText(),8085,"",false);
    }
}
