package Game.Ui;

import Common.GameStateDto;

import javax.swing.*;
import java.awt.*;

public interface Screen {
    ScreenName getScreenName();
    JPanel getPanel();
    void onShow();
    void onHide();
    void updateState(GameStateDto state);
}
