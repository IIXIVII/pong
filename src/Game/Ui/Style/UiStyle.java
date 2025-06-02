package Game.Ui.Style;

import javax.swing.*;
import java.awt.*;

/**
 * Classe utilitaire pour définir et appliquer des styles graphiques unifiés aux composants
 * Contient des constantes pour les couleurs, les polices, et des méthodes pour styliser les éléments.
 * Cette classe n'est pas destinée à être instanciée.
 */
public class UiStyle {

    // Couleurs
    public static final Color BACKGROUND_COLOR = new Color(10, 25, 10);
    public static final Color DEFAULT_COLOR = new Color(30, 255, 30);
    public static final Color ACCENT_COLOR = new Color(80, 255, 80);

    // Polices
    public static final Font FONT_PRIMARY = new Font("Monospaced", Font.PLAIN, 18);
    public static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 50);
    public static final Font FONT_SUBTITLE = new Font("Monospaced", Font.BOLD, 24);
    public static final Font FONT_BUTTON = new Font("Monospaced", Font.BOLD, 16);
    public static final Font FONT_LABEL = new Font("Monospaced", Font.PLAIN, 14);

    /**
     * Applique un style prédéfini à un bouton.
     * @param button Le JButton à styliser.
     */
    public static void styleButton(JButton button) {
        button.setFont(FONT_BUTTON);
        button.setBackground(BACKGROUND_COLOR);
        button.setForeground(DEFAULT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    /**
     * Applique un style prédéfini à un label.
     * @param label Le JLabel à styliser.
     * @param font La police à appliquer.
     * @param color La couleur du texte à appliquer.
     */
    public static void styleLabel(JLabel label, Font font, Color color) {
        label.setFont(font);
        label.setForeground(color);
    }

    /**
     * Applique un style prédéfini à un champ de texte.
     * @param textField Le JTextField à styliser.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_PRIMARY);
        textField.setBackground(new Color(5, 15, 5));
        textField.setForeground(DEFAULT_COLOR);
        textField.setCaretColor(ACCENT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DEFAULT_COLOR, 1),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
    }
}