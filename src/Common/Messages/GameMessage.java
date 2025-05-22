package Common.Messages;

import org.json.JSONObject;
import org.json.JSONException;
import Common.Tools.Logger; // Assure-toi que ta classe Logger est accessible

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


    public JSONObject toJSONObject() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("cmd", this.cmd); // this.cmd est "connect"
        // On ne met pas le playerId dans le JSON sortant si c'est le client qui envoie,
        // car il ne le connaît pas encore. Le serveur l'associera.
        // Si le serveur envoyait ce message (improbable), il mettrait le playerId.
        json.put("id",this.id);


        if (this.getMessage() != null) {
            json.put("msg", this.getMessage());
        }
        return json;
    };


    protected void initFromJSONObject(JSONObject jsonData) throws JSONException{
        // playerId est déjà défini par super(playerIdFromHandler)
        this.cmd = jsonData.getString("cmd");
        if (!ConnectMessage.CMD.equals(this.cmd)) {
            throw new JSONException("CMD mismatch: Expected '" + ConnectMessage.CMD + "' but got '" + this.cmd + "'");
        }
        this.message = jsonData.optString("msg", null); // Le message associé
        this.id = jsonData.optInt("id");

    };
}