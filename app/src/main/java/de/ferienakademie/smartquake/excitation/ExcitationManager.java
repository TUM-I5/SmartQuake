package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.LinkedList;

/**
 * Created by Yehor on 21.09.2016.
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {

    private AccelerometerReading currAcceleration;

    private LinkedList<AccelerometerReading> recentMeasurements = new LinkedList<>();

    /**
     * @param event:  change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        currAcceleration.xAcceleration = event.values[0];
        currAcceleration.yAcceleration = event.values[1];
        // put new element to the queue of sensor measurements
        recentMeasurements.add(new AccelerometerReading(currAcceleration));
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
        return new double[] {currAcceleration.xAcceleration, currAcceleration.yAcceleration};
    }

    @Override
    public AccelerometerReading getAccelerationMeasurement() {
        return null;
    }

    /**
     *
     * @param timestamp closest time moment w.r.t. start of the simulation when acceleration measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     * calculated as linear interpolation of two closet recorded readings
     */
    @Override
    public double[] getAcceleration(double timestamp) {
        AccelerometerReading nextReading = new AccelerometerReading() ;
        AccelerometerReading prevReading = new AccelerometerReading();

        // poll entries of the queue until the first reading with timestep greater larger than wanted timestep found
        do {
            prevReading = nextReading;
            nextReading = recentMeasurements.poll();
        } while (nextReading.timestamp < timestamp);

        // calculated as y(x) = y1+(y2-y1)*(x-x1)/(x2-x1)
        return new double[] {prevReading.xAcceleration +
                (nextReading.xAcceleration - prevReading.yAcceleration) *
                (nextReading.timestamp - prevReading.timestamp) / (timestamp - prevReading.timestamp),
                prevReading.yAcceleration + (nextReading.yAcceleration - prevReading.yAcceleration) *
                        (nextReading.yAcceleration - prevReading.yAcceleration) / (timestamp - prevReading.timestamp)};
    }
}
