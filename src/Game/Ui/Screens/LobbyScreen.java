package Game.Ui.Screens;

import Common.GameStateDto;
import Game.PongClientApp;
import Game.Ui.BaseScreen;
import Game.Ui.ScreenName;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Écran du lobby où les joueurs attendent le début de la partie.
 * Affiche le nombre de joueurs connectés et permet à l'hôte de démarrer le jeu.
 */
public class LobbyScreen extends BaseScreen {
    private final JLabel statusLabel;
    private final JLabel playersLabel;
    private final JButton startGameButton;

    public LobbyScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Titre
        JLabel titleLabel = new JLabel("GAME LOBBY");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        // Joueurs connectés
        playersLabel = new JLabel("PLAYERS: 0/2");
        UiStyle.styleLabel(playersLabel, UiStyle.FONT_SUBTITLE, UiStyle.DEFAULT_COLOR);
        gbc.insets = new Insets(10, 20, 10, 20);
        add(playersLabel, gbc);

        // Statut
        statusLabel = new JLabel("Waiting for opponent...");
        UiStyle.styleLabel(statusLabel, UiStyle.FONT_LABEL, UiStyle.ACCENT_COLOR);
        add(statusLabel, gbc);

        // Bouton démarrer
        startGameButton = new JButton("START GAME");
        UiStyle.styleButton(startGameButton);
        startGameButton.addActionListener(this::onstartGameButtonPressed);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startGameButton.setEnabled(false);
        gbc.insets = new Insets(20, 80, 10, 80);
        add(startGameButton, gbc);

        // Bouton quitter
        JButton backButton = new JButton("LEAVE LOBBY");
        UiStyle.styleButton(backButton);
        backButton.addActionListener(this::onbackButtonPressed);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 80, 20, 80);
        add(backButton, gbc);
    }

    /**
     * Gère l'action du bouton "Démarrer Partie".
     */
    private void onstartGameButtonPressed(ActionEvent e) {
        app.client.startGame();
    }

    /**
     * Gère l'action du bouton "Quitter Salon".
     * Déconnecte le client et retourne à l'écran titre.
     */
    private void onbackButtonPressed(ActionEvent e) {
        app.client.quit();
    }

    @Override
    public void onShow() {
        updateUIBasedOnState(state);
        super.onShow();
    }

    @Override
    public void updateState(GameStateDto newState) {
        super.updateState(newState);
        updateUIBasedOnState(newState);
    }

    private void updateUIBasedOnState(GameStateDto newState) {
        super.updateState(newState);

        // Nombre de joueurs
        int playersConnected = newState.getConnectedPlayers();
        playersLabel.setText("PLAYERS: " + playersConnected + "/2");
        if (app.client.getHost()) {
            startGameButton.setEnabled(true);
        }
        // Statut d'attente
        if (playersConnected < 2) {
            statusLabel.setText("Waiting for opponent...");
        } else {
            statusLabel.setText("Ready ! Waiting for host to start the game...");
        }
    }
}