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
public class SinCosExcitation extends AccelerationProvider {
    double amplitude;
    double frequency;
    private double timestep;
    long counter;

    public SinCosExcitation(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.timestep = 30_000_000;
        this.counter = 0;
    }

    public SinCosExcitation() {
        this.amplitude = 10;
        this.frequency = 1;
        this.timestep = 30_000_000;
    }

    @Override
    public double[] getAcceleration() {
        counter++;
        return new double[]{amplitude * Math.sin(2* Math.PI *frequency * counter * timestep * 1e-9), 0.0};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        counter++;
        AccelData accelData = new AccelData(Math.sin(2 * Math.PI * frequency * counter * timestep * 1e-9), 0.0,
                (long) (counter * timestep));
        notifyNewAccelData(accelData);
        return accelData;
    }

    public void setFrequency(double frequency){
        this.frequency = frequency;
    }

    public void setAmplitude(double amplitude){
        this.amplitude = amplitude;
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
