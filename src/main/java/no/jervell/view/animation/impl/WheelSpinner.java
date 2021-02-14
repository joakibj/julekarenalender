package no.jervell.view.animation.impl;


import no.jervell.view.swing.WheelView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.github.julekarenalender.JulekarenalenderKt.logger;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class WheelSpinner extends MouseAdapter {


    WheelAnimation animation;

    int pressY;
    double wheelPosition;

    private Target target;

    long lastT;
    private int[] y;
    private long[] t;
    private int pos;
    private int nPoints;

    private double maxVelocity = 100;
    private double minVelocity = 1;

    public WheelSpinner(WheelAnimation wheel, double maxVelocity) {
        this.maxVelocity = maxVelocity;
        this.animation = wheel;
        y = new int[3];
        t = new long[y.length];
        lastT = -1;
        pos = 0;
        nPoints = 0;
        pressY = 0;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    private synchronized void registerPosition(MouseEvent e) {
        registerPosition(e.getY(), e.getWhen());
    }

    private synchronized void registerPosition(int yCoordinate, long time) {
        if (time != lastT) {
            y[pos] = yCoordinate;
            t[pos] = time;
            lastT = time;
            pos = (pos + 1) % y.length;
            nPoints++;
        }
    }

    private void releaseWheel(String reason) {
        logger.debug("Releasing the wheel: " + reason);
    }

    private void spinWheel(double velocity) {
        animation.spin(velocity, target == null ? null : target.getTargetIndex(animation.getWheelView()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (animation.isBusy()) {
            return;
        }

        registerPosition(e);
        pressY = e.getY();
        wheelPosition = animation.getWheelView().getYOffset();
        animation.halt();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (animation.isBusy()) {
            return;
        }

        registerPosition(e);
        WheelView view = animation.getWheelView();
        double dragDistance = (double) ((e.getY() - pressY) * view.getVisibleRowCount()) / view.getHeight();
        view.setYOffset(wheelPosition - dragDistance);
        view.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (animation.isBusy()) {
            return;
        }

        registerPosition(e);

        int n = nPoints;
        nPoints = 0;
        if (n < 2) {
            releaseWheel("Click");
            return;
        }

        int L = y.length;
        int i = pos - 1 + L;   // +L to allow for subtraction below

        int y0 = y[i % L];
        int y1 = y[(i - 1) % L];
        int y2 = y[(i - 2) % L];
        long t0 = t[i % L];
        long t1 = t[(i - 1) % L];
        long t2 = t[(i - 2) % L];

        long delay = e.getWhen() - (e.getWhen() == t0 ? t1 : t0);
        if (delay > 80) {
            releaseWheel("Too long delay (" + delay + ")");
            return;
        }

        int dy1 = y0 - y1;
        long dt1 = Math.abs(t0 - t1);
        int dy2 = dy1;
        long dt2 = dt1;
        if (n > 2 && t0 != t2) {
            dy2 = y0 - y2;
            dt2 = Math.abs(t0 - t2);
        }

        if (dt1 == 0) {
            releaseWheel("Too short time interval");
            return;
        }

        double v1 = -dy1 * 10. / dt1;
        double v2 = -dy2 * 10. / dt2;
        double velocity = v2 + (v2 - v1) / 2;
        if (Math.abs(velocity) < minVelocity || v2 * velocity < 0) {
            releaseWheel("Faded out");
            return;
        } else if (velocity < -maxVelocity) {
            velocity = -maxVelocity;
        } else if (velocity > maxVelocity) {
            velocity = maxVelocity;
        }

        spinWheel(velocity);
    }

    public interface Target {
        public Integer getTargetIndex(WheelView view);
    }
}
