package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */

/**
 * Class for generating a "standard" earthquake
 * For now only uses sin-functipn default amplitude 5 and defaut frequency of one Hertz
 */
public class SinCosExcitation implements AccelerationProvider {
    double amplitude;
    double frequency;

    public SinCosExcitation(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    public SinCosExcitation() {
        this.amplitude = 5;
        this.frequency = 16e-10 * 2 * Math.PI;
    }

    @Override
    public double[] getAcceleration() {
        return new double[]{amplitude * Math.sin(Math.random() * frequency), 0};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        return new AccelData(Math.sin(Math.random() * frequency), 0, 0);
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
        return new AccelData(amplitude * Math.sin(timestamp * frequency), 0, timestamp);
    }

    @Override
    public double[] getAcceleration(long timestamp) {
        AccelData temp = getAccelerationMeasurement(timestamp);
        return new double[]{temp.xAcceleration, temp.yAcceleration};
    }
}
