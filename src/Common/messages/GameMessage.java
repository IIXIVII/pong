package Common.messages;

import org.json.JSONObject;
import org.json.JSONException;
import Common.Logger; // Assure-toi que ta classe Logger est accessible

public abstract class GameMessage {
    public static boolean debug = false; // Pour activer/désactiver les logs de message
    protected String cmd;
    protected String message;
    protected int id;


    public GameMessage(String cmd, String message, int id) {
        if (cmd == null || cmd.trim().isEmpty()) {
            throw new IllegalArgumentException("Command 'cmd' cannot be null or empty.");
        }
        this.cmd = cmd;
        this.message = message;
        this.id = id;
    }


    protected GameMessage(int id) {
        this.id = id;
        // cmd et message seront définis par initFromJSONObject dans la sous-classe
    }


    public String getCmd() {
        return cmd;
    }

    public String getMessage() { // Renommé pour éviter confusion avec le "message" global
        return message;
    }

    public int getId() {
        return id;
    }

    public void log(String header) {
        if (GameMessage.debug) {
            Logger.log(
                    String.format("ID: %d | CMD: %s | MSG: %s | Specifics: %s",
                            this.id,
                            this.getCmd(),
                            this.getMessage() == null ? "N/A" : this.getMessage(),
                            this.getSpecificDataString() // Méthode à implémenter dans les sous-classes pour les détails
                    ),
                    Logger.LogType.DEBUG,
                    header);
        }
    }


    protected abstract String getSpecificDataString();


    public abstract JSONObject toJSONObject() throws JSONException;


    protected abstract void initFromJSONObject(JSONObject jsonData) throws JSONException;
}