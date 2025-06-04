package Server.Game.Entitites;

/**
 * Classe de base abstraite pour les objets du jeu qui peuvent se déplacer.
 * Hérite de GameObject et ajoute des propriétés de vitesse (dx, dy).
 */
public class MovingGameObject extends GameObject {
    protected int dx, dy;

    /**
     * Constructeur pour MovingGameObject.
     * @param x Position X initiale.
     * @param y Position Y initiale.
     * @param width Largeur de l'objet.
     * @param height Hauteur de l'objet.
     * @param dx Vitesse horizontale initiale.
     * @param dy Vitesse verticale initiale.
     */
    public MovingGameObject(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public void setDx(int dx) { this.dx = dx; }
    public void setDy(int dy) { this.dy = dy; }

    /**
     * Déplace l'objet en fonction de ses vitesses dx et dy.
     */
    public void move() {
        x += dx;
        y += dy;
    }

    /**
     * Inverse la direction de déplacement horizontal.
     */
    public void reverseX() {
        dx *= -1;
    }

    /**
     * Inverse la direction de déplacement vertical.
     */
    public void reverseY() {
        dy *= -1;
    }

    @Override
    public void update() {
        move();
    }
}