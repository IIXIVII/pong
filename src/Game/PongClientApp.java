/**
 * PongClientApp est la classe principale du client Pong.
 * Elle initialise l'interface utilisateur, gère la connexion au serveur,
 * et orchestre la boucle de jeu en fonction des mises à jour du serveur.
 */
package Game;

import Common.GameStateDto;
import Common.GameStatus;
import Common.Messages.GameMessage;
import Common.Tools.Logger;
import Game.Ui.Screen;
import Game.Ui.ScreenManager;
import Game.Ui.ScreenName;
import Game.Ui.Screens.EndingScreen;
import Game.Ui.Screens.GameScreen;
import Game.Ui.Screens.LobbyScreen;
import Game.Ui.Screens.TitleScreen;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

import static Common.GameConfig.SERVER_TPS;
import static Game.Ui.ScreenName.*;



public class PongClientApp {
    private JFrame frame; //Fenêtre principale de l'application.
    public ScreenManager screenManager; //Gestionnaire des écrans (UI).
    public Client client; //Client réseau pour communiquer avec le serveur Pong.
    public boolean playingState = false; //Indique si le client est actuellement en phase de jeu.

    public GameStateDto gameState = new GameStateDto(); //État actuel du jeu reçu du serveur.
    private Timer gameLoopTimer; //Timer Swing pour la boucle de mise à jour.


