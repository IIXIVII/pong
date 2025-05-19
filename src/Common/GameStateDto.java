package Common;
import Game.Ui.Style.UiStyle;

import java.io.Serializable;

public class GameStateDto implements Serializable {
    public int winningScore = 3;

    // État du jeu
    public int player1Y;
    public int player2Y;

    public int scorePlayer1;
    public int scorePlayer2;

    public GameStatus currentStatus;
    public String message; // Pour afficher des infos (e.g., "Waiting for Player 2", "Player 1 Wins!")
    public int connectedPlayers = 0;
    public boolean canStartGame = false; // Si le bouton "Play" / "Start" doit être activé

    public GameStateDto() {
        // Initialisation par défaut
        player1Y = UiStyle.WINDOW_DIMENSION.height / 2 - UiStyle.PADDLE_HEIGHT / 2;
        player2Y = UiStyle.WINDOW_DIMENSION.height / 2 - UiStyle.PADDLE_HEIGHT / 2;
        currentStatus = GameStatus.WELCOME;
    }
}