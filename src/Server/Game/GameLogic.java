package Server.Game;

import Common.GameStateDto;
import Common.Messages.CommandMessage;
import Common.Messages.GameMessage;
import Common.PlayerInput;
import Common.Tools.Logger;
import Server.Game.Entitites.*;
import Server.Server;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static Common.GameConfig.*;
import static Common.GameStatus.*;

/**
 * Gère la logique principale du jeu Pong côté serveur.
 * Inclut la gestion des entités (paddles, balles, obstacles), les collisions,
 * le comptage des scores, et la progression de l'état du jeu.
 * S'exécute dans son propre thread pour mettre à jour l'état du jeu à intervalles réguliers.
 */
public class GameLogic implements Runnable{
    GameStateDto gameState;
    Server server;
    private final List<Ball> balls;
    private final Paddle player1Paddle;
    private final Paddle player2Paddle;
    private final List<GameObject> obstacles;

    private long lastBallSpawnTime;
    private static final Random random = new Random();

    public GameLogic(){
        this.balls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.player1Paddle = new Paddle(1);
        this.player2Paddle = new Paddle(2);
    }

    /**
     * Initialise une nouvelle partie.
     * Réinitialise les positions des paddles, les balles, et les obstacles.
     * @param initialGameState L'objet GameStateDto à utiliser et à mettre à jour.
     * @param server La référence au serveur principal.
     */
    public void initializeNewGame(GameStateDto initialGameState, Server server) {
        this.gameState = initialGameState;
        this.server = server;

        this.gameState.setScorePlayer1(0);
        this.gameState.setScorePlayer2(0);

        player1Paddle.resetPosition();
        player2Paddle.resetPosition();

        this.balls.clear();
        addInitialBall();

        this.obstacles.clear();
        spawnObstacles();

        this.server = server;

        lastBallSpawnTime = System.currentTimeMillis();
        syncEntitiesToDTO();
    }

    /**
     * Ajoute une balle initiale au centre du terrain.
     */
    private void addInitialBall() {
        Ball newBall = new Ball(
                SCREEN_WIDTH / 2 - BALL_DIAMETER / 2,
                SCREEN_HEIGHT / 2 - BALL_DIAMETER / 2
        );
        balls.add(newBall);
    }

    /**
     * Fait apparaître une nouvelle balle si le nombre maximum n'est pas atteint
     * et si l'intervalle de temps depuis le dernier spawn est écoulé.
     */
    private void spawnNewBall() {
        if (balls.size() < MAX_BALLS &&
                (System.currentTimeMillis() - lastBallSpawnTime) > BALL_SPAWN_INTERVAL_SECONDS * 1000) {
            addInitialBall();
            lastBallSpawnTime = System.currentTimeMillis();
        }
    }

