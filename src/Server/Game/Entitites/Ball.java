package Server.Game.Entitites;

import Common.GameConfig;

import java.awt.Rectangle;
import java.util.Random;

import static Common.GameConfig.MAX_BALL_SPEED_Y;
/**
 * Représente la balle
 * Hérite de MovingGameObject pour gérer le mouvement et les collisions.
 */
public class Ball extends MovingGameObject {
    private static final Random RANDOM = new Random();
    private static final double MAX_ANGLE = Math.PI / 4; // 45°
    private boolean markedForRemoval = false;

    /**
     * Constructeur de la balle.
     * @param startX Position X initiale.
     * @param startY Position Y initiale.
     */
    public Ball(int startX, int startY) {
        super(startX, startY, GameConfig.BALL_DIAMETER, GameConfig.BALL_DIAMETER, 0, 0);
        resetSpeedAndDirection();
    }

    /**
     * Réinitialise la balle
     * La balle part dans une direction aléatoire (gauche ou droite) avec un angle aléatoire.
     */
    private void resetSpeedAndDirection() {
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

        // Gérer les collisions avec les bords supérieur et inférieur de l'écran
        if (y <= 0) {
            y = 0;
            reverseY();
        } else if (y + height >= GameConfig.SCREEN_HEIGHT) {
            y = GameConfig.SCREEN_HEIGHT - height;
            reverseY();
        }
    }

    /**
     * Gère l'impact de la balle avec un paddle.
     * La vitesse de la balle est influencée par la vitesse du paddle
     * @param paddle Le paddle avec lequel la balle est entrée en collision.
     */
    public void handlePaddleImpact(Paddle paddle) {
        reverseX();
        // Ajout de momentum en fonction de la vitesse du paddle
        dy += (int) (paddle.getCurrentSpeedY() * GameConfig.PADDLE_SPEED_TO_BALL_DY_FACTOR);
        // Limiter la vitesse verticale max pour éviter les rebonds trop rapides
        this.dy = Math.max(-GameConfig.MAX_BALL_SPEED_Y, Math.min(this.dy, GameConfig.MAX_BALL_SPEED_Y));

        // Repositionnement de la balle juste en dehors du paddle pour éviter les collisions persistantes
        if (dx > 0) x = paddle.getX() + paddle.getWidth() + 1;
        else x = paddle.getX() - this.width - 1;
    }

    /**
     * Gère l'impact de la balle avec un obstacle.
     * @param obstacle L'obstacle avec lequel la balle est entrée en collision.
     */
    public void handleObstacleImpact(GameObject obstacle) {
        Rectangle ballBounds = getBounds();
        Rectangle obstacleBounds = obstacle.getBounds();
        Rectangle intersection = ballBounds.intersection(obstacleBounds);

        if (intersection.isEmpty()) return;

        // Collision verticale (dessus/dessous)
        if (intersection.width >= intersection.height) {
            reverseY();
            // Repositionnement de la balle pour éviter les collisions persistantes
            y = (ballBounds.getCenterY() < obstacleBounds.getCenterY()) ? obstacleBounds.y - height -1 : obstacleBounds.y + obstacleBounds.height +1;

        // Collision horizontale (gauche/droite)
        } else {
            reverseX();
            // Repositionnement de la balle pour éviter les collisions persistantes
            x = (ballBounds.getCenterX() < obstacleBounds.getCenterX()) ? obstacleBounds.x - width -1 : obstacleBounds.x + obstacleBounds.width +1;
        }
    }

    public void markForRemoval() { this.markedForRemoval = true; }
    public boolean isMarkedForRemoval() { return markedForRemoval; }
}