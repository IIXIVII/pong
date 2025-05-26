package Game.Ui;

import Common.GameConfig;
import Game.PongClientApp;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
public class EndingScreen extends BaseScreen {
    private JLabel titleLabel;
    private JLabel winnerMessageLabel;
    private JLabel finalScoreLabel; // New JLabel for the final score
    private JButton replayButton;
    private JButton menuButton;

    public EndingScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(15, 20, 15, 20); // Adjusted insets
        gbc.anchor = GridBagConstraints.CENTER;

        titleLabel = new JLabel("GAME OVER");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        add(titleLabel, gbc);

        winnerMessageLabel = new JLabel("Determining winner..."); // Placeholder
        UiStyle.styleLabel(winnerMessageLabel, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(winnerMessageLabel, gbc);

        finalScoreLabel = new JLabel("Score: 0 - 0"); // Placeholder
        UiStyle.styleLabel(finalScoreLabel, UiStyle.FONT_LABEL, UiStyle.DEFAULT_COLOR);
        add(finalScoreLabel, gbc);

        replayButton = new JButton("Replay");
        UiStyle.styleButton(replayButton);
        replayButton.addActionListener(this::requestReplayAction);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 100, 10, 100);
        add(replayButton, gbc);

        menuButton = new JButton("Back to Menu");
        UiStyle.styleButton(menuButton);
        menuButton.addActionListener(this::requestMenuAction);
        gbc.insets = new Insets(10, 100, 20, 100);
        add(menuButton, gbc);
    }
        @Override
        public void onShow () {
            if (currentLocalState != null) {
                String winnerText;
                if (currentLocalState.scorePlayer1 >= GameConfig.WINNING_SCORE) {
                    winnerText = "Player 1 Wins!";
                } else if (currentLocalState.scorePlayer2 >= GameConfig.WINNING_SCORE) {
                    winnerText = "Player 2 Wins!";
                } else {
                    winnerText = "Match Ended"; // Fallback if scores don't clearly indicate a winner
                }
                winnerMessageLabel.setText(winnerText);
                finalScoreLabel.setText("Final Score: " + currentLocalState.scorePlayer1 + " - " + currentLocalState.scorePlayer2);

                // Only host can initiate a replay
                replayButton.setEnabled(app.client.getHost());
            } else {
                winnerMessageLabel.setText("Error: Game state unavailable.");
                finalScoreLabel.setText("Score: -");
                replayButton.setEnabled(false);
            }
            super.onShow();
        }


        private void requestReplayAction (ActionEvent e){
            app.client.startGame();
        }

        private void requestMenuAction (ActionEvent e) {
            app.client.quit();
            app.switchToScreen(ScreenName.TITLE);
        }
}
