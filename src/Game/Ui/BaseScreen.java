package Game.Ui;

import Common.GameConfig;
import Common.GameStateDto;
import Game.Ui.Style.*;
import Game.PongClientApp;
import javax.swing.*;
import java.awt.*;

/**
 * Classe de base abstraite pour tous les écrans du jeu.
 * Fournit des fonctionnalités communes comme la gestion de l'état du jeu local,
 * le dessin de fond, et les méthodes de cycle de vie (onShow, onHide).
 */
public abstract class BaseScreen extends JPanel implements Screen{
    protected final PongClientApp app;
    private final ScreenName screenName;
    protected GameStateDto state; // État local du jeu, mis à jour par le serveur

    /**
     * Constructeur pour BaseScreen.
     * @param name Le nom de l'écran.
     * @param app La référence à l'application principale.
     */
    public BaseScreen(ScreenName name, PongClientApp app) {
        this.screenName = name;
        this.app = app;
        setFocusable(true); // Permet à l'écran de recevoir les événements clavier
        this.state = new GameStateDto();
        setBackground(UiStyle.BACKGROUND_COLOR);


    }

    @Override public ScreenName getScreenName() {return screenName;};
    @Override public JPanel getPanel() { return this; }

    /**
     * Appelé lorsque l'écran devient visible.
     */
    @Override
    public void onShow() {
        repaint();
    }

    /**
     * Appelé lorsque l'écran est masqué.
     */
    @Override
    public void onHide() {
        // Par défaut ne fait rien
    }

    /**
     * Méthode de dessin principale pour les composants Swing.
     * @param g Le contexte graphique.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    /**
     * Met à jour l'état local du jeu pour cet écran et redessine l'écran
     * @param newState Le nouvel état du jeu reçu.
     */
    @Override
    public void updateState(GameStateDto newState) {
        this.state = newState;
        repaint(); // Redessiner avec le nouvel état
    }

    /**
     * On fixe la taille de l'écran à celle de la fenêtre
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }


}
