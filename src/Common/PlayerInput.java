package Common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Représente une action effectuée par un joueur. Immutable.
 */
public class PlayerInput implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Enumération des types d'entrées du joueur
    public enum InputType {
        MOVE_UP,
        MOVE_DOWN,
        STOP_MOVE,
    }

    public InputType type;
    public int playerId; // Pour savoir quel joueur a envoyé l'input

    public PlayerInput(InputType type, int playerId) {
        this.type = type;
        this.playerId = playerId;
    }

    @Override
    public String toString() { return "PlayerInput: " + type.toString() + ", player id:" + playerId; }
}
