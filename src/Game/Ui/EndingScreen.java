package Game.Ui;

import Game.PongClientApp;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
public class EndingScreen extends BaseScreen {
    private JLabel titleLabel;
    private JLabel winnerLabel;
    private JLabel finalScoreLabel; // New JLabel for the final score
    private JButton replayButton;
    private JButton menuButton;

    private String displayWinnerMessage = "The winner is Player";
    private String displayFinalScore = "Score: 0 - 0";
    private boolean displayCanReplay = false;

    public EndingScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(15, 20, 15, 20); // Adjusted insets
        gbc.anchor = GridBagConstraints.CENTER;

        titleLabel = new JLabel("GAME OVER");
        UiStyle.styleLabel(titleLabel, UiStyle.FONT_TITLE , UiStyle.DEFAULT_COLOR);
        add(titleLabel, gbc);

        winnerLabel = new JLabel(displayWinnerMessage); // Initial text
        UiStyle.styleLabel(winnerLabel, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(winnerLabel, gbc);

        finalScoreLabel = new JLabel(displayFinalScore);
        UiStyle.styleLabel(finalScoreLabel, UiStyle.FONT_LABEL, UiStyle.DEFAULT_COLOR);
        add(finalScoreLabel, gbc);


        replayButton = new JButton("Replay");
        UiStyle.styleButton(replayButton);
        replayButton.addActionListener(this::requestReplayAction);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 100, 10, 100); // Wider buttons
        add(replayButton, gbc);

        menuButton = new JButton("Menu");
        UiStyle.styleButton(menuButton);
        menuButton.addActionListener(this::requestMenuAction);
        gbc.insets = new Insets(10, 100, 20, 100);
        add(menuButton, gbc);
    }
        @Override
        public void onShow () {
            super.onShow();
            String winner = currentLocalState.scorePlayer1 > currentLocalState.scorePlayer2 ? "1" : "2";
            this.displayWinnerMessage += winner;
            this.displayFinalScore = "Final Score: " + currentLocalState.scorePlayer1 + " - " + currentLocalState.scorePlayer2;
            this.displayCanReplay = app.client.getHost();

            winnerLabel.setText(displayWinnerMessage);
            finalScoreLabel.setText(displayFinalScore);
            replayButton.setEnabled(displayCanReplay);
            repaint();
        }


        private void requestReplayAction (ActionEvent e){
            app.client.startGame();
        }

        private void requestMenuAction (ActionEvent e){
            app.client.quit();
            app.switchToScreen(ScreenName.TITLE);
        }
}
