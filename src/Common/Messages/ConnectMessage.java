package Common.Messages;


import org.json.JSONObject;
import org.json.JSONException;

public class ConnectMessage extends GameMessage {

    public static final String CMD = "connect"; // La commande spécifique

    private String adminKey = "";
    private Integer nbconnected = 0;



    public ConnectMessage(int id, String adminKey, String message, Integer nbconnected) {
        super(ConnectMessage.CMD, message, id);

        this.adminKey = adminKey;
        this.nbconnected = nbconnected;
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

    public Integer getNbconnected(){return nbconnected;};


    @Override
    public JSONObject toJSONObject() throws JSONException {

        JSONObject json = super.toJSONObject();

        JSONObject dataObject = new JSONObject();

        if (this.adminKey != null) {
            // Attention à ne pas renvoyer la clé admin aux autres clients si ce message était broadcasté.
            // Pour un message client -> serveur, c'est ok.
            dataObject.put("admin_key", this.adminKey);
        }

        dataObject.put("nbconnected",this.nbconnected);

        json.put("data", dataObject);
        return json;
    }

    @Override
    protected void initFromJSONObject(JSONObject jsonData) throws JSONException {

        super.initFromJSONObject(jsonData);

        JSONObject dataPart = jsonData.optJSONObject("data");
        if (dataPart != null) {

            this.adminKey = dataPart.optString("admin_key", null);
            this.nbconnected = dataPart.getInt("nbconnected");

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
        sb.append("Nombre de connecté : " + this.nbconnected);
        return sb.toString().trim();
    }
}