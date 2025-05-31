package Server.Game.Entitites;

import Common.GameConfig;

public class Obstacle extends GameObject {
    public Obstacle(int x, int y) {
        super(x, y, GameConfig.OBSTACLE_SIZE, GameConfig.OBSTACLE_SIZE);
    }

    @Override
    public void update() {
        // Rien à faire
    }
}
