package Common;
import Game.Ui.Style.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public LocalDateTime StartTargetTime ;

    public List<BallPosition> balls;
    public static class BallPosition implements Serializable {
        private static final long serialVersionUID = 2L;
        public int x, y;
        public BallPosition(int x, int y) { this.x = x; this.y = y; }
    }

    public GameStateDto() {
        // Initialisation par défaut
        this.balls = new ArrayList<>();
        player1Y = UiStyle.WINDOW_DIMENSION.height / 2 - UiStyle.PADDLE_HEIGHT / 2;
        player2Y = UiStyle.WINDOW_DIMENSION.height / 2 - UiStyle.PADDLE_HEIGHT / 2;
        currentStatus = GameStatus.WELCOME;
    }



}