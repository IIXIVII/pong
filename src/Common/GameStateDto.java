package Common;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) représentant l'état complet du jeu à un instant T.
 * Cet objet est sérializable pour être transmis sur le réseau.
 */
public class GameStateDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Positions des paddles des joueurs
    private int player1Y;
    private int player2Y;
    // Scores des joueurs
    private int scorePlayer1;
    private int scorePlayer2;

    public int connectedPlayers;

    public String message; // Pour afficher des infos (e.g., "Waiting for Player 2", "Player 1 Wins!")
    public LocalDateTime StartTargetTime;// Compte-à-rebours pour synchroniser les joueurs au lancement du jeu

    public GameStatus gameStatus;

    private List<BallPosition> balls;
    private List<ObstaclePosition> obstacles;

    /** Constructeur par défaut. Initialise l'état du jeu. */
    public GameStateDto() {
        this.player1Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.player2Y = GameConfig.SCREEN_HEIGHT / 2 - GameConfig.PADDLE_HEIGHT / 2;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
        this.connectedPlayers = 0;
        this.message = "";
        this.startTargetTime = null;
        this.gameStatus = GameStatus.WELCOME;
        this.balls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }

    /** Constructeur de copie. Crée une copie profonde. */
    public GameStateDto(GameStateDto other) {
        this.player1Y = other.player1Y;
        this.player2Y = other.player2Y;
        this.scorePlayer1 = other.scorePlayer1;
        this.scorePlayer2 = other.scorePlayer2;
        this.connectedPlayers = other.connectedPlayers;
        this.message = other.message;
        this.startTargetTime = other.startTargetTime; // LocalDateTime est immutable
        this.gameStatus = other.gameStatus;
        this.balls = new ArrayList<>(other.balls);
        this.obstacles = new ArrayList<>(other.obstacles);
    }

    // Getters
    public int getPlayer1Y() { return player1Y; }
    public int getPlayer2Y() { return player2Y; }
    public int getScorePlayer1() { return scorePlayer1; }
    public int getScorePlayer2() { return scorePlayer2; }
    public int getConnectedPlayers() { return connectedPlayers; }
    public String getMessage() { return message; }
    public LocalDateTime getStartTargetTime() { return startTargetTime; }
    public GameStatus getGameStatus() { return gameStatus; }
    public List<BallPosition> getBalls() { return new ArrayList<>(balls); } // Retourne une copie pour protéger l'encapsulation
    public List<ObstaclePosition> getObstacles() { return new ArrayList<>(obstacles); } // Idem

    // Setters
    public void setPlayer1Y(int player1Y) { this.player1Y = player1Y; }
    public void setPlayer2Y(int player2Y) { this.player2Y = player2Y; }
    public void setScorePlayer1(int scorePlayer1) { this.scorePlayer1 = scorePlayer1; }
    public void setScorePlayer2(int scorePlayer2) { this.scorePlayer2 = scorePlayer2; }
    public void setConnectedPlayers(int connectedPlayers) { this.connectedPlayers = connectedPlayers; }
    public void setMessage(String message) { this.message = message; }
    public void setStartTargetTime(LocalDateTime startTargetTime) { this.startTargetTime = startTargetTime; }
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }
    public void setBalls(List<BallPosition> balls) {
        this.balls = (balls != null) ? new ArrayList<>(balls) : new ArrayList<>();
    }
    public void setObstacles(List<ObstaclePosition> obstacles) {
        this.obstacles = (obstacles != null) ? new ArrayList<>(obstacles) : new ArrayList<>();
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
                ", gameStatus=" + gameStatus +
                ", balls=" + balls +
                ", obstacles=" + obstacles +
                '}';
    }
}