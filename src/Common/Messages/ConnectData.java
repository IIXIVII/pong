package Common.Messages;

import java.io.Serial;
import java.io.Serializable;

/**
 * Données spécifiques à la connexion
 * Contient des informations sur l'hôte et le nombre de joueurs connectés.
 */
public class ConnectData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String adminKey;
    private final int nbConnected;

    public ConnectData(String adminKey, int nbConnected) {
        this.adminKey = adminKey;
        this.nbConnected = nbConnected;
    }

    // Getters & Setters
    public String getAdminKey() {
        return adminKey;
    }

    public int getNbConnected() {
        return nbConnected;
    }

    @Override
    public String toString() {
        return "ConnectData{" +
                "adminKey='" + adminKey + '\'' +
                ", nbConnected=" + nbConnected +
                '}';
    }
}
