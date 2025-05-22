package Common.Messages;

import org.json.JSONException;
import org.json.JSONObject;

public class QuitMessage extends GameMessage{

    public static final String CMD = "quit";

    public QuitMessage(int id, String message) {
        super(QuitMessage.CMD,message,id);
    }

    public QuitMessage(JSONObject jsonObject,  int playerId) {
        super(playerId);
        initFromJSONObject(jsonObject);
    }


    @Override
    protected String getSpecificDataString() {
        return "";
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = super.toJSONObject();
        return json;
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {
        super.initFromJSONObject(jsonData);
    }

}
