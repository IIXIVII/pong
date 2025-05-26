package Server.Game.Entitites;

import Common.GameConfig;

import java.awt.Rectangle;
import java.util.Random;

public class Ball {
    public int x, y, dx, dy;
    private static final Random RANDOM = new Random();
    private static final double MAX_ANGLE = Math.PI / 4; // 45°
    public boolean outOfPlay = false; // Marqueur pour suppression

    public Ball(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        resetSpeed();
    }


    public void resetSpeed() {
        // Choix d'un quadrant (gauche ou droite)
        double baseAngle = RANDOM.nextBoolean() ? 0 : Math.PI;
        // Déviation entre -45° et +45°
        double angle = baseAngle + (RANDOM.nextDouble() * 2 - 1) * MAX_ANGLE;

        dx = (int) Math.round(GameConfig.INITIAL_BALL_SPEED * Math.cos(angle));
        dy = (int) Math.round(GameConfig.INITIAL_BALL_SPEED * Math.sin(angle));

        // S'assurer que dx ne soit pas nul
        if (dx == 0) dx = baseAngle == 0 ? 1 : -1;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.BALL_DIAMETER, GameConfig.BALL_DIAMETER);
    }
}