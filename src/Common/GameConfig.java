package Common;

/**
 * Contient les constantes de configuration du jeu.
 * Cette classe n'est pas destinée à être instanciée.
 */
public final class GameConfig {
    private GameConfig() {}

    // Fenêtre
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 600;

    // Paddle
    public static final int PADDLE_WIDTH = 20;
    public static final int PADDLE_HEIGHT = 100;
    public static final int PADDLE_OFFSET_X = 20; // Distance du paddle par rapport au bord de l'écran
    public static final int PADDLE_SPEED = 10;    // Vitesse de déplacement des paddles

    // Balle
    public static final int BALL_DIAMETER = 20;
    public static final int MAX_BALLS = 10;                             // Nombre maximum de balles en jeu simultanément
    public static final int INITIAL_BALL_SPEED = 5;                     // Vitesse initiale de la balle
    public static final int BALL_SPAWN_INTERVAL_SECONDS = 20;           // Intervalle en secondes pour faire apparaître de nouvelles balles
    public static final double PADDLE_SPEED_TO_BALL_DY_FACTOR = 0.35;   // Influence de la vitesse du paddle sur la balle
    public static final int MAX_BALL_SPEED_Y = INITIAL_BALL_SPEED * 2;  // Vitesse verticale maximale de la balle

    // Obstacles
    public static final int OBSTACLE_SIZE = 30;                             // Taille des obstacles
    public static final int MAX_OBSTACLES = 3;                              // Nombre maximum d'obstacles
    public static final int OBSTACLE_MIN_DISTANCE_FROM_PADDLE = 150;        // Distance minimale d'un obstacle par rapport au paddle
    public static final int OBSTACLE_MIN_DISTANCE_FROM_CENTER_X = 100;      // Distance minimale d'un obstacle par rapport au centre de l'écran
    public static final int OBSTACLE_MOVING_SPEED = 2;                      // Vitesse des obstacles mobiles

    public static final int WINNING_SCORE = 5; // Score nécessaire pour gagner la partie

    // --- Paramètres Serveur et Client ---
    public static final int SERVER_TPS = 60;                        // Ticks par seconde
    public static final String DEFAULT_SERVER_HOST = "localhost";   // Hôte par défaut du serveur
    public static final int DEFAULT_SERVER_PORT = 8085;             // Port par défaut du serveur
}