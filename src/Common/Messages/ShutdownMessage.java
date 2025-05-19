package Common.Messages;

import org.json.JSONException;
import org.json.JSONObject;

public class ShutdownMessage extends GameMessage {

    public static final String CMD = "shutdown"; // Commande correcte


    public ShutdownMessage(int playerId, String reason) {
        super(ShutdownMessage.CMD, reason, playerId); // reason va dans this.message
    }


    public ShutdownMessage(JSONObject jsonData, int playerIdFromHandler) throws JSONException {
        super(playerIdFromHandler);
        initFromJSONObject(jsonData);
    }


    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("cmd", this.cmd); // this.cmd est "shutdown"
        json.put("id",this.id);

        JSONObject dataObject = new JSONObject();
        dataObject.put("msg", this.getMessage()); // La raison du shutdown

        json.put("data", dataObject);
        return json;
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {
        // playerId est déjà défini par super(playerIdFromHandler)
        this.cmd = jsonData.getString("cmd");
        if (!ShutdownMessage.CMD.equals(this.cmd)) {
            throw new JSONException("CMD mismatch: Expected '" + ShutdownMessage.CMD + "' but got '" + this.cmd + "'");
        }
        this.id = jsonData.optInt("id");
        this.message = jsonData.getString("msg");
    }

    @Override
    protected String getSpecificDataString() {
        // Pour ShutdownMessage, la "raison" est déjà dans getAssociatedMessage().
        // On pourrait ne rien retourner ici ou retourner une chaîne vide.
        return "Reason: " + (this.getMessage() == null ? "N/A" : this.getMessage());
    }
}