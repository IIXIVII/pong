package Game.Ui;

import Game.PongClientApp;
import Game.Ui.Style.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;



public class GameScreen extends BaseScreen {

    public GameScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        System.out.println("Panel size: " + getWidth() + "x" + getHeight());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        //TODO Gérer les input avec le serveur
        //if (currentLocalState == null || gameClient.getPlayerID() == 0) return;
       /* int myPlayerID = gameClient.getPlayerID();
        PlayerInput.InputType input = null;

        if ((myPlayerID == 1 && keyCode == KeyEvent.VK_Z) || (myPlayerID == 2 && keyCode == KeyEvent.VK_UP)) {
            input = PlayerInput.InputType.MOVE_UP;
        } else if ((myPlayerID == 1 && keyCode == KeyEvent.VK_S) || (myPlayerID == 2 && keyCode == KeyEvent.VK_DOWN)) {
            input = PlayerInput.InputType.MOVE_DOWN;

        if (input != null) {
           gameClient.sendInput(input);
        }*/
    }



    public void tick() {
        repaint(); // Pour l'instant, juste rafraîchir l'écran
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentLocalState == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UiStyle.DEFAULT_COLOR);


        // Ligne centrale pleine
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(UiStyle.WINDOW_DIMENSION.width/2, 0, UiStyle.WINDOW_DIMENSION.width/2, UiStyle.WINDOW_DIMENSION.height);
        g2d.setStroke(oldStroke);

        // Lignes en pointillés indiquant les limites
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 8}, 0);
        g2d.setStroke(dashed);
        int goalLineOffset = 10;
        g2d.drawLine(goalLineOffset, 0, goalLineOffset, UiStyle.WINDOW_DIMENSION.height);
        g2d.drawLine(UiStyle.WINDOW_DIMENSION.width - goalLineOffset, 0, UiStyle.WINDOW_DIMENSION.width - goalLineOffset, UiStyle.WINDOW_DIMENSION.height);
        g2d.setStroke(new BasicStroke()); // Reset stroke

        // Cercle d'indication du départ de la balle
        int centerCircleDiameter = 40;
        Shape centerCircle = new Ellipse2D.Double(
                (double) UiStyle.WINDOW_DIMENSION.width / 2 - (double) centerCircleDiameter / 2,
                (double) UiStyle.WINDOW_DIMENSION.height / 2 - (double) centerCircleDiameter / 2,
                centerCircleDiameter, centerCircleDiameter
        );

        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(centerCircle);

        int paddleOffset = 50;
        // Raquette Joueur 1
        g2d.fillRect(paddleOffset, currentLocalState.player1Y, UiStyle.PADDLE_WIDTH, UiStyle.PADDLE_HEIGHT);
        // Raquette Joueur 2
        g2d.fillRect(UiStyle.WINDOW_DIMENSION.width - paddleOffset - UiStyle.PADDLE_WIDTH, currentLocalState.player2Y, UiStyle.PADDLE_WIDTH, UiStyle.PADDLE_HEIGHT);

        // Balle(s)
        // TODO

        // Obstacle
        // TODO

        // Scores (Drawn directly)
        Font scoreFont = UiStyle.FONT_TITLE;
        g2d.setFont(scoreFont);
        g2d.setColor(UiStyle.DEFAULT_COLOR);
        FontMetrics fmScore = g2d.getFontMetrics(scoreFont);
        g2d.drawString(String.valueOf(currentLocalState.scorePlayer1), UiStyle.WINDOW_DIMENSION.width / 4 - fmScore.stringWidth(String.valueOf(currentLocalState.scorePlayer1)) / 2, 60);
        g2d.drawString(String.valueOf(currentLocalState.scorePlayer2), UiStyle.WINDOW_DIMENSION.width * 3 / 4 - fmScore.stringWidth(String.valueOf(currentLocalState.scorePlayer2)) / 2, 60);
    }
}
