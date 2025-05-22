package Common.Messages;

import java.io.Serializable;

public class ConnectData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String adminKey;
    private int nbConnected;

    // Constructeur
    public ConnectData(String adminKey, int nbConnected) {
        this.adminKey = adminKey;
        this.nbConnected = nbConnected;
    }

    // Constructeur sans-arg (nécessaire pour certaines bibliothèques de sérialisation)
    public ConnectData() {
    }

    // Getters & Setters
    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public int getNbConnected() {
        return nbConnected;
    }

    public void setNbConnected(int nbConnected) {
        this.nbConnected = nbConnected;
    }

    @Override
    public String toString() {
        return "ConnectData{" +
                "adminKey='" + adminKey + '\'' +
                ", nbConnected=" + nbConnected +
                '}';
    }
}
