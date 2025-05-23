package Server.Game.Entitites;

import Common.GameConfig;
import Game.Ui.Style.UiStyle;

import java.awt.*;

public class Paddle {
    private int x;
    private int y;

    public Paddle(int playerId) {
        // 1 ou 2
        if (playerId == 1) {
            this.x = GameConfig.PADDLE_OFFSET_X;
        } else {
            this.x = GameConfig.SCREEN_WIDTH - GameConfig.PADDLE_WIDTH - GameConfig.PADDLE_OFFSET_X;;
        }
        resetPosition();
    }

    public void resetPosition() {
        this.y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
    }

    public void moveUp() {
        this.y = Math.max(0, this.y - GameConfig.PADDLE_SPEED);
    }

    public void moveDown() {
        this.y = Math.min(GameConfig.SCREEN_HEIGHT - GameConfig.PADDLE_HEIGHT, this.y + GameConfig.PADDLE_SPEED);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, GameConfig.PADDLE_WIDTH, GameConfig.PADDLE_HEIGHT);
    }}
