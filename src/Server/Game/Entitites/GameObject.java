package Server.Game.Entitites;

import java.awt.*;

/**
 * Classe de base abstraite pour les objets du jeu (paddles, balle, obstacles).
 */
public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected Rectangle bounds;

    /**
     * Constructeur pour GameObject.
     * @param x Position X initiale.
     * @param y Position Y initiale.
     * @param width Largeur de l'objet.
     * @param height Hauteur de l'objet.
     */
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Rectangle getBounds() {
        bounds.x = this.x;
        bounds.y = this.y;
        bounds.width = this.width;
        bounds.height = this.height;
        return bounds;
    }

    /**
     * Méthode abstraite pour mettre à jour l'état de l'objet à chaque tick du jeu. (Ex : mouvement)
     * Doit être implémentée par les sous-classes.
     */
    public abstract void update();
}
