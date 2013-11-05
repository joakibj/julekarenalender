package no.jervell.view.animation.impl;

import java.util.ArrayList;
import java.util.List;

public class VelocityDecay extends AbstractTimeFunction {
    public VelocityDecay(double maxVelocity) {
        init(maxVelocity, .996, .009, .05);
    }

    public VelocityDecay(double maxVelocity, double decay, double drag, double threshold) {
        init(maxVelocity, decay, drag, threshold);
    }

    private void init(double velocity, double decay, double drag, double threshold) {
        if (decay < 0 || decay >= 1) {
            throw new IllegalArgumentException("decay=" + decay);
        }
        threshold = Math.abs(threshold);

        List<Double> vList = computeVelocityValues(velocity, decay, drag, threshold);
        double[] vel = copyToArray(vList);
        setValues(vel);
    }

    private List<Double> computeVelocityValues(double velocity, double decay, double drag, double threshold) {
        List<Double> vList = new ArrayList<Double>(1000);
        while (Math.abs(velocity) > threshold) {
            vList.add(velocity);
            velocity = velocity * decay - drag * Math.signum(velocity);
        }
        return vList;
    }

}
