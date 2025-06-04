package Server.Game.Entitites;

import Common.GameConfig;

/**
 * Représente un obstacle statique dans le jeu.
 * Cet objet ne se déplace pas de lui-même.
 */
public class Obstacle extends GameObject {
    /**
     * Constructeur pour un obstacle statique.
     * @param x Position X de l'obstacle.
     * @param y Position Y de l'obstacle.
     */
    public Obstacle(int x, int y) {
        super(x, y, GameConfig.OBSTACLE_SIZE, GameConfig.OBSTACLE_SIZE);
    }

    @Override
    public void update() {
        // Rien à faire car statique
    }
}
