package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.ejml.data.DenseMatrix64F;

import java.util.LinkedList;

/**
 * Created by Yehor on 21.09.2016.
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {

    private double Xacceleration;
    private double Yacceleration;

    private LinkedList<double[]> RecentMeasurements = new LinkedList<>();

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
     * @param timestamp closest time moment w.r.t. start of the simulation when acceleration measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     * calculated as linear interpolation of two closet recorded readings
     */
    @Override
    public double[] getAcceleration(double timestamp) {
        double[] retrievedreading = {0.0, 0.0, 0.0};
        double[] oldretrievedreading ;

        // poll entries of the queue until the first reading with timestep greater larger than wanted timestep found
        do {
            oldretrievedreading = retrievedreading;
            retrievedreading = RecentMeasurements.poll();
        } while (retrievedreading[2] < timestamp);

        // calculated as y(x) = y1+(y2-y1)*(x-x1)/(x2-x1)
        return new double[] {oldretrievedreading[0]+(retrievedreading[0]-oldretrievedreading[0])*
                (retrievedreading[2]-oldretrievedreading[2])/(timestamp-oldretrievedreading[2]),
                oldretrievedreading[1]+(retrievedreading[1]-oldretrievedreading[1])*
                        (retrievedreading[2]-oldretrievedreading[2])/(timestamp-oldretrievedreading[2])};
    }
}
