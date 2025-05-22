package Server.Game;

import Common.GameStateDto;
import Common.GameStatus;
import Server.Server;

import java.time.LocalDateTime;


public class GameLogic {
    GameStateDto gameState;
    Server server;

    public GameLogic(Server server){
        this.server = server;

        gameState = new GameStateDto();
        gameState.player1Y = server.getClient(0).getId();
        gameState.player2Y = server.getClient(1).getId();

        gameState.canStartGame = false;
        gameState.connectedPlayers = server.getNbClient();
        gameState.currentStatus = GameStatus.PLAYING;
        gameState.winningScore = 0;
        gameState.scorePlayer1 = 0;
        gameState.scorePlayer2 = 0;
        gameState.StartTargetTime = LocalDateTime.now().plusSeconds(5);
        gameState.message ="";


    }


    public GameStateDto getGameState() {
        return gameState;
    }
}
