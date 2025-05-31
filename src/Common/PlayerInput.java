package Common;

import java.io.Serializable;

public class PlayerInput implements Serializable {
    public enum InputType {
        MOVE_UP_PRESSED,
        MOVE_UP_RELEASED,
        MOVE_DOWN_PRESSED,
        MOVE_DOWN_RELEASED
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
