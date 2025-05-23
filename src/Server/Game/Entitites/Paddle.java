package Server.Game.Entitites;

import Game.Ui.Style.UiStyle;

import java.awt.*;

public class Paddle {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final int speed;
    private final int playerId; // 1 ou 2

    public Paddle(int playerId) {
        this.playerId = playerId;
        this.width = UiStyle.PADDLE_WIDTH;
        this.height = UiStyle.PADDLE_HEIGHT;
        this.speed = 10;
        if (playerId == 1) {
            this.x = width;
        } else {
            this.x = UiStyle.WINDOW_DIMENSION.width - (2 * width);
        }
        resetPosition();
    }

    public void resetPosition() {
        this.y = UiStyle.WINDOW_DIMENSION.height  / 2 - height / 2;
    }

    public void moveUp() {
        this.y = Math.max(0, this.y - speed);
    }

    public void moveDown() {
        this.y = Math.min(UiStyle.WINDOW_DIMENSION.height - height, this.y + speed);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getPlayerId() { return playerId; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
