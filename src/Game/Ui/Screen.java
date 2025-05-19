package Game.Ui;

import Common.GameStateDto;

import javax.swing.*;
import java.awt.*;

public interface Screen {
    ScreenName getScreenName();
    JPanel getPanel();
    void onShow();
    void onHide();
    Dimension getPreferredSize();
    void updateState(GameStateDto state);
}
