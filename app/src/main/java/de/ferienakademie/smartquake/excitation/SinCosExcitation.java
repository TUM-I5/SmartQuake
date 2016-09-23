package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */
public class SinCosExcitation extends AccelerationProvider {
    @Override
    public double[] getAcceleration() {
        return new double[0];
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        return null;
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
        return null;
    }

    @Override
    public double[] getAcceleration(long timestamp) {
        return new double[0];
    }
}