    /**
     * Constructeur : initialise la fenêtre, les écrans, et affiche l'écran titre.
     */
    public PongClientApp() {
        frame = new JFrame("PONG");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.quit();
                }
                System.out.println("Application closing.");
                frame.dispose();
                System.exit(0);
            }
        });

        this.screenManager = new ScreenManager(frame);
        initializeAllScreens();

        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Affiche l'écran titre au démarrage
        this.screenManager.switchTo(TITLE);
    }

    /**
     * Enregistre tous les écrans (Title, Lobby, Game, Ending) auprès du ScreenManager.
     */
    private void initializeAllScreens() {
        screenManager.register(new TitleScreen(ScreenName.TITLE, this));
        screenManager.register(new LobbyScreen(ScreenName.LOBBY, this));
        screenManager.register(new GameScreen(ScreenName.GAME, this));
        screenManager.register(new EndingScreen(ScreenName.ENDING, this));
    }

    /**
     * Initialise le client réseau.
     * @param host L'adresse du serveur.
     * @param port Le port du serveur.
     * @param adminKey La clé d'admin (si hôte).
     * @param createServer True si ce client doit aussi créer le serveur.
     * @return true si l'initialisation a réussi, false sinon.
     */
    public boolean initializeClient(String host, int port, String adminKey, boolean createServer) {
        String newHost = host;
        if (this.client != null) {
            this.client.quit();
        }
        try {
            this.client = Client.getInstance(this, newHost, port, adminKey, createServer);
            this.gameState.setGameStatus(GameStatus.CONNECTING); // Mettre à jour l'état local
            return true;
        } catch (RuntimeException e) {
            Logger.log("Échec de l'initialisation du client: " + e.getMessage(), Logger.LogType.ERROR, "CLIENT_APP");
            this.client = null;
            this.gameState.setGameStatus(GameStatus.ERROR);
            this.gameState.setMessage("Erreur: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bascule vers un écran donné et délègue la logique spécifique à chaque type d'écran.
     *
     * @param screenName nom de l'écran cible.
     */
    public void switchToScreen(ScreenName screenName) {
        screenManager.updateScreens(this.gameState);
        screenManager.switchTo(screenName);
        Screen current = screenManager.getCurrentScreen();

        if (current instanceof GameScreen) {
            handleSwitchToGameScreen();
        }
        else if (current instanceof LobbyScreen) {
            handleSwitchToLobbyScreen();
        }
        else {
            handleSwitchToOtherScreens();
        }
    }

    /**
     * Logique à exécuter quand on passe à l'écran de jeu (GameScreen).
     * - Si StartTargetTime est null, lance directement la boucle.
     * - Sinon, calcule le délai jusqu'au début, démarre un compte à rebours si nécessaire.
     */
    private void handleSwitchToGameScreen() {
        LocalDateTime now = LocalDateTime.now();

        // Si StartTargetTime non initialisé → jeu direct
        if (gameState.getStartTargetTime() == null) {
            Logger.log("StartTargetTime est null, lancement direct du jeu.", Logger.LogType.ERROR, "CLIENTAPP");
            gameState.playing = true;
            startGameLoop();
            return;
        }

            long totalMillis = java.time.Duration.between(now, this.gameState.getStartTargetTime()).toMillis();

        // Si déjà en retard, démarrer la boucle immédiatement
        if (totalMillis <= 0) {
            gameState.playing = true;
            startGameLoop();
            Logger.log("1gameState.playing = " + gameState.playing, Logger.LogType.INFO, "CLIENTAPP");
            Logger.log("2gameState.playing = " + gameState.playing, Logger.LogType.INFO, "CLIENTAPP");
            return;
        }

        // Sinon, lancer un compte à rebours avant le début
        startCountdownAndGameLoop((int) (totalMillis / 1000));
    }

    /**
     * Démarre un Timer Swing pour un compte à rebours, puis lance la boucle de jeu.
     *
     * @param secondsInitial Nombre de secondes à décompter.
     */
    private void startCountdownAndGameLoop(int secondsInitial) {
        final int[] remaining = { secondsInitial };
        Logger.log("Début du compte à rebours : " + remaining[0] + "s", Logger.LogType.INFO, "CLIENTAPP");

        Timer countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            if (remaining[0] > 0) {
                System.out.println("Début dans " + remaining[0] + "s");
                remaining[0]--;
            } else {
                countdownTimer.stop();
                System.out.println("GO !");
                startGameLoop();
            }
        });
        countdownTimer.start();

        // On peut déjà mettre playing=true pour indiquer que la partie va démarrer
        gameState.playing = true;
        Logger.log("1gameState.playing = " + gameState.playing, Logger.LogType.INFO, "CLIENTAPP");
        Logger.log("2gameState.playing = " + gameState.playing, Logger.LogType.INFO, "CLIENTAPP");
    }

    /**
     * Logique à exécuter quand on passe à l'écran de lobby (LobbyScreen).
     * Démarre immédiatement la boucle de jeu pour écouter le serveur.
     */
    private void handleSwitchToLobbyScreen() {
        startGameLoop();
    }

    /**
     * Logique à exécuter pour tous les autres écrans (Title, Ending, etc.).
     * Il suffit d'arrêter la boucle de jeu si elle tourne.
     */
    private void handleSwitchToOtherScreens() {
        stopGameLoop();
    }


    /**
     * Démarre la boucle de jeu qui interroge le serveur à intervalle fixe (SERVER_TPS).
     */
    private void startGameLoop() {
        gameLoopTimer = new Timer(SERVER_TPS, e -> handleServerUpdate());
        Logger.log("Client Prepare la loop",Logger.LogType.DEBUG,"CLIENTAPP");
        gameLoopTimer.start();
        Logger.log("Client execute la loop",Logger.LogType.DEBUG,"CLIENTAPP");
    }

    /**
     * Arrête la boucle de jeu si elle est active.
     */
    private void stopGameLoop() {
        if (gameLoopTimer != null) {
            Logger.log("Client arrete la loop",Logger.LogType.DEBUG,"CLIENTAPP");
            gameLoopTimer.stop();
            playingState = false;
        }
    }


    /**
     * Gère les mises à jour reçues du serveur, en trois phases :
     *  1. récupération et traitement des messages,
     *  2. mise à jour de l'état de jeu et des écrans,
     *  3. transition d'écran selon le statut.
     */
    private void handleServerUpdate() {
        // 1) Récupérer et traiter tous les messages en attente
        GameMessage<?> lastMsg = processIncomingMessages();

        // 2) Mettre à jour l'UI sur le thread EDT
        SwingUtilities.invokeLater(() -> {
            if (lastMsg != null) {
                updateGameStateAndScreens(lastMsg);
                handleStatusTransition(lastMsg);
            }
        });
    }

    /**
     * Parcourt tous les messages en attente, les traite via client.process(...)
     * et renvoie le dernier message reçu (ou null si aucun message).
     */
    private GameMessage<?> processIncomingMessages() {
        GameMessage<?> msg = client.pollResponse();
        GameMessage<?> lastMsg = null;

        while (msg != null) {
            client.process(msg);
            lastMsg = msg;
            msg = client.pollResponse();
        }

        return lastMsg;
    }

    /**
     * Si le dernier message contient un GameStateDto, met à jour gameState
     * et actualise les écrans via screenManager.updateScreens(...).
     */
    private void updateGameStateAndScreens(GameMessage<?> lastMsg) {
        if (lastMsg.getData() instanceof GameStateDto) {
            GameStateDto newState = (GameStateDto) lastMsg.getData();
            this.gameState = newState;
            screenManager.updateScreens(gameState);
        }
    }

    /**
     * Lit le statut du dernier message et bascule vers l'écran approprié.
     */
    private void handleStatusTransition(GameMessage<?> lastMsg) {
        switch (lastMsg.getCurrentStatus()) {
            case WELCOME:
                switchToScreen(TITLE);
                break;

            case LOBBY_WAITING:
            case LOBBY_READY_TO_START:
                switchToScreen(LOBBY);
                break;

            case PLAYING:
                if (!playingState) {
                    playingState = true;
                    switchToScreen(GAME);
                }
                break;

            case GAME_OVER:
                switchToScreen(ENDING);
                break;

            default:
                // Pour CONNECTING ou autres statuts inconnus : ne rien faire
                break;
        }
    }


    /**
     * Point d'entrée de l'application : démarre l'application sur le thread EDT.
     *
     * @param args arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongClientApp::new);
    }
}
