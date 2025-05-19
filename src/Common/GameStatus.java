package Common;

public enum GameStatus {
    WELCOME, // État initial du client avant connexion/host
    CONNECTING, // Le client essaie de se connecter
    LOBBY_WAITING, // Connecté, en attente d'autres joueurs (ou que l'hôte démarre)
    LOBBY_READY_TO_START, // Assez de joueurs, l'hôte peut démarrer
    PLAYING, // En jeu
    GAME_OVER, // Fin de partie
    ERROR // Erreur de connexion ou autre
}
