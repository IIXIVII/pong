package Game.Ui.Screens;

import Common.GameConfig;
import Game.PongClientApp;
import Game.Ui.BaseScreen;
import Game.Ui.ScreenName;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Écran titre du jeu, premier écran affiché au lancement.
 * Permet à l'utilisateur d'héberger une partie ou de rejoindre une partie existante.
 */
public class TitleScreen extends BaseScreen {
    private final JTextField ipAddressField;

    public TitleScreen(ScreenName screenName, PongClientApp app) {
        super(screenName, app);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Titre
        JLabel titlePart1 = new JLabel("Pong", SwingConstants.CENTER);
        UiStyle.styleLabel(titlePart1, UiStyle.FONT_TITLE, UiStyle.DEFAULT_COLOR);
        add(titlePart1, gbc);

        // Sous titre
        JLabel titlePart2 = new JLabel("Super Ultimate Mega Champion Edition", SwingConstants.CENTER);
        UiStyle.styleLabel(titlePart2, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(titlePart2, gbc);

        // Bouton Héberger
        JButton hostButton = new JButton("HOST GAME");
        UiStyle.styleButton(hostButton);
        hostButton.addActionListener(this::onHostButtonPressed);
        gbc.insets = new Insets(10, 50, 10, 50);
        add(hostButton, gbc);

        // Champ de texte pour l'adresse IP
        ipAddressField = new JTextField("localhost");
        UiStyle.styleTextField(ipAddressField);
        gbc.insets = new Insets(10, 50, 0, 50);
        add(ipAddressField, gbc);

        // Bouton Rejoindre
        JButton joinButton = new JButton("JOIN");
        UiStyle.styleButton(joinButton);
        joinButton.addActionListener(this::onJoinButtonPressed);
        gbc.insets = new Insets(0, 50, 20, 50);
        add(joinButton, gbc);
    }

    /**
     * Gère l'action du bouton "Héberger Partie".
     * Initialise le client en mode hôte et passe à l'écran du lobby.
     */
    private void onHostButtonPressed(ActionEvent e) {
        // L'application principale gère la création du client
        boolean success = app.initializeClient(GameConfig.DEFAULT_SERVER_HOST, GameConfig.DEFAULT_SERVER_PORT, "SUPER_SECRET_ADMIN_KEY", true);
        if (success) {
            app.switchToScreen(ScreenName.LOBBY);

        } else {
            // Afficher un message d'erreur à l'utilisateur si la connexion/création échoue
            JOptionPane.showMessageDialog(this, "Impossible de démarrer le serveur ou de se connecter.", "Erreur Hôte", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère l'action du bouton "Rejoindre Partie".
     * Initialise le client en mode joueur, utilisant l'IP fournie, et passe à l'écran du lobby.
     */
    private void onJoinButtonPressed(ActionEvent e) {
        String ipAddress = ipAddressField.getText().trim();
        if (ipAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une adresse IP.", "Erreur IP", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = app.initializeClient(ipAddress, GameConfig.DEFAULT_SERVER_PORT, "", false);
        if (success) {
            app.switchToScreen(ScreenName.LOBBY);
        } else {
            // Afficher un message d'erreur
            JOptionPane.showMessageDialog(this, "Impossible de se connecter au serveur à l'adresse: " + ipAddress, "Erreur Connexion", JOptionPane.ERROR_MESSAGE);
        }
    }
}
