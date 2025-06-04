package Server.Game.Entitites;

import Common.GameConfig;
import Common.PlayerInput;

/**
 * Représente un paddle (raquette) contrôlé par un joueur.
 * Gère le mouvement vertical du paddle en réponse aux actions du joueur
 * et s'assure qu'il reste dans les limites de l'écran.
 */
public class Paddle extends MovingGameObject {
    private int currentSpeedY = 0;

    /**
     * Constructeur du Paddle.
     * @param playerId Identifiant du joueur (1 pour gauche, 2 pour droite) pour positionner le paddle.
     */
    public Paddle(int playerId) {
        super(0, 0, GameConfig.PADDLE_WIDTH, GameConfig.PADDLE_HEIGHT, 0, 0);
        if (playerId == 1) {
            this.x = GameConfig.PADDLE_OFFSET_X;
        } else {
            this.x = GameConfig.SCREEN_WIDTH - GameConfig.PADDLE_WIDTH - GameConfig.PADDLE_OFFSET_X;
        }
        resetPosition();
    }

    /**
     * Réinitialise la position verticale du paddle au centre de l'écran et arrête son mouvement.
     */
    public void resetPosition() {
        this.y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.currentSpeedY = 0;
    }

    /**
     * Traite une action du joueur (appui ou relâchement de touche) pour modifier la vitesse du paddle.
     * @param action Le type d'action du joueur.
     */
    public void handlePlayerAction(PlayerInput.InputType action) {
        switch (action) {
            case MOVE_UP -> currentSpeedY = -GameConfig.PADDLE_SPEED;
            case MOVE_DOWN -> currentSpeedY = GameConfig.PADDLE_SPEED;
            case STOP_MOVE -> currentSpeedY = 0;
        }
    }

    @Override
    public void update() {
        this.y += currentSpeedY;

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
