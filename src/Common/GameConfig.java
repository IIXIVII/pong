package Common;
// Constantes du jeu
public final class GameConfig {
    private GameConfig() {}

    // --- Dimensions de l'écran et des éléments ---
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 600;

    public static final int PADDLE_WIDTH = 20;
    public static final int PADDLE_HEIGHT = 100;
    public static final int PADDLE_OFFSET_X = 20;
    public static final int PADDLE_SPEED = 10;

    // Balle
    public static final int BALL_DIAMETER = 20;
    public static final int MAX_BALLS = 5;
    public static final int INITIAL_BALL_SPEED = 5;
    public static final int BALL_SPAWN_INTERVAL_SECONDS = 10;
    public static final double PADDLE_SPEED_TO_BALL_DY_FACTOR = 0.35; // Influence de la vitesse du paddle sur dy de la balle.
    public static final int MAX_BALL_SPEED_Y = INITIAL_BALL_SPEED * 2;

    // Obstacles
    public static final int OBSTACLE_SIZE = 50;
    public static final int MAX_OBSTACLES = 3; // Max number of obstacles
    public static final int OBSTACLE_MIN_DISTANCE_FROM_PADDLE = 150;
    public static final int OBSTACLE_MIN_DISTANCE_FROM_CENTER_X = 100;
    public static final int OBSTACLE_MOVING_SPEED = 2;

    public static final int WINNING_SCORE = 5;

    // --- Paramètres Serveur et Client ---
    public static final int SERVER_TPS = 60; // Ticks par seconde pour la logique serveur
    public static final String DEFAULT_SERVER_HOST = "localhost";
    public static final int DEFAULT_SERVER_PORT = 8085; // Port serveur
    public static final int MAX_PLAYERS = 2; // Nombre maximum de joueurs
}