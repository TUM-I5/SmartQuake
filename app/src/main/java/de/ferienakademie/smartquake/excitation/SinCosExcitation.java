package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import de.ferienakademie.smartquake.managers.PreferenceReader;

/**
 * Class for generating a "standard" earthquake
 * used whenever sinusoidal excitation is used
 * For now only uses cos-function default amplitude 10 m/s^2 and default frequency of 1 Hz
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
    public SinCosExcitation(double frequency) {
        this(10, frequency);
    }

    /**
     * produces harmonic acceleration along X axis a=cos(2*pi*f*t)
     * @return 4d vector with accelerations along X,Y axis and zero gravitation vector
     */
    @Override
    public double[] getAcceleration() {
        return AccelData.toArray(getAccelerationMeasurement());
    }

    @Override
    public double[] getAcceleration(double time) {
        //counter++;
        AccelData temp = getAccelerationMeasurement(time);

        return AccelData.toArray(temp);
    }

    @Override
    /**
     * not used in current code
     * notifys the observer
     */
    public AccelData getAccelerationMeasurement() {
        counter++;
        AccelData accelData = new AccelData( amplitude * Math.cos(2 * Math.PI * frequency * counter * timestep  * 1e-9),
                0.0, (long) (counter * timestep));
        accelData.yGravity = 9.81;
        modifyData(accelData);
        notifyNewAccelData(accelData);
        return accelData;
    }

    @Override
    public AccelData getAccelerationMeasurement(double time) {
        AccelData accelData = new AccelData ( amplitude * Math.cos(2 * Math.PI * frequency * time),
                0.0, (long) (time * 1e9));
        accelData.yGravity = 9.81;
        modifyData(accelData);
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
    public void saveFileIfDataPresent(Context c, String fileName) throws IOException {
        //no.
    }

    @Override
    public void setActive() {

    }

    @Override
    public void setInactive() {

    }
}
