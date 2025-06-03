// Gestionnaire de client pour le serveur.
package Server;

import Common.Messages.CommandMessage;
import Common.Messages.GameMessage;
import Common.Tools.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Gestionnaire de client qui traite/ecoute les messages reçus d'un client connecté.
 */
public class ClientHandler implements Runnable {
    // Socket utilisé pour communiquer avec le client.
    private final Socket socket;

    // Référence au serveur qui a créé ce gestionnaire de client.
    private final Server server;

    // Objets de sortie et entrée utilisés pour échanger des données avec le client.
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // État du gestionnaire de client (true si en cours d'exécution, false sinon).
    private volatile boolean running = true;

    // Identifiant attribué au client par le serveur.
    private int id;

    // Indique si le client est un administrateur.
    private boolean isAdmin = false;


    /**
     * Crée un nouveau gestionnaire de client lié à un socket spécifique et associé
     * à un serveur qui le gère. Cette méthode instancie également les objets de sortie
     * et entrée utilisés pour communiquer avec le client.
     *
     * @param socket Socket connectant le client au serveur.
     * @param server Référence au serveur qui a créé ce gestionnaire de client.
     * @throws IOException Si une erreur se produit lors de la création des objets de sortie
     * et entrée ou du sockets.
     */
    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        // Attribue un identifiant unique au client en fonction de la position qu'il occupe
        // dans l'ensemble des clients connectés.
        this.id = this.server.getNbClient();

    }

    @Override
    public void run() {
        try {
            while (running) {
                // Lit un message reçu du client et le transmet au serveur pour traitement.
                GameMessage<?> msg = (GameMessage<?>) in.readObject();
                server.process(this, msg);
            }
        } catch (IOException e) {
            // Gère les erreurs d'entrée/sortie pouvant survenir lors de la lecture des messages.
            Logger.log("Erreur d'entrée/sortie : " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
        } catch (ClassNotFoundException e) {
            // Gère le cas où une classe non définie est rencontrée lors de la désérialisation
            // d'un message.
            Logger.log("Classe non trouvée lors de la désérialisation : " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
        } finally {
            stop(); // Arrête le gestionnaire de client après avoir traité les messages en attente.
        }
    }

    /**
     * Envoie un message au client lié à ce gestionnaire de client. Si une erreur se produit
     * lors de l'envoi, le gestionnaire de client est arrêté et le serveur est notifié pour
     * que les autres clients puissent se connecter.
     *
     * @param message Message à envoyer au client.
     */
    public void send(GameMessage<?> message) {
        try {
            // Écrit le message dans l'objet de sortie et force sa transmission au client.
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            // Gère les erreurs d'entrée/sortie pouvant survenir lors de la transmission du
            // message.
            Logger.log("Erreur envoi message: " + e.getMessage(), Logger.LogType.ERROR, "HANDLER");
            // Envoie un message au serveur pour qu'il peut arrêter le client et libérer les ressources associées.
            server.process(this, new GameMessage<>(CommandMessage.QUIT, this.id, "Déconnexion", null, null));

        }
    }

    /**
     * Arrête le gestionnaire de client en mettant son état à false et ferme la socket
     * utilisée pour communiquer avec le client.
     */
    public void stop() {
        running = false;
        try {
            // Ferme la socket connectant le client au serveur.
            socket.close();
        } catch (IOException ignored) {}
    }

    /**
     * Renvoie l'identifiant attribué à ce gestionnaire de client.
     *
     * @return Identifiant du client.
     */
    public int getId() { return id; }

    /**
     * Indique si le client lié à ce gestionnaire est un administrateur.
     *
     * @return true si le client est un administrateur, false sinon.
     */
    public boolean isAdmin() { return isAdmin; }
    /**
     * Définit si le client lié à ce gestionnaire est un administrateur ou non.
     *
     * @param admin true pour qu'un client soit considéré comme administrateur, false
     * sinon.
     */
    public void setAdmin(boolean admin) { isAdmin = admin; }
}