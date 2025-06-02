package Game.Ui.Screens;

import Common.GameConfig;
import Common.Tools.Logger;
import Game.Client;
import Game.PongClientApp;
import Game.Ui.BaseScreen;
import Game.Ui.ScreenName;
import Game.Ui.Style.UiStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TitleScreen extends BaseScreen {
    private JButton hostButton;
    private JButton joinButton;
    private JTextField ipAddressField;

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

        JLabel titlePart2 = new JLabel("Super Ultimate Mega Champion Edition", SwingConstants.CENTER);
        UiStyle.styleLabel(titlePart2, UiStyle.FONT_SUBTITLE, UiStyle.ACCENT_COLOR);
        add(titlePart2, gbc);

        // Bouton Host
        hostButton = new JButton("HOST GAME");
        UiStyle.styleButton(hostButton);
        hostButton.addActionListener(this::onHostButtonPressed);
        gbc.insets = new Insets(10, 50, 10, 50);
        add(hostButton, gbc);

        // Champ de texte Ip Adress
        ipAddressField = new JTextField("localhost");
        UiStyle.styleTextField(ipAddressField);
        gbc.insets = new Insets(10, 50, 0, 50);
        add(ipAddressField, gbc);

        // Bouton Join
        joinButton = new JButton("JOIN");
        UiStyle.styleButton(joinButton);
        joinButton.addActionListener(this::onJoinButtonPressed);
        gbc.insets = new Insets(0, 50, 20, 50);
        add(joinButton, gbc);
    }

    private void onHostButtonPressed(ActionEvent e) {
        app.client = Client.getInstance(app, GameConfig.DEFAULT_SERVER_HOST, GameConfig.DEFAULT_SERVER_PORT, "SUPER_SECRET_ADMIN_KEY", true);
        Logger.log("jbiefz", Logger.LogType.INFO,"fezion");
        app.switchToScreen(ScreenName.LOBBY);
        app.screenManager.updateScreens(app.gameState);
        return;
    }

    private void onJoinButtonPressed(ActionEvent e) {
        app.client = Client.getInstance(this.app, this.ipAddressField.getText(),GameConfig.DEFAULT_SERVER_PORT,"",false);
        app.switchToScreen(ScreenName.LOBBY);
        app.screenManager.updateScreens(app.gameState);
    }
}
