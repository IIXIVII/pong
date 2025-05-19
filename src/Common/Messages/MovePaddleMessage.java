package Common.Messages;

import org.json.JSONObject;
import org.json.JSONException;

public class MovePaddleMessage extends GameMessage {
    public static final String CMD = "move_paddle"; // La commande spécifique
    private int yPosition;


    public MovePaddleMessage(int playerId, int yPosition, String message) {
        super(MovePaddleMessage.CMD, message, playerId);
        this.yPosition = yPosition;
    }


    public MovePaddleMessage(JSONObject jsonData, int playerIdFromHandler) throws JSONException {
        super(playerIdFromHandler);
        initFromJSONObject(jsonData);
    }

    public int getYPosition() {
        return yPosition;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("cmd", this.cmd); // cmd est "move_paddle"
        json.put("msg",this.message);

        JSONObject dataObject = new JSONObject();
        dataObject.put("y_position", this.yPosition);


        json.put("data", dataObject);
        return json;
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {
        // playerId est déjà défini par super(playerIdFromHandler)
        this.cmd = jsonData.getString("cmd"); // Vérifier que c'est bien MovePaddleMessage.CMD
        if (!MovePaddleMessage.CMD.equals(this.cmd)) {
            throw new JSONException("CMD mismatch: Expected '" + MovePaddleMessage.CMD + "' but got '" + this.cmd + "'");
        }

        JSONObject dataPart = jsonData.optJSONObject("data");
        if (dataPart == null) {
            throw new JSONException("Missing 'data' field for MovePaddleMessage.");
        }
        this.yPosition = dataPart.getInt("y_position"); // Note: clé "y_position"

        // Le champ 'message' de GameMessage peut être peuplé si pertinent
        // this.message = dataPart.optString("msg", null); // Si le message était dans data
        this.message = jsonData.optString("msg", null); // Si le message était au niveau racine (moins courant pour data structuré)

    }

    @Override
    protected String getSpecificDataString() {
        return String.format("yPosition: %d", this.yPosition);
    }
}