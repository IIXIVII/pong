package Game.Ui;

import Common.GameStateDto;
import Common.GameStatus;
import Game.Ui.Style.*;
import Game.PongClientApp;
import javax.swing.*;
import java.awt.*;

abstract class BaseScreen extends JPanel implements Screen{
    protected PongClientApp app;
    private final ScreenName screenName;
    protected GameStateDto currentLocalState;


    public BaseScreen(ScreenName name, PongClientApp app) {
        this.screenName = name;
        this.app = app;
        setFocusable(true);
        this.currentLocalState = new GameStateDto();
        setBackground(UiStyle.BACKGROUND_COLOR);
    }

    @Override
    public ScreenName getScreenName() {return screenName;};
    public JPanel getPanel() {
        return this; // This class itself is the JPanel
    }

    @Override
    public void onShow() {
        repaint();
        // System.out.println(getName() + " is now visible. Requesting focus...");
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }
    @Override
    public void onHide() {
        // System.out.println(getName() + " is now hidden.");
        // Subclasses can override for specific cleanup
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    @Override
    public void updateState(GameStateDto newState, GameStatus currentStatus) {
        this.currentLocalState = newState;
        repaint(); // Redessiner avec le nouvel état
    }
    @Override
    public Dimension getPreferredSize() {
        return UiStyle.WINDOW_DIMENSION;
    }


}
