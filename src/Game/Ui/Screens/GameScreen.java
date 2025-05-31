package Game.Ui.Screens;
import static Common.GameConfig.*;
import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.CommandMessage;
import Common.Messages.GameMessage;
import Common.PlayerInput;
import Game.PongClientApp;
import Game.Ui.BaseScreen;
import Game.Ui.ScreenName;
import Game.Ui.Style.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;


public class GameScreen extends BaseScreen {

    private boolean upArrowPressed = false;
    private boolean downArrowPressed = false;

    public GameScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e.getKeyCode());
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        PlayerInput.InputType inputType = null;

        if (keyCode == KeyEvent.VK_UP) {
            if (!upArrowPressed) { // Send only if state changes
                upArrowPressed = true;
                inputType = PlayerInput.InputType.MOVE_UP_PRESSED;
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
            if (!downArrowPressed) { // Send only if state changes
                downArrowPressed = true;
                inputType = PlayerInput.InputType.MOVE_DOWN_PRESSED;
            }
        }

        if (inputType != null) {
            app.client.send(new GameMessage<>(CommandMessage.ACTION_PLAYER, app.client.getId(),"", new PlayerInput(inputType, this.app.client.getId()), GameStatus.PLAYING));
        }
    }

    private void handleKeyRelease(int keyCode) {
        PlayerInput.InputType inputType = null;

        if (keyCode == KeyEvent.VK_UP) {
            if (upArrowPressed) { // Send only if state changes
                upArrowPressed = false;
                inputType = PlayerInput.InputType.MOVE_UP_RELEASED;
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
            if (downArrowPressed) { // Send only if state changes
                downArrowPressed = false;
                inputType = PlayerInput.InputType.MOVE_DOWN_RELEASED;
            }
        }

        if (inputType != null) {
            app.client.send(new GameMessage<>(CommandMessage.ACTION_PLAYER, app.client.getId(),"", new PlayerInput(inputType, this.app.client.getId()), GameStatus.PLAYING));
        }
    }

    public void tick() {
        repaint(); // Pour l'instant, juste rafraîchir l'écran
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Handles background and anti-aliasing

        if (currentLocalState == null) return; // Should not happen if initialized in BaseScreen

        Graphics2D g2d = (Graphics2D) g;

        // Draw common game elements (field, net)
        drawCourt(g2d);

        // Draw paddles
        g2d.setColor(UiStyle.DEFAULT_COLOR);
        g2d.fillRect(PADDLE_OFFSET_X, currentLocalState.player1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g2d.fillRect(SCREEN_WIDTH - PADDLE_OFFSET_X - PADDLE_WIDTH, currentLocalState.player2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball(s)
        if (currentLocalState.balls != null) {
            for (GameStateDto.BallPosition ballPos : currentLocalState.balls) {
                g2d.setColor(Color.WHITE); // Or a UiStyle color for ball
                g2d.fillOval(ballPos.x, ballPos.y, BALL_DIAMETER, BALL_DIAMETER);
            }
        }

        // Draw obstacles
        if (currentLocalState.obstacles != null) {
            g2d.setColor(Color.WHITE);
            for (GameStateDto.ObstaclePosition obstacle : currentLocalState.obstacles) {
                g2d.fillRect(obstacle.x, obstacle.y, OBSTACLE_SIZE, OBSTACLE_SIZE);
            }
        }

        // Draw scores
        drawScores(g2d);
    }

    private void drawCourt(Graphics2D g2d) {
        g2d.setColor(UiStyle.DEFAULT_COLOR);

        // Center line (solid)
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT);
        g2d.setStroke(oldStroke);

        // Dashed goal lines (indicative)
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 8}, 0);
        g2d.setStroke(dashed);
        int goalLineOffset = PADDLE_OFFSET_X / 2; // Closer to edge
        g2d.drawLine(goalLineOffset, 0, goalLineOffset, SCREEN_HEIGHT);
        g2d.drawLine(SCREEN_WIDTH - goalLineOffset, 0, SCREEN_WIDTH - goalLineOffset, SCREEN_HEIGHT);
        g2d.setStroke(new BasicStroke()); // Reset stroke

        // Center circle (decorative)
        int centerCircleDiameter = 40;
        Shape centerCircle = new Ellipse2D.Double(
                (double) SCREEN_WIDTH / 2 - (double) centerCircleDiameter / 2,
                (double) SCREEN_HEIGHT / 2 - (double) centerCircleDiameter / 2,
                centerCircleDiameter, centerCircleDiameter
        );
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(centerCircle);
    }

    private void drawScores(Graphics2D g2d) {
        g2d.setFont(UiStyle.FONT_TITLE);
        g2d.setColor(UiStyle.DEFAULT_COLOR);
        FontMetrics fm = g2d.getFontMetrics();

        String score1 = String.valueOf( currentLocalState.scorePlayer1);
        String score2 = String.valueOf(currentLocalState.scorePlayer2);

        g2d.drawString(score1, SCREEN_WIDTH / 4 - fm.stringWidth(score1) / 2, 60);
        g2d.drawString(score2, SCREEN_WIDTH * 3 / 4 - fm.stringWidth(score2) / 2, 60);
    }
}
