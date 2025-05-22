package Common.Messages;

import org.json.JSONException;
import org.json.JSONObject;

public class StartGameMessage extends GameMessage{

    public static final String CMD = "startgame";

    public StartGameMessage(int id,String message){
        super(StartGameMessage.CMD,message,id);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        return super.toJSONObject();
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {
        super.initFromJSONObject(jsonData);
    }

    @Override
    protected String getSpecificDataString() {
        return "";
    }
}
