package Server.Game.Entitites;

import java.awt.*;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected Rectangle bounds;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public Rectangle getBounds() {
        bounds.x = this.x;
        bounds.y = this.y;
        bounds.width = this.width;
        bounds.height = this.height;
        return bounds;
    }

    public abstract void update();
}
