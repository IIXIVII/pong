package Game.Ui;

import Common.GameStateDto;
import Common.GameStatus;

import javax.swing.*;

public interface Screen {
    ScreenName getScreenName();
    JPanel getPanel();
    void onShow();
    void onHide();
    void updateState(GameStateDto state, GameStatus currentStatus);
}
