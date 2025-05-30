package Game.Ui;

import Common.GameConfig;
import Common.GameStateDto;
import Common.GameStatus;
import Game.Ui.Style.*;
import Game.PongClientApp;
import javax.swing.*;
import java.awt.*;

public abstract class BaseScreen extends JPanel implements Screen{
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
        return this;
    }

    @Override
    public void onShow() {
        repaint();
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
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    @Override
    public void updateState(GameStateDto newState) {
        this.currentLocalState = newState;
        repaint(); // Redessiner avec le nouvel état
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }


}
