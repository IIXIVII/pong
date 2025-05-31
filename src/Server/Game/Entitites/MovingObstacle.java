package Server.Game.Entitites;

import Common.GameConfig;

public class MovingObstacle extends MovingGameObject {
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
