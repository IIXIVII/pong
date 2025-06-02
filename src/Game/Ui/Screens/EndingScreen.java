package Game.Ui.Screens;

import Common.GameConfig;
import Game.PongClientApp;
import Game.Ui.BaseScreen;
import Game.Ui.ScreenName;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * Écran affiché à la fin d'une partie.
 */
public class EndingScreen extends BaseScreen {
    private final JLabel winnerMessageLabel;
    private final JLabel finalScoreLabel;
    private final JButton replayButton;

    public EndingScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(15, 20, 15, 20); // Adjusted insets
        gbc.anchor = GridBagConstraints.CENTER;

        // Titre
        JLabel titleLabel = new JLabel("GAME OVER");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        add(titleLabel, gbc);

        // Joueur gagnant
        winnerMessageLabel = new JLabel("Determining winner..."); // Placeholder
        UiStyle.styleLabel(winnerMessageLabel, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(winnerMessageLabel, gbc);

        // Score final
        finalScoreLabel = new JLabel("Score: 0 - 0"); // Placeholder
        UiStyle.styleLabel(finalScoreLabel, UiStyle.FONT_LABEL, UiStyle.DEFAULT_COLOR);
        add(finalScoreLabel, gbc);

        // Bouton rejouer
        replayButton = new JButton("Replay");
        UiStyle.styleButton(replayButton);
        replayButton.addActionListener(this::onReplayButtonPressed);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 100, 10, 100);
        add(replayButton, gbc);

        // Bouton menu
        JButton menuButton = new JButton("Back to Menu");
        UiStyle.styleButton(menuButton);
        menuButton.addActionListener(this::onMenuButtonPressed);
        gbc.insets = new Insets(10, 100, 20, 100);
        add(menuButton, gbc);
    }

    /**
     * Mise à jour des informations de l'écran de fin de partie une seule fois au moment de son affichage
     */
    @Override
    public void onShow () {
        if (state != null) {
            String winnerText;
            if (state.getScorePlayer1() >= GameConfig.WINNING_SCORE) {
                winnerText = "Player 1 Wins!";
            } else if (state.getScorePlayer2() >= GameConfig.WINNING_SCORE) {
                winnerText = "Player 2 Wins!";
            } else {
                winnerText = "Match Ended";
            }
            winnerMessageLabel.setText(winnerText);
            finalScoreLabel.setText("Final Score: " + state.getScorePlayer1() + " - " + state.getScorePlayer2());

            // Seulement l'hôte peut relancer la partie
            replayButton.setEnabled(app.client.getHost());
        } else {
            winnerMessageLabel.setText("Error: Game state unavailable.");
            finalScoreLabel.setText("Score: -");
            replayButton.setEnabled(false);
        }
        super.onShow();
    }

    /**
     * Gère l'action du bouton "Rejouer".
     * Demande au client de démarrer une nouvelle partie.
     */
    private void onReplayButtonPressed (ActionEvent e) {
        app.client.startGame();
    }

    /**
     * Gère l'action du bouton "Retour au Menu".
     * Déconnecte le client et retourne à l'écran titre.
     */
    private void onMenuButtonPressed (ActionEvent e) {
        app.client.quit();
        app.switchToScreen(ScreenName.TITLE);
    }
}
