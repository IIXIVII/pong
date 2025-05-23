package Common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Etat du jeu
public class GameStateDto implements Serializable {
    // Position des paddles en Y
    public int player1Y;
    public int player2Y;
    // Score des joueurs
    public int scorePlayer1;
    public int scorePlayer2;

    public int connectedPlayers;

    public String message; // Pour afficher des infos (e.g., "Waiting for Player 2", "Player 1 Wins!")
    public LocalDateTime StartTargetTime ;

    // Liste des positions des balles
    public List<BallPosition> balls;
    public static class BallPosition implements Serializable {
        private static final long serialVersionUID = 2L;
        public int x, y;
        public BallPosition(int x, int y) { this.x = x; this.y = y; }
    }

    public GameStateDto() {
        // Initialisation par défaut
        this.balls = new ArrayList<>();
        this.player1Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.player2Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
    }



}