package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by user on 21.09.2016.
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {

    private double Xacceleration;
    private double Yacceleration;

    private LinkedList<double[]> RecentMeasurements;

    /**
     * @param event:  change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Xacceleration = event.values[0];
        Yacceleration = event.values[1];
        RecentMeasurements.add(new double[] {Xacceleration,Yacceleration, event.timestamp});
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     *
     * @return latest measurements of accelerometer in X,Y axis
     */
    @Override
    public double[] getAcceleration() {
        return new double[] {Xacceleration, Yacceleration};
    }

    /**
     *
     * @param timestamp closest time moment w.r.t. start of the simulation when accelearation measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     */
    @Override
    public double[] getAcceleration(double timestamp) {
        double retrievedtimestamp;
        double Xacceleration;
        double Yacceleartion;

        do {
            [Xacceleration, Yacceleartion, retrievedtimestamp] = RecentMeasurements.poll();
        } while (retrievedtimestamp < timestamp);

        return new double[] {Xacceleration, Yacceleration};
    }

}
