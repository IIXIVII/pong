package Server.Game;
import static Common.GameConfig.*;
import static Common.GameStatus.GAME_OVER;
import static Common.GameStatus.PLAYING;

import Common.GameConfig;
import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.CommandMessage;
import Common.Messages.GameMessage;
import Common.PlayerInput;
import Common.Tools.Logger;
import Game.Ui.Style.UiStyle;
import Server.Game.Entitites.Ball;
import Server.Game.Entitites.Paddle;
import Server.Server;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameLogic implements Runnable{
    GameStateDto gameState;
    Server server;
    private List<Ball> balls;
    private Paddle player1Paddle;
    private Paddle player2Paddle;

    public GameLogic(){
        this.balls = new ArrayList<>();
        this.player1Paddle = new Paddle(1);
        this.player2Paddle = new Paddle(2);
    }

    public void initializeNewGame(GameStateDto gameState, Server server) {
        this.gameState = gameState;
        this.gameState.scorePlayer1 = 0;
        this.gameState.scorePlayer2 = 0;
        player1Paddle.resetPosition();
        player2Paddle.resetPosition();
        addBall();

        this.gameState.message = "Partie en cours!";
        this.server = server;

        syncEntitiesToDTO();
    }

    public void addBall() {
        Ball newBall = new Ball(
                SCREEN_WIDTH / 2 - BALL_DIAMETER / 2,
                SCREEN_HEIGHT / 2 - BALL_DIAMETER / 2
        );
        balls.add(newBall);
    }

    private void syncEntitiesToDTO() {
        this.gameState.player1Y = player1Paddle.getY();
        this.gameState.player2Y = player2Paddle.getY();
        this.gameState.balls.clear();
        for (Ball ball : balls) {
            this.gameState.balls.add(new GameStateDto.BallPosition(ball.x, ball.y));
        }
    }



    public void actionPlayer(PlayerInput input){
        Paddle paddleToMove = (input.playerId == 1) ? player1Paddle : player2Paddle;
        if (PlayerInput.InputType.MOVE_UP.equals(input.type)) {
            paddleToMove.moveUp();
        } else {
            paddleToMove.moveDown();
        }
    }

    public void update() {

        List<Ball> ballsToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.move();

            // Collisions avec les murs haut/bas
            if (ball.y <= 0 || ball.y >= SCREEN_HEIGHT - BALL_DIAMETER) {
                ball.reverseY();
                ball.y = Math.max(0, Math.min(ball.y, SCREEN_HEIGHT - BALL_DIAMETER));
            }

            Rectangle ballBounds = ball.getBounds();

            // Collisions paddles
            // Vérifier collision avec le paddle du joueur 1
            if (ballBounds.intersects(player1Paddle.getBounds())) {
                applyPaddleImpact(ball, player1Paddle);
            }
            // Vérifier collision avec le paddle du joueur 2
            else if (ballBounds.intersects(player2Paddle.getBounds())) {
                applyPaddleImpact(ball, player2Paddle);
            }

            // Point marqué
            if (ball.x <= 0) {
                this.gameState.scorePlayer2++;
                ball.outOfPlay = true; // Marquer pour suppression et réinitialisation
            } else if (ball.x >= SCREEN_WIDTH - BALL_DIAMETER) {
                this.gameState.scorePlayer1++;
                ball.outOfPlay = true;
            }
            if (ball.outOfPlay) ballsToRemove.add(ball);
        }
        balls.removeAll(ballsToRemove);

        // Si toutes les balles sont hors jeu et que la partie n'est pas finie, en ajouter une nouvelle
        if (balls.isEmpty()) {
            addBall();
        }

        GameMessage message;
        // Vérifier condition de victoire

        if (this.gameState.scorePlayer1 >= WINNING_SCORE) {
            this.gameState.message = "Le Joueur 1 a gagné !";
            this.gameState.gameStatus = GAME_OVER;
            message = new GameMessage<>(CommandMessage.GAME_OVER,-2,"",new GameStateDto(this.gameState), GAME_OVER);
        } else if (this.gameState.scorePlayer2 >= WINNING_SCORE) {
            this.gameState.message = "Le Joueur 2 a gagné !";
            this.gameState.gameStatus = GAME_OVER;
            message = new GameMessage<>(CommandMessage.GAME_OVER,-2,"",new GameStateDto(this.gameState), GAME_OVER);
        } else {
            message = new GameMessage<>(CommandMessage.UPDATE_GAME_STATE,-2,"",new GameStateDto(this.gameState), PLAYING);
        }

        // Mettre à jour le DTO avec les positions des balles et des paddles
        syncEntitiesToDTO();
        this.server.broadcast(message);
        Logger.log("Update game", Logger.LogType.INFO, "Game");
    }

    /**
     * Applique l'inversion de direction et modifie dy selon le point d'impact.
     */
    private void applyPaddleImpact(Ball ball, Paddle paddle) {
        // Inversion de la direction X
        ball.reverseX();

        // Calcul du décalage vertical entre la balle et le centre du paddle
        int paddleCenterY = paddle.getY() + GameConfig.PADDLE_HEIGHT / 2;
        int ballCenterY   = ball.getY() + GameConfig.BALL_DIAMETER / 2;
        int deltaY = ballCenterY - paddleCenterY;

        // Normalisation dans [-1, +1]
        double factor = (double) deltaY / ((double) GameConfig.PADDLE_HEIGHT / 2);

        // Ajustement de dy pour dévier la trajectoire
        int speed = GameConfig.INITIAL_BALL_SPEED;
        ball.setDy((int) (speed * factor));

        // Réajustement de dx pour conserver la même magnitude et optionnellement accélérer
        int signX = ball.getDx() > 0 ? 1 : -1;
        ball.setDx(signX * speed);
    }

    @Override
    public void run() {
        Logger.log("Game thread started.", Logger.LogType.INFO, "Game");
        final long optimalTime = 1_000_000_000 / SERVER_TPS;
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
