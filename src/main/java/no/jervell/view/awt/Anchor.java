package no.jervell.view.awt;

import java.awt.geom.Rectangle2D;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public enum Anchor {
    /**
     * Horizontally centered, top aligned
     */
    TOP_CENTER,
    /**
     * Horizontally centered, bottom aligned
     */
    BOTTOM_CENTER,
    /**
     * Vertically centered, left aligned
     */
    LEFT_CENTER,
    /**
     * Vertically centered, right aligned
     */
    RIGHT_CENTER,
    /**
     * Horizontally and vertically centered
     */
    CENTER,
    /**
     * Top/left aligned
     */
    TOP_LEFT,
    /**
     * Top/right aligned
     */
    TOP_RIGHT,
    /**
     * Bottom/left aligned
     */
    BOTTOM_LEFT,
    /**
     * Bottom/right aligned
     */
    BOTTOM_RIGHT;

    public Rectangle2D align(Rectangle2D location) {
        double w = location.getWidth();
        double h = location.getHeight();
        double x = location.getX();
        double y = location.getY();

        return align(x, y, w, h);
    }

    public Rectangle2D align(double x, double y, double w, double h) {
        double alignedX = alignHorizontally(x, w);
        double alignedY = alignVertically(y, h);
        return new Rectangle2D.Double(alignedX, alignedY, w, h);
    }

    double alignHorizontally(double x, double w) {
        switch (this) {
            case CENTER:
            case TOP_CENTER:
            case BOTTOM_CENTER:
                x -= w / 2;
                break;

            case RIGHT_CENTER:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                x -= w;
                break;
        }
        return x;
    }

    double alignVertically(double y, double h) {
        switch (this) {
            case CENTER:
            case LEFT_CENTER:
            case RIGHT_CENTER:
                y -= h / 2;
                break;

            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
            case BOTTOM_LEFT:
                y -= h;
                break;
        }
        return y;
    }
}
