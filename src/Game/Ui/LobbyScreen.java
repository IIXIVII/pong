package Game.Ui;

import Common.GameStateDto;
import Common.GameStatus;
import Game.PongClientApp;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;

public class LobbyScreen extends BaseScreen {
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel playersLabel;
    private JButton startGameButton;
    private JButton backButton;

    public LobbyScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        titleLabel = new JLabel("GAME LOBBY");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        playersLabel = new JLabel("PLAYERS: 1/2");
        UiStyle.styleLabel(playersLabel, UiStyle.FONT_SUBTITLE, UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(10, 20, 10, 20);
        add(playersLabel, gbc);

        statusLabel = new JLabel("Waiting for opponent...");
        UiStyle.styleLabel(statusLabel, UiStyle.FONT_LABEL, UiStyle.ACCENT_COLOR);
        add(statusLabel, gbc);

        startGameButton = new JButton("START GAME");
        UiStyle.styleButton(startGameButton);
        startGameButton.addActionListener(e -> onstartGameButtonPressed());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 80, 10, 80); // Wider buttons
        add(startGameButton, gbc);

        backButton = new JButton("LEAVE LOBBY");
        UiStyle.styleButton(backButton);
        backButton.addActionListener(e -> {
            this.onbackButtonPressed();
        });
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make back button same width
        gbc.insets = new Insets(10, 80, 20, 80);
        add(backButton, gbc);
    }
    public void onstartGameButtonPressed(){
        app.client.startGame();
    }

    public void onbackButtonPressed(){
        app.client.quit();
        app.switchToScreen(ScreenName.TITLE);
    }


    @Override
    public void updateState(GameStateDto newState, GameStatus currentStatus) {
        super.updateState(newState, currentStatus); // Met à jour currentLocalState + repaint

        // Nombre de joueurs
        int playersConnected = newState.connectedPlayers; // ou newState.playerNames.size();
        playersLabel.setText("PLAYERS: " + playersConnected + "/2");

        // Statut d'attente
        if (playersConnected < 2) {
            statusLabel.setText("Waiting for opponent...");
            startGameButton.setEnabled(false);
        } else {
            statusLabel.setText("Ready to start!");
            startGameButton.setEnabled(true);
        }
    }

}