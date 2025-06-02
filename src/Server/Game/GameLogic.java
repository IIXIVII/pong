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
import java.util.concurrent.ThreadLocalRandom;

import static Common.GameConfig.*;
import static Common.GameStatus.GAME_OVER;
import static Common.GameStatus.PLAYING;


public class GameLogic implements Runnable{
    GameStateDto gameState;
    Server server;
    private List<Ball> balls;
    private Paddle player1Paddle;
    private Paddle player2Paddle;
    private List<GameObject> obstacles;

    private long lastBallSpawnTime;
    private static final Random random = ThreadLocalRandom.current();

    public GameLogic(){
        this.balls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.player1Paddle = new Paddle(1);
        this.player2Paddle = new Paddle(2);
    }

    public void initializeNewGame(GameStateDto gameState, Server server) {
        this.gameState = gameState;
        this.server = server;

        this.gameState.scorePlayer1 = 0;
        this.gameState.scorePlayer2 = 0;
        this.gameState.message = "Partie en cours!";

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

    private void addInitialBall() {
        Ball newBall = new Ball(
                SCREEN_WIDTH / 2 - BALL_DIAMETER / 2,
                SCREEN_HEIGHT / 2 - BALL_DIAMETER / 2
        );
        balls.add(newBall);
    }

    public void spawnNewBall() {
        if (balls.size() < MAX_BALLS &&
                (System.currentTimeMillis() - lastBallSpawnTime) > BALL_SPAWN_INTERVAL_SECONDS * 1000) {

            Ball newBall = new Ball(
                    SCREEN_WIDTH / 2 - BALL_DIAMETER / 2,
                    SCREEN_HEIGHT / 2 - BALL_DIAMETER / 2
            );
            balls.add(newBall);
            lastBallSpawnTime = System.currentTimeMillis();
        }
    }

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

    private boolean isValidObstaclePosition(GameObject obstacle) {
        Rectangle obsBounds = obstacle.getBounds();

        // Check distance from paddles' initial columns
        if (obsBounds.getMaxX() > (PADDLE_OFFSET_X - OBSTACLE_MIN_DISTANCE_FROM_PADDLE) &&
                obsBounds.x < (PADDLE_OFFSET_X + PADDLE_WIDTH + OBSTACLE_MIN_DISTANCE_FROM_PADDLE)) {
            return false; // Too close to P1
        }
        if (obsBounds.getMaxX() > (SCREEN_WIDTH - PADDLE_OFFSET_X - PADDLE_WIDTH - OBSTACLE_MIN_DISTANCE_FROM_PADDLE) &&
                obsBounds.x < (SCREEN_WIDTH - PADDLE_OFFSET_X + OBSTACLE_MIN_DISTANCE_FROM_PADDLE)) {
            return false; // Too close to P2
        }

        // Check distance from center spawn area (horizontally)
        int centerX = SCREEN_WIDTH / 2;
        if (obsBounds.getMaxX() > (centerX - OBSTACLE_MIN_DISTANCE_FROM_CENTER_X) &&
                obsBounds.x < (centerX + OBSTACLE_MIN_DISTANCE_FROM_CENTER_X)) {
            return false; // Too close to center X
        }

        // Check for overlap with other existing obstacles
        for (GameObject existing : obstacles) {
            if (obsBounds.intersects(existing.getBounds())) {
                return false;
            }
        }
        return true;
    }


    private void syncEntitiesToDTO() {
        this.gameState.player1Y = player1Paddle.getY();
        this.gameState.player2Y = player2Paddle.getY();

        this.gameState.balls.clear();
        for (Ball ball : balls) {
            this.gameState.balls.add(new GameStateDto.BallPosition(ball.getX(), ball.getY()));
        }

        this.gameState.obstacles.clear();
        for (GameObject  obstacle : obstacles) {
            this.gameState.obstacles.add(new GameStateDto.ObstaclePosition(obstacle.getX(), obstacle.getY()));
        }
    }

    public void actionPlayer(PlayerInput input) {
        Paddle paddleToMove = (input.playerId == 1) ? player1Paddle : player2Paddle;
        paddleToMove.handlePlayerAction(input.type);
    }

    public void update() {
        player1Paddle.update();
        player2Paddle.update();

        for (GameObject obstacle : obstacles) {
            obstacle.update();
        }
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
                gameState.scorePlayer2++;
                ball.outOfPlay = true;
            } else if (ball.getX() + ball.getWidth() >= SCREEN_WIDTH) {
                gameState.scorePlayer1++;
                ball.outOfPlay = true;
            }
            if (ball.outOfPlay) ballsToRemove.add(ball);
        }
        balls.removeAll(ballsToRemove);

        // Si toutes les balles sont hors jeu et que la partie n'est pas finie, en ajouter une nouvelle
        if (balls.isEmpty() && gameState.scorePlayer1 < WINNING_SCORE && gameState.scorePlayer2 < WINNING_SCORE) {
            addInitialBall(); // Add one at center
            Logger.log("All balls out, adding new initial ball.", Logger.LogType.INFO, "GAMELOGIC");
        } else {
            // Spawn de balle basé sur un timer
            spawnNewBall();
        }


        GameMessage<?> message;
        if (gameState.scorePlayer1 >= WINNING_SCORE || gameState.scorePlayer2 >= WINNING_SCORE) {
            gameState.message = (gameState.scorePlayer1 >= WINNING_SCORE) ? "Player 1 Wins!" : "Player 2 Wins!";
            gameState.gameStatus = GAME_OVER;
            message = new GameMessage<>(CommandMessage.GAME_OVER, -2, "", new GameStateDto(this.gameState), GAME_OVER);
            Logger.log("Game Over: " + gameState.message, Logger.LogType.INFO, "GAMELOGIC");
        } else {
            // Mettre à jour le DTO avec les positions des balles et des paddles
            syncEntitiesToDTO();
            message = new GameMessage<>(CommandMessage.UPDATE_GAME_STATE, -2, "", new GameStateDto(this.gameState), PLAYING);
        }

        server.broadcast(message);

        Logger.log("Update game", Logger.LogType.INFO, "Game");
    }

    @Override
    public void run() {
        Logger.log("Game thread started.", Logger.LogType.INFO, "Game");
        final long optimalTime = 1_000_000_000 / SERVER_TPS;
        gameState.playing = true;

        while (PLAYING.equals(this.gameState.gameStatus)  ){
            long startTime = System.nanoTime();

            this.update();

            // TODO: envoyer l'état à l'affichage ou aux clients ici (via websocket, etc.)

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
