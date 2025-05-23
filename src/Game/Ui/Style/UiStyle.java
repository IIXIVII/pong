package Game.Ui.Style;

import javax.swing.*;
import java.awt.*;

public class UiStyle {
    public static final Color BACKGROUND_COLOR = new Color(10, 25, 10);
    public static final Color DEFAULT_COLOR = new Color(30, 255, 30);
    public static final Color ACCENT_COLOR = new Color(80, 255, 80);
    // CURSOR_COLOR not used in this context, can be removed if not needed elsewhere

    public static final Font FONT_PRIMARY = new Font("Monospaced", Font.PLAIN, 18);
    public static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 50);
    public static final Font FONT_SUBTITLE = new Font("Monospaced", Font.BOLD, 24);
    public static final Font FONT_BUTTON = new Font("Monospaced", Font.BOLD, 16);
    public static final Font FONT_LABEL = new Font("Monospaced", Font.PLAIN, 14);

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

    public static void styleLabel(JLabel label, Font font, Color color) {
        label.setFont(font);
        label.setForeground(color);
    }

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