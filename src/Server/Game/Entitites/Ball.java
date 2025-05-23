package Server.Game.Entitites;

import java.awt.Rectangle;
import java.util.Random;

public class Ball {
    public int x, y, dx, dy;
    private final int diameter;
    private final int initialSpeedMagnitude;
    private Random random = new Random();
    public boolean outOfPlay = false; // Marqueur pour suppression

    public Ball(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.diameter = 30;
        this.initialSpeedMagnitude = 10;
        resetSpeed();
    }

    public void resetSpeed() {
        outOfPlay = false;
        // Logique de vitesse initiale améliorée pour éviter dx ou dy trop faible
        double angle = random.nextDouble() * Math.PI / 2 - Math.PI / 4; // Angle entre -45 et 45 deg
        if (random.nextBoolean()) angle += Math.PI; // Direction gauche ou droite

        dx = (int) (initialSpeedMagnitude * Math.cos(angle));
        dy = (int) (initialSpeedMagnitude * Math.sin(angle));

        // S'assurer qu'il y a un mouvement significatif sur les deux axes
        if (Math.abs(dx) < initialSpeedMagnitude / 3) {
            dx = (dx > 0 ? 1 : -1) * (initialSpeedMagnitude / 3 + 1);
        }
        if (Math.abs(dy) < initialSpeedMagnitude / 3 && initialSpeedMagnitude > 2) {
            dy = (dy > 0 ? 1 : -1) * (initialSpeedMagnitude / 3);
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
        return new Rectangle(x, y, diameter, diameter);
    }
}