package Server.Game.Entitites;

import Common.GameConfig;

import java.awt.Rectangle;
import java.util.Random;

import static Common.GameConfig.MAX_BALL_SPEED_Y;

public class Ball extends MovingGameObject {
    private static final Random RANDOM = new Random();
    private static final double MAX_ANGLE = Math.PI / 4; // 45°
    public boolean outOfPlay = false; // Marqueur pour suppression

    public Ball(int startX, int startY) {
        super(startX, startY, GameConfig.BALL_DIAMETER, GameConfig.BALL_DIAMETER, 0, 0);
        resetSpeedAndDirection();
    }


    public void resetSpeedAndDirection() {
        // Choix d'un quadrant (gauche ou droite)
        double baseAngle = RANDOM.nextBoolean() ? 0 : Math.PI;
        // Angle entre -45° et +45°
        double angle = baseAngle + (RANDOM.nextDouble() * 2 - 1) * MAX_ANGLE;

        dx = (int) Math.round(GameConfig.INITIAL_BALL_SPEED * Math.cos(angle));
        dy = (int) Math.round(GameConfig.INITIAL_BALL_SPEED * Math.sin(angle));

        // S'assurer que dx ne soit pas nul
        if (dx == 0) dx = baseAngle == 0 ? 1 : -1;
    }

    @Override
    public void update() {
        super.move();

        if (y <= 0) {
            y = 0;
            reverseY();
        } else if (y + height >= GameConfig.SCREEN_HEIGHT) {
            y = GameConfig.SCREEN_HEIGHT - height;
            reverseY();
        }
    }

    public void handlePaddleImpact(Paddle paddle) {
        Rectangle paddleBounds = paddle.getBounds();

        // Inversion direction horizontale
        reverseX();

        // Ajout de momentum en fonction de la vitesse du paddle
        float paddleVelocityY = paddle.getCurrentSpeedY();
        dy += (int) (paddleVelocityY * GameConfig.PADDLE_SPEED_TO_BALL_DY_FACTOR);

        // Limiter la vitesse verticale max pour éviter les rebonds trop rapides
        this.dy = Math.max(-MAX_BALL_SPEED_Y, Math.min(this.dy, MAX_BALL_SPEED_Y));

        // Positionner la balle juste en dehors du paddle pour éviter les collisions persistantes
        if (dx > 0) {
            x = paddleBounds.x + paddleBounds.width + 1;
        } else {
            x = paddleBounds.x - this.width - 1;
        }
    }

    public void handleObstacleImpact(GameObject obstacle) {
        Rectangle ballBounds = getBounds();
        Rectangle obstacleBounds = obstacle.getBounds();

        // Rebond horizontal ou vertical
        if (ballBounds.intersects(obstacleBounds)) {
            if (ballBounds.x < obstacleBounds.x || ballBounds.x + ballBounds.width > obstacleBounds.x + obstacleBounds.width) {
                reverseX();
            } else {
                reverseY();
            }

            // Repositionner la balle pour la sortir de l'obstacle
            int stepX = (dx != 0) ? dx / Math.abs(dx) : 0;
            int stepY = (dy != 0) ? dy / Math.abs(dy) : 0;

            // Pour éviter une boucle infinie si dx et dy sont tous deux 0
            if (stepX == 0 && stepY == 0) {
                // Fallback: pousse la balle légèrement à droite
                stepX = 1;
            }

            while (getBounds().intersects(obstacleBounds)) {
                x += stepX;
                y += stepY;
            }
        }
    }


}