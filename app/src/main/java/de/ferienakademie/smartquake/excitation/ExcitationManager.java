package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.LinkedList;

/**
 * Created by Yehor on 21.09.2016.
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {

    private double xAcceleration;
    private double yAcceleration;

    private LinkedList<double[]> recentMeasurements = new LinkedList<>();

    /**
     * @param event:  change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        xAcceleration = event.values[0];
        yAcceleration = event.values[1];
        // put new element to the queue of sensor measurements
        recentMeasurements.add(new double[] {xAcceleration, yAcceleration, event.timestamp});
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
        return new double[] {xAcceleration, yAcceleration};
    }

    /**
     *
     * @param timestamp closest time moment w.r.t. start of the simulation when acceleration measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     * calculated as linear interpolation of two closet recorded readings
     */
    @Override
    public double[] getAcceleration(double timestamp) {
        double[] nextReading = {0.0, 0.0, 0.0};
        double[] prevReading ;

        // poll entries of the queue until the first reading with timestep greater larger than wanted timestep found
        do {
            prevReading = nextReading;
            nextReading = recentMeasurements.poll();
        } while (nextReading[2] < timestamp);

        // calculated as y(x) = y1+(y2-y1)*(x-x1)/(x2-x1)
        return new double[] {prevReading[0] + (nextReading[0] - prevReading[0]) *
                (nextReading[2] - prevReading[2]) / (timestamp - prevReading[2]),
                prevReading[1] + ( nextReading[1] - prevReading[1]) *
                        (nextReading[2] - prevReading[2]) / (timestamp - prevReading[2])};
    }
}
