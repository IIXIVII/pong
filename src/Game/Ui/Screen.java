package Game.Ui;

import Common.GameStateDto;

import javax.swing.*;

/**
 * Interface définissant les fonctionnalités de base d'un écran de jeu.
 */
public interface Screen {
    /**
     * @return Le nom unique de cet écran.
     */
    ScreenName getScreenName();
    /**
     * @return Le JPanel associé à cet écran, qui sera affiché.
     */
    JPanel getPanel();
    /**
     * Méthode appelée lorsque l'écran devient visible.
     * Permet d'effectuer des initialisations ou des mises à jour spécifiques à l'affichage.
     */
    void onShow();
    /**
     * Méthode appelée lorsque l'écran est sur le point d'être masqué.
     */
    void onHide();
    /**
     * Met à jour l'état de l'écran avec les dernières informations du jeu.
     * @param state Le nouvel état du jeu.
     */
    void updateState(GameStateDto state);
}
