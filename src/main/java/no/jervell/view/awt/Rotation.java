package no.jervell.view.awt;

import java.awt.geom.Point2D;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public enum Rotation {
    ZERO(0),
    CW_90(90),
    CW_180(180),
    CW_270(270);

    private double angleRad;

    Rotation(int angleDeg) {
        this.angleRad = Math.PI * angleDeg / 180;
    }

    public double getRadians() {
        return angleRad;
    }

    public Point2D getCenterOfRotation(double x, double y, double w, double h) {
        switch (this) {
            case CW_90:
                return new Point2D.Double(x + w, y);
            case CW_180:
                return new Point2D.Double(x + w, y + h);
            case CW_270:
                return new Point2D.Double(x, y + h);
            default:
                return new Point2D.Double(x, y);
        }
    }

    /**
     * Get origin (upper left corner)
     *
     * @param cx canvas x
     * @param cy canvas y
     * @param tw target width
     * @param th target height
     * @return origin relative to center of rotation
     */
    public Point2D getOrigin(double cx, double cy, double tw, double th) {
        switch (this) {
            case CW_90:
                return new Point2D.Double(cx + tw, cy);
            case CW_180:
                return new Point2D.Double(cx + tw, cy + th);
            case CW_270:
                return new Point2D.Double(cx, cy + th);
            default:
                return new Point2D.Double(cx, cy);
        }
    }
}
