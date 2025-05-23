package Server.Game.Entitites;

import Common.GameConfig;

import java.awt.Rectangle;
import java.util.Random;

public class Ball {
    public int x, y, dx, dy;
    private Random random = new Random();
    public boolean outOfPlay = false; // Marqueur pour suppression

    public Ball(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        resetSpeed();
    }


    public void resetSpeed() {
        double angle = random.nextDouble() * Math.PI / 2 - Math.PI / 4;
        if (random.nextBoolean()) angle += Math.PI;

        dx = (int) (GameConfig.INITIAL_BALL_SPEED * Math.cos(angle));
        dy = (int) (GameConfig.INITIAL_BALL_SPEED * Math.sin(angle));

        if (Math.abs(dx) < GameConfig.INITIAL_BALL_SPEED / 3) {
            dx = (dx > 0 ? 1 : -1) * (GameConfig.INITIAL_BALL_SPEED / 3 + 1);
        }
        if (Math.abs(dy) < GameConfig.INITIAL_BALL_SPEED / 3 && GameConfig.INITIAL_BALL_SPEED > 2) {
            dy = (dy > 0 ? 1 : -1) * (GameConfig.INITIAL_BALL_SPEED / 3);
        } else if (Math.abs(dy) == 0) {
            dy = (random.nextBoolean() ? 1 : -1);
        }
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void reverseX() {
        dx *= -1;
    }

    public void reverseY() {
        dy *= -1;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.BALL_DIAMETER, GameConfig.BALL_DIAMETER);
    }
}