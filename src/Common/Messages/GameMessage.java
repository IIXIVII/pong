package Common.Messages;

import Common.GameStatus;
import Common.Tools.Logger;

import java.io.Serial;
import java.io.Serializable;
/**
 * Message générique utilisé pour la communication entre le client et le serveur.
 * Il encapsule une commande, un identifiant, un message textuel optionnel,
 * des données spécifiques ({@link ConnectData}, {@link Common.GameStateDto}, {@link Common.PlayerInput}) et le statut actuel du jeu.
 * @param <T> Le type des données transportées par le message, doit être Serializable.
 */
public class GameMessage<T extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static boolean debug = false; // Active/désactive les logs de debug pour les messages

    private final CommandMessage cmd;
    private final int id;
    private final String message;
    private final T data;
    private final GameStatus currentStatus;

    /**
     * Constructeur principal pour GameMessage.
     * @param cmd La commande du message (obligatoire).
     * @param id L'identifiant associé au message.
     * @param message Un message textuel descriptif.
     * @param data Les données spécifiques associées à la commande (peut être null).
     * @param status Le statut actuel du jeu.
     * @throws IllegalArgumentException si cmd est null.
     */
    public GameMessage(CommandMessage cmd, int id, String message, T data, GameStatus status) {
        if (cmd == null) throw new IllegalArgumentException("cmd ne peut pas être null");
        this.cmd = cmd;
        this.id = id;
        this.message = message;
        this.data = data;
        this.currentStatus = status;
    }

    // Getters
    public CommandMessage getCmd()       { return cmd; }
    public int getId()            { return id; }
    public String getMessage()    { return message; }
    public T getData()            { return data; }
    public GameStatus getCurrentStatus() { return currentStatus; }

    /**
     * Enregistre les détails du message dans les logs si le mode debug est activé.
     * @param header Un préfixe pour le message de log (ex: "CLIENT", "SERVER").
     */
    public void log(String header) {
        if (GameMessage.debug) {
            String dataString = (data != null) ? data.toString() : "N/A";
            String messageString = (this.message == null || this.message.isEmpty()) ? "N/A" : this.message;

            Logger.log(
                    "message :" +
                            String.format("ID: %d | CMD: %s | MSG: %s | Specifics: %s",
                                    this.id,
                                    this.cmd,
                                    messageString,
                                    dataString
                            ),
                    Logger.LogType.DEBUG,
                    header);
        }
    }
}