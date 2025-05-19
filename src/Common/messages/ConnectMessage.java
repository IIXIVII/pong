package Common.messages;


import org.json.JSONObject;
import org.json.JSONException;

public class ConnectMessage extends GameMessage {

    public static final String CMD = "connect"; // La commande spécifique

    private String adminKey = "";



    public ConnectMessage(int id, String adminKey, String message) {
        super(ConnectMessage.CMD, message, id);

        this.adminKey = adminKey;
    }


    public ConnectMessage( String adminKey) {
        super(ConnectMessage.CMD, "Demande de connexion", -1); // -1 indique un ID non encore assigné
        this.adminKey = adminKey;
    }


    public ConnectMessage(JSONObject jsonData, int playerIdFromHandler) throws JSONException {
        super(playerIdFromHandler); // Stocke l'ID du handler
        initFromJSONObject(jsonData);
    }

    public String getAdminKey() {
        return adminKey;
    }

    public boolean hasAdminKey() {
        return adminKey != null && !adminKey.isEmpty();
    }



    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("cmd", this.cmd); // this.cmd est "connect"
        // On ne met pas le playerId dans le JSON sortant si c'est le client qui envoie,
        // car il ne le connaît pas encore. Le serveur l'associera.
        // Si le serveur envoyait ce message (improbable), il mettrait le playerId.
        json.put("id",this.id);
        JSONObject dataObject = new JSONObject();

        if (this.adminKey != null) {
            // Attention à ne pas renvoyer la clé admin aux autres clients si ce message était broadcasté.
            // Pour un message client -> serveur, c'est ok.
            dataObject.put("admin_key", this.adminKey);
        }
        if (this.getMessage() != null) {
            json.put("msg", this.getMessage());
        }

        json.put("data", dataObject);
        return json;
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {
        // playerId est déjà défini par super(playerIdFromHandler)
        this.cmd = jsonData.getString("cmd");
        if (!ConnectMessage.CMD.equals(this.cmd)) {
            throw new JSONException("CMD mismatch: Expected '" + ConnectMessage.CMD + "' but got '" + this.cmd + "'");
        }
        this.message = jsonData.optString("msg", null); // Le message associé
        this.id = jsonData.optInt("id");
        JSONObject dataPart = jsonData.optJSONObject("data");
        if (dataPart != null) {

            this.adminKey = dataPart.optString("admin_key", null);

        } else {
            // Si "data" est absent, les champs optionnels restent null

            this.adminKey = null;
            this.message = jsonData.optString("msg", null); // Fallback si msg est au niveau racine
        }
    }

    @Override
    protected String getSpecificDataString() {
        StringBuilder sb = new StringBuilder();
        if (hasAdminKey()) sb.append("HasAdminKey: true "); // Ne pas logger la clé elle-même
        return sb.toString().trim();
    }
}