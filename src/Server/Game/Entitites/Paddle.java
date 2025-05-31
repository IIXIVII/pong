package Server.Game.Entitites;

import Common.GameConfig;
import Common.PlayerInput;
import Game.Ui.Style.UiStyle;

import java.awt.*;

import static Common.PlayerInput.InputType.*;

public class Paddle extends MovingGameObject {
    private int currentSpeedY = 0;

    public Paddle(int playerId) {
        super(0, 0, GameConfig.PADDLE_WIDTH, GameConfig.PADDLE_HEIGHT, 0, 0);
        if (playerId == 1) {
            this.x = GameConfig.PADDLE_OFFSET_X;
        } else {
            this.x = GameConfig.SCREEN_WIDTH - GameConfig.PADDLE_WIDTH - GameConfig.PADDLE_OFFSET_X;
        }
        resetPosition();
    }

    public void resetPosition() {
        this.y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.currentSpeedY = 0;
    }

    public void handlePlayerAction(PlayerInput.InputType action) {
        switch (action) {
            case MOVE_UP_PRESSED:
                currentSpeedY = -GameConfig.PADDLE_SPEED;
                break;
            case MOVE_DOWN_PRESSED:
                currentSpeedY = GameConfig.PADDLE_SPEED;
                break;
            case MOVE_UP_RELEASED:
                if (currentSpeedY < 0) {
                    currentSpeedY = 0;
                }
                break;
            case MOVE_DOWN_RELEASED:
                if (currentSpeedY > 0) {
                    currentSpeedY = 0;
                }
                break;
        }
    }

    @Override
    public void update() {
        this.y += currentSpeedY; // Apply the current speed

        // Clamp paddle to screen boundaries
        if (this.y < 0) {
            this.y = 0;
        }
        if (this.y + GameConfig.PADDLE_HEIGHT > GameConfig.SCREEN_HEIGHT) {
            this.y = GameConfig.SCREEN_HEIGHT - GameConfig.PADDLE_HEIGHT;
        }
    }

    public int getCurrentSpeedY() {
        return currentSpeedY;
    }
}
