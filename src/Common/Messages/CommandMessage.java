package Common.Messages;

/**
 * Énumération des commandes possibles dans les messages serveurs.
 * Ces commandes dictent l'action à entreprendre ou l'information transmise.
 */
public enum CommandMessage {
    CONNECT,          // Connexion au serveur
    INFO_SERVER,      // Message d'information générique du serveur
    SHUTDOWN,         // Déconnexion des joueurs + arrêt du serveur
    QUIT,             // Déconnexion d'un client
    START_GAME,       // Démarrage de la partie
    UPDATE_GAME_STATE,// Mise à jour de l'état du jeu envoyée par le serveur
    ACTION_PLAYER,    // Action effectuée par un joueur
    GAME_OVER         // Notification que la partie est terminée
}
