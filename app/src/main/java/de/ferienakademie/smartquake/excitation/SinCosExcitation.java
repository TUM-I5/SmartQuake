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
    long counter;

    public SinCosExcitation(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.counter = 0;
    }

    public SinCosExcitation() {
        this.amplitude = 5;
        this.frequency = 8;
    }

    @Override
    public double[] getAcceleration() {
        counter++;
        return new double[]{amplitude * Math.sin(counter * Math.PI / 16), 0};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        counter++;
        return new AccelData(amplitude * Math.sin(counter * Math.PI / 16), 0, 0);
    }

    @Override
    public void initTime(long timeStamp, double timeStep) {

    }
}
