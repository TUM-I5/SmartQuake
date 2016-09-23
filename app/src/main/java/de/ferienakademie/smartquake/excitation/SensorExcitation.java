package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by Yehor on 21.09.2016.
 */
public class SensorExcitation implements SensorEventListener, AccelerationProvider {

    private AccelData currAcceleration;
    private ArrayList<AccelData> recentMeasurements = new ArrayList<>();

    private int queue_pos = 0;

    /**
     * @param event: change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        currAcceleration.xAcceleration = event.values[0];
        currAcceleration.yAcceleration = event.values[1];
        // put new element to the queue of sensor measurements
        recentMeasurements.add(new AccelData(currAcceleration));
    }

    public void reset() {
        currAcceleration = new AccelData();
        queue_pos = 0;
        //TODO write queue
        recentMeasurements.clear();
        recentMeasurements.add(currAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * @return latest measurements of accelerometer in X,Y axis
     */
    @Override
    public double[] getAcceleration() {
        return new double[]{currAcceleration.xAcceleration, currAcceleration.yAcceleration};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        return null;
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
        return null;
    }

    /**
     * @param timestamp closest time moment w.r.t. start of the simulation when acceleration measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     * calculated as linear interpolation of two closet recorded readings
     */
    @Override
    public double[] getAcceleration(long timestamp) {
        // poll entries of the queue until the first reading with timestep greater larger than wanted timestep found
        while (recentMeasurements.size() > queue_pos
                && recentMeasurements.get(queue_pos).timestamp < timestamp) {
            ++queue_pos;
        }

        if(queue_pos == 0 || queue_pos == recentMeasurements.size()){
            return new double[]{currAcceleration.xAcceleration, currAcceleration.yAcceleration};
        } else {
            return new double[]{recentMeasurements.get(queue_pos).xAcceleration,
                    recentMeasurements.get(queue_pos).yAcceleration};
        }
        // calculated as y(x) = y1+(y2-y1)*(x-x1)/(x2-x1)
        /*
        return new double[] {prevReading.xAcceleration +

                (nextReading.xAcceleration - prevReading.yAcceleration) *
                (nextReading.timestamp - prevReading.timestamp) / (timestamp - prevReading.timestamp),
                prevReading.yAcceleration + (nextReading.yAcceleration - prevReading.yAcceleration) *
                        (nextReading.yAcceleration - prevReading.yAcceleration) / (timestamp - prevReading.timestamp)};
        */
    }

    private double[] interpolate(int queue_pos, long timestamp) {
        AccelData curr = recentMeasurements.get(queue_pos);
        AccelData prev = recentMeasurements.get(queue_pos - 1);
        long factor = (curr.timestamp - prev.timestamp) / (timestamp - prev.timestamp);
        return new double[] {prev.xAcceleration +
        (curr.xAcceleration - prev.yAcceleration) * factor,
                prev.yAcceleration + (curr.yAcceleration - prev.yAcceleration) * factor};

}
}
