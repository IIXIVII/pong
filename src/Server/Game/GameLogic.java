package Server.Game;

import Common.GameStateDto;
import Common.GameStatus;
import Game.Ui.Style.UiStyle;
import Server.Game.Entitites.Ball;
import Server.Game.Entitites.Paddle;
import Server.Server;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameLogic {
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

    public void initializeNewGame(GameStateDto state) {
        state.scorePlayer1 = 0;
        state.scorePlayer2 = 0;
        player1Paddle.resetPosition();
        player2Paddle.resetPosition();
        addBall();

        state.message = "Partie en cours!";

        syncEntitiesToDTO(state);
    }

    public void addBall() {
        Ball newBall = new Ball(
                UiStyle.WINDOW_DIMENSION.width / 2 - UiStyle.BALL_DIAMETER / 2,
                UiStyle.WINDOW_DIMENSION.height/ 2 - UiStyle.BALL_DIAMETER / 2
        );
        balls.add(newBall);
    }

    private void syncEntitiesToDTO(GameStateDto state) {
        state.player1Y = player1Paddle.getY();
        state.player2Y = player2Paddle.getY();
        state.balls.clear();
        for (Ball ball : balls) {
            state.balls.add(new GameStateDto.BallPosition(ball.x, ball.y));
        }
    }

    public void movePaddle(int playerId, boolean up) {
        Paddle paddleToMove = (playerId == 1) ? player1Paddle : player2Paddle;
        if (up) {
            paddleToMove.moveUp();
        } else {
            paddleToMove.moveDown();
        }
    }

    public void update(GameStateDto state) {

        List<Ball> ballsToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.move();

            // Collisions avec les murs haut/bas
            if (ball.y <= 0 || ball.y >= UiStyle.WINDOW_DIMENSION.height - UiStyle.BALL_DIAMETER) {
                ball.reverseY();
                ball.y = Math.max(0, Math.min(ball.y, UiStyle.WINDOW_DIMENSION.height - UiStyle.BALL_DIAMETER));
            }

            Rectangle ballBounds = ball.getBounds();

            // Collisions paddles
            if (ballBounds.intersects(player1Paddle.getBounds())) {
                ball.x = player1Paddle.getX() + UiStyle.BALL_DIAMETER; // Ajustement pour éviter de coller
                ball.reverseX();
            } else if (ballBounds.intersects(player2Paddle.getBounds())) {
                ball.x = player2Paddle.getX() - UiStyle.BALL_DIAMETER; // Ajustement
                ball.reverseX();
            }

            // Point marqué
            if (ball.x <= 0) {
                state.scorePlayer2++;
                ball.outOfPlay = true; // Marquer pour suppression et réinitialisation
            } else if (ball.x >= UiStyle.WINDOW_DIMENSION.width - UiStyle.BALL_DIAMETER) {
                state.scorePlayer1++;
                ball.outOfPlay = true;
            }
            if (ball.outOfPlay) ballsToRemove.add(ball);
        }
        balls.removeAll(ballsToRemove);

        // Si toutes les balles sont hors jeu et que la partie n'est pas finie, en ajouter une nouvelle
        if (balls.isEmpty()) {
            addBall();
        }

        // Vérifier condition de victoire
        if (state.scorePlayer1 >= gameState.winningScore) {

            state.message = "Le Joueur 1 a gagné !";
        } else if (state.scorePlayer2 >= gameState.winningScore) {

            state.message = "Le Joueur 2 a gagné !";
        }

        // Mettre à jour le DTO avec les positions des balles et des paddles
        syncEntitiesToDTO(state);
    }
}
