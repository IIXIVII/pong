package Common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Etat du jeu
public class GameStateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // Position des paddles en Y
    public int player1Y;
    public int player2Y;
    // Score des joueurs
    public int scorePlayer1;
    public int scorePlayer2;

    public int connectedPlayers;

    public String message; // Pour afficher des infos (e.g., "Waiting for Player 2", "Player 1 Wins!")
    public LocalDateTime StartTargetTime;

    public GameStatus gameStatus;

 ;   // Liste des positions des balles
    public List<BallPosition> balls;

    public static class BallPosition implements Serializable {
        private static final long serialVersionUID = 2L;
        public int x, y;
        public BallPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "BallPosition{x=" + x + ", y=" + y + "}";
        }
    }

    public GameStateDto() {
        // Initialisation par dÃ©faut
        this.balls = new ArrayList<>();
        this.player1Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.player2Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.message = "";
        this.connectedPlayers = 0;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
        this.gameStatus = GameStatus.WELCOME;
    }

    // Copy constructor to avoid reference issues
    public GameStateDto(GameStateDto other) {
        this.player1Y = other.player1Y;
        this.player2Y = other.player2Y;
        this.scorePlayer1 = other.scorePlayer1;
        this.scorePlayer2 = other.scorePlayer2;
        this.connectedPlayers = other.connectedPlayers;
        this.message = other.message;
        this.StartTargetTime = other.StartTargetTime; // LocalDateTime is immutable, so this is safe

        // Deep copy of balls list
        this.balls = new ArrayList<>();
        if (other.balls != null) {
            for (BallPosition ball : other.balls) {
                this.balls.add(new BallPosition(ball.x, ball.y));
            }
        }
    }

    @Override
    public String toString() {
        return "GameStateDto{" +
                "player1Y=" + player1Y +
                ", player2Y=" + player2Y +
                ", scorePlayer1=" + scorePlayer1 +
                ", scorePlayer2=" + scorePlayer2 +
                ", connectedPlayers=" + connectedPlayers +
                ", message='" + message + '\'' +
                ", StartTargetTime=" + StartTargetTime +
                ", balls=" + balls +
                '}';
    }
}