package Common;
// Constantes du jeu
public final class GameConfig {
    private GameConfig() {}

    // --- Dimensions de l'écran et des éléments ---
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    public static final int PADDLE_WIDTH = 20;
    public static final int PADDLE_HEIGHT = 100;
    public static final int PADDLE_OFFSET_X = 20;

    public static final int BALL_DIAMETER = 20;

    // --- Paramètres de Jeu ---
    public static final int WINNING_SCORE = 3;
    public static final int PADDLE_SPEED = 15;
    public static final int MAX_BALLS = 3;
    public static final int INITIAL_BALL_SPEED = 6;

    // --- Paramètres Serveur et Client ---
    public static final double SERVER_TPS = 60.0; // Ticks par seconde pour la logique serveur
    public static final String DEFAULT_SERVER_HOST = "localhost";
    public static final int DEFAULT_SERVER_PORT = 8085; // Port serveur

    // --- Identification des Joueurs ---
    public static final int PLAYER_1_ID = 1;
    public static final int PLAYER_2_ID = 2;
}