    /**
     * Crée et place les obstacles sur le terrain de jeu.
     * Les obstacles sont placés aléatoirement en évitant certaines zones.
     */
    private void spawnObstacles() {
        obstacles.clear();
        for (int i = 0; i < MAX_OBSTACLES; i++) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int x = random.nextInt(SCREEN_WIDTH - OBSTACLE_SIZE);
                int y = random.nextInt(SCREEN_HEIGHT - OBSTACLE_SIZE);
                GameObject newObstacle;

                // CHoix aléatoire entre un obstacle statique ou qui se déplace
                if (random.nextBoolean()) {
                    newObstacle = new MovingObstacle(x,y);
                } else {
                    newObstacle = new Obstacle(x, y);
                }

                if (isValidObstaclePosition(newObstacle)) {
                    obstacles.add(newObstacle);
                    placed = true;
                }
                attempts++;
            }
        }
    }

    /**
     * Vérifie si la position d'un nouvel obstacle est valide.
     * Un obstacle ne doit pas être trop proche des paddles, du centre, ou d'autres obstacles.
     * @param obstacle Le nouvel obstacle à valider.
     * @return true si la position est valide, false sinon.
     */
    private boolean isValidObstaclePosition(GameObject obstacle) {
        Rectangle obsBounds = obstacle.getBounds();

        // Vérifier la distance par rapport aux paddles
        if (obsBounds.getMaxX() > (PADDLE_OFFSET_X - OBSTACLE_MIN_DISTANCE_FROM_PADDLE) &&
                obsBounds.x < (PADDLE_OFFSET_X + PADDLE_WIDTH + OBSTACLE_MIN_DISTANCE_FROM_PADDLE)) {
            return false; // Trop proche du paddle 1
        }
        if (obsBounds.getMaxX() > (SCREEN_WIDTH - PADDLE_OFFSET_X - PADDLE_WIDTH - OBSTACLE_MIN_DISTANCE_FROM_PADDLE) &&
                obsBounds.x < (SCREEN_WIDTH - PADDLE_OFFSET_X + OBSTACLE_MIN_DISTANCE_FROM_PADDLE)) {
            return false; // Trop proche du paddle 2
        }

        // Vérifier la distance par rapport à la zone de spawn centrale
        int centerX = SCREEN_WIDTH / 2;
        if (obsBounds.getMaxX() > (centerX - OBSTACLE_MIN_DISTANCE_FROM_CENTER_X) &&
                obsBounds.x < (centerX + OBSTACLE_MIN_DISTANCE_FROM_CENTER_X)) {
            return false; // Trop proche du centre (zone de spawn de la balle)
        }

        // Vérifier le chevauchement avec les obstacles existants
        for (GameObject existing : obstacles) {
            if (obsBounds.intersects(existing.getBounds())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Synchronise les positions des entités du jeu (paddles, balles, obstacles)
     * avec l'objet GameStateDto.
     */
    private void syncEntitiesToDTO() {
        this.gameState.setPlayer1Y(player1Paddle.getY());
        this.gameState.setPlayer2Y(player2Paddle.getY());

        gameState.setBalls(balls.stream().map(b -> new GameStateDto.BallPosition(b.getX(), b.getY())).collect(Collectors.toList()));
        gameState.setObstacles(obstacles.stream().map(o -> new GameStateDto.ObstaclePosition(o.getX(), o.getY())).collect(Collectors.toList()));
    }

    /**
     * Traite une action envoyée par un joueur.
     * @param input L'objet PlayerInput contenant l'action et l'ID du joueur.
     */
    public void actionPlayer(PlayerInput input) {
        if (input == null) return;
        Paddle paddleToMove = (input.playerId == 0) ? player1Paddle : player2Paddle;
        paddleToMove.handlePlayerAction(input.type);
    }

    /**
     * Boucle de mise à jour principale de la logique du jeu.
     * Met à jour toutes les entités, gère les collisions, les scores, et vérifie la fin de partie.
     * Envoie ensuite l'état mis à jour aux clients.
     */
    public void update() {

        // Mettre à jour les paddles
        player1Paddle.update();
        player2Paddle.update();

        // Mettre à jour les obstacles (ceux qui bougent)
        for (GameObject obstacle : obstacles) {
            obstacle.update();
        }

        // Mettre à jour les balles et gérer les collisions
        List<Ball> ballsToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.update();

            Rectangle ballBounds = ball.getBounds();

            // Collisions paddle
            if (ballBounds.intersects(player1Paddle.getBounds())) {
                ball.handlePaddleImpact(player1Paddle);
            } else if (ballBounds.intersects(player2Paddle.getBounds())) {
                ball.handlePaddleImpact(player2Paddle);
            }

            // Collisions obstacle
            for (GameObject obstacle : obstacles) {
                if (ballBounds.intersects(obstacle.getBounds())) {
                    ball.handleObstacleImpact(obstacle);
                }
            }

            // Point marqué
            if (ball.getX() <= 0) {
                gameState.setScorePlayer2(gameState.getScorePlayer2() + 1);
                ball.markForRemoval();
            } else if (ball.getX() + ball.getWidth() >= SCREEN_WIDTH) {
                gameState.setScorePlayer1(gameState.getScorePlayer1() + 1);
                ball.markForRemoval();
            }
            if (ball.isMarkedForRemoval()) ballsToRemove.add(ball);
        }
        balls.removeAll(ballsToRemove);

        // Si toutes les balles sont hors jeu et que la partie n'est pas finie, en ajouter une nouvelle
        if (balls.isEmpty() && gameState.getScorePlayer1() < WINNING_SCORE && gameState.getScorePlayer2() < WINNING_SCORE) {
            addInitialBall(); // Add one at center
            Logger.log("All balls out, adding new initial ball.", Logger.LogType.INFO, "GAMELOGIC");
        } else {
            // Spawn de balle basé sur un timer
            spawnNewBall();
        }

        // Vérifier la condition de fin de partie
        GameMessage<?> message;
        if (gameState.getScorePlayer1() >= WINNING_SCORE || gameState.getScorePlayer2() >= WINNING_SCORE) {
            gameState.setMessage((gameState.getScorePlayer1() >= WINNING_SCORE) ? "Player 1 Wins!" : "Player 2 Wins!");
            gameState.setGameStatus(GAME_OVER);
            message = new GameMessage<>(CommandMessage.GAME_OVER, -2, "", new GameStateDto(this.gameState), GAME_OVER);
            Logger.log("Game Over: " + gameState.getMessage(), Logger.LogType.INFO, "GAMELOGIC");
        } else {
            // Mettre à jour le DTO avec les positions des balles et des paddles
            syncEntitiesToDTO();
            message = new GameMessage<>(CommandMessage.UPDATE_GAME_STATE, -2, "", new GameStateDto(this.gameState), PLAYING);
        }

        // Envoyer l'état mis à jour à tous les clients
        server.broadcast(message);

        //Logger.log("Update game", Logger.LogType.INFO, "Game");
    }

    /**
     * Méthode principale du thread de logique de jeu.
     * Exécute la boucle de mise à jour du jeu à une fréquence définie par SERVER_TPS.
     */
    @Override
    public void run() {
        Logger.log("Game thread started.", Logger.LogType.INFO, "Game");
        final long optimalTime = 1_000_000_000 / SERVER_TPS;
        while (PLAYING.equals(this.gameState.getGameStatus())  ){
            long startTime = System.nanoTime();

            this.update();

            long elapsed = System.nanoTime() - startTime;
            long sleepTime = (optimalTime - elapsed) / 1_000_000; // en millisecondes

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
