package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for generating a "standard" earthquake
 * For now only uses sin-functipn default amplitude 5 and defaut frequency of one Hertz
 */
public class SinCosExcitation implements AccelerationProvider {
    double amplitude;
    double frequency;
    private double timestep;
    long counter;

    public SinCosExcitation(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.timestep = 2e-8;
        this.counter = 0;
    }

    public SinCosExcitation() {
        this.amplitude = 5;
        this.frequency = 8;
        this.timestep = 2e-8;
    }

    @Override
    public double[] getAcceleration() {
        counter++;
        return new double[]{amplitude * Math.sin(2* Math.PI *frequency * counter * timestep), 0.0};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        counter++;
        return new AccelData(Math.sin(2* Math.PI *frequency * counter * timestep), 0.0,
                (long) (counter * timestep * 1000) );
    }

    @Override
    public void initTime(double timeStep) {
        this.timestep = timeStep;
    }

    @Override
    public void saveFile(OutputStream outputStream) throws IOException {
        //no.
    }

    @Override
    public void setActive() {

    }

    @Override
    public void setInactive() {

    }
}
