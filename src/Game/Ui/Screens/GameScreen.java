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

/**
 * Écran de jeu où une partie se déroule
 * Gère l'affichage des éléments du jeu (paddles, balle, score) et la capture des entrées du joueur.
 */
public class GameScreen extends BaseScreen {
    public GameScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode(), true);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyPress(e.getKeyCode(), false);
            }
        });
    }

    /**
     * Gère l'appui sur une touche.
     * Envoie un message au serveur si une touche de mouvement est pressée ou relâchée.
     * @param keyCode Le code de la touche pressée.
     * @param pressed Indique si la touche est préssée
     */
    private void handleKeyPress(int keyCode, boolean pressed) {
        PlayerInput.InputType inputType = null;
        if (pressed) {
            inputType = switch (keyCode) {
                case KeyEvent.VK_UP -> PlayerInput.InputType.MOVE_UP;
                case KeyEvent.VK_DOWN -> PlayerInput.InputType.MOVE_DOWN;
                default -> inputType;
            };
        } else {
            inputType = switch (keyCode) {
                case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> PlayerInput.InputType.STOP_MOVE;
                default -> inputType;
            };
        }

        if (inputType != null && app.client != null) {
            app.client.send(new GameMessage<>(CommandMessage.ACTION_PLAYER, app.client.getId(),"", new PlayerInput(inputType, this.app.client.getId()), GameStatus.PLAYING));
        }
    }

    /**
     * Dessin des éléménts du jeu
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UiStyle.DEFAULT_COLOR);

        // Dessins du terrain (lignes et cercle centrale)
        drawCourt(g2d);

        // Paddles
        g2d.fillRect(PADDLE_OFFSET_X, state.getPlayer1Y(), PADDLE_WIDTH, PADDLE_HEIGHT);
        g2d.fillRect(SCREEN_WIDTH - PADDLE_OFFSET_X - PADDLE_WIDTH, state.getPlayer2Y(), PADDLE_WIDTH, PADDLE_HEIGHT);

        // Balles
        if (state.getBalls() != null) {
            for (GameStateDto.BallPosition ballPos : state.getBalls()) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(ballPos.x(), ballPos.y(), BALL_DIAMETER, BALL_DIAMETER);
            }
        }

        // Obstacles
        if (state.getObstacles() != null) {
            g2d.setColor(Color.WHITE);
            for (GameStateDto.ObstaclePosition obstacle : state.getObstacles()) {
                g2d.fillRect(obstacle.x(), obstacle.y(), OBSTACLE_SIZE, OBSTACLE_SIZE);
            }
        }

        // Scores
        drawScores(g2d);
    }

    /**
     * Dessin du terrain de jeu
     * @param g2d
     */
    private void drawCourt(Graphics2D g2d) {
        // Ligne centrale
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT);
        g2d.setStroke(oldStroke);

        // Lignes de but
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 8}, 0);
        g2d.setStroke(dashed);
        int goalLineOffset = PADDLE_OFFSET_X / 2;
        g2d.drawLine(goalLineOffset, 0, goalLineOffset, SCREEN_HEIGHT);
        g2d.drawLine(SCREEN_WIDTH - goalLineOffset, 0, SCREEN_WIDTH - goalLineOffset, SCREEN_HEIGHT);
        g2d.setStroke(new BasicStroke());

        // Cercle central
        int centerCircleDiameter = 40;
        Shape centerCircle = new Ellipse2D.Double(
                (double) SCREEN_WIDTH / 2 - (double) centerCircleDiameter / 2,
                (double) SCREEN_HEIGHT / 2 - (double) centerCircleDiameter / 2,
                centerCircleDiameter, centerCircleDiameter
        );
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(centerCircle);
    }

    /**
     * Dessin des scores
     * @param g2d
     */
    private void drawScores(Graphics2D g2d) {
        g2d.setFont(UiStyle.FONT_TITLE);
        g2d.setColor(UiStyle.DEFAULT_COLOR);
        FontMetrics fm = g2d.getFontMetrics();

        String score1 = String.valueOf(state.getScorePlayer1());
        String score2 = String.valueOf(state.getScorePlayer2());

        g2d.drawString(score1, SCREEN_WIDTH / 4 - fm.stringWidth(score1) / 2, 60);
        g2d.drawString(score2, SCREEN_WIDTH * 3 / 4 - fm.stringWidth(score2) / 2, 60);
    }
}
