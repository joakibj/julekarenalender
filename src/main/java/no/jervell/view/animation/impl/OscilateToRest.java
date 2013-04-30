package no.jervell.view.animation.impl;

import java.util.ArrayList;
import java.util.List;

public class OscilateToRest extends AbstractTimeFunction {
    private static final double EPSILON = 0.00001;
    private double frameDuration = 20;  // Frame duration in milliseconds. TODO: Configurable

    public OscilateToRest(double initialVelocity) {
        init(.2, initialVelocity, 3, 80);
    }

    public OscilateToRest(double dist, double initialVelocity, int nWaves, int maxT) {
        init(dist, initialVelocity, nWaves, maxT);
    }

    private void init(double dist, double initialVelocity, int nWaves, int maxT) {
        List<Double> vList = computeVelocityValues(dist, initialVelocity, nWaves, maxT);
        double[] vel = copyToArray(vList);
        setValues(vel);
    }

    private List<Double> computeVelocityValues(double dist, double initialVelocity, int nWaves, int maxT) {
        List<Double> vList = new ArrayList<Double>(1000);
        moveToCenter(dist, initialVelocity, vList);
        for (int i = 0; i < maxT; ++i) {
            double scale = (maxT - 1. - i) / (maxT - 1);
            double velocity = Math.cos(i * nWaves * 2 * Math.PI / maxT) * initialVelocity * scale;
            vList.add(velocity * (dist < 0 ? -1 : 1));
        }
        return vList;
    }

    private void moveToCenter(double dist, double velocity, List<Double> vList) {
        double distSign = dist < 0 ? -1 : 1;
        dist = Math.abs(dist);
        while (dist > EPSILON) {
            double distanceNow = Math.min(frameDuration * velocity / 1000, dist);
            double velocityNow = (distanceNow * 1000) / frameDuration;
            vList.add(distSign * velocityNow);
            dist -= distanceNow;
        }
    }

}
