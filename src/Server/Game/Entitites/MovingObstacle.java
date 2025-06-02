package Server.Game.Entitites;

import Common.GameConfig;

/**
 * Représente un obstacle mobile dans le jeu.
 * Il se déplace verticalement et rebondit sur les bords supérieur et inférieur de l'écran.
 */
public class MovingObstacle extends MovingGameObject {

    /**
     * Constructeur pour MovingObstacle.
     * L'obstacle est initialisé avec une vitesse verticale et une direction aléatoire.
     * @param x Position X initiale.
     * @param y Position Y initiale.
     */
    public MovingObstacle(int x, int y) {
        super(x, y, GameConfig.OBSTACLE_SIZE, GameConfig.OBSTACLE_SIZE, 0, GameConfig.OBSTACLE_MOVING_SPEED);
        // Direction initiale aléatoire
        if (Math.random() < 0.5) {
            this.dy *= -1;
        }
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
}
