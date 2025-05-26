package Common.Messages;

import Common.GameStatus;
import Common.Tools.Logger;

import java.io.Serializable;

public class GameMessage<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static boolean debug = false;



    private CommandMessage cmd;
    private int id;
    private String message;      // optionnel, usage libre
    private T data;              // le payload typé

    public GameStatus currentStatus;

    public GameMessage(CommandMessage startGame, int i, String leJeuCommence, GameStatus playing) { /* constructeur sans-arg pour sérialisation */ }

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

    // Pour le debug
    public void log(String header) {
        if (GameMessage.debug) {
            String otherdata = (data != null) ? this.data.toString() : "";

            Logger.log(
                    "message :" +
                    String.format("ID: %d | CMD: %s | MSG: %s | Specifics: %s",
                            this.id,
                            this.cmd,
                            this.message == null ? "N/A" : this.message,// Méthode à implémenter dans les sous-classes pour les détails
                            otherdata
                    ),
                    Logger.LogType.DEBUG,
                    header);
        }
    }
}
