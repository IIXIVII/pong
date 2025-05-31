package Server.Game.Entitites;

public class MovingGameObject extends GameObject {
    protected int dx, dy;

    public MovingGameObject(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public void setDx(int dx) { this.dx = dx; }
    public void setDy(int dy) { this.dy = dy; }

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

    @Override
    public void update() {
        move();
    }
}