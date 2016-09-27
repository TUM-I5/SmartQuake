package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for generating a "standard" earthquake
 * For now only uses sin-function default amplitude 5 and default frequency of one Hertz
 */
public class SinCosExcitation extends AccelerationProvider {
    double amplitude;
    double frequency;
    private double timestep;
    long counter;
    private List<AccelData> data;

    public SinCosExcitation(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.timestep = 10;
        this.counter = 0;
        data = new LinkedList<>();
    }

    public SinCosExcitation() {
        this.amplitude = 10;
        this.frequency = 1;
        this.timestep = 10;
        data = new LinkedList<>();
    }

    /**
     * produces harmonic acceleration along X axis a=sin(2*pi*f*t)
     * @return 4d vector with accelerations along X,Y axis and gravitation vector (-9.81,0)
     */
    @Override
    public double[] getAcceleration() {
        AccelData temp = getAccelerationMeasurement();
        return new double[]{temp.xAcceleration,
                temp.yAcceleration, temp.xGravity, temp.yGravity};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        counter++;
        AccelData accelData = new AccelData(Math.sin(2 * Math.PI * frequency * counter * timestep * 1e-9), 0.0,
                (long) (counter * timestep));
        accelData.yGravity = 9.81;
        data.add(accelData);
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
        data = new LinkedList<>();
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
