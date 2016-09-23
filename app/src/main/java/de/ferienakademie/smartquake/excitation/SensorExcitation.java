package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by Yehor on 21.09.2016.
 */
public class SensorExcitation extends AccelerationProvider implements SensorEventListener  {

    private AccelData currAcceleration;
    private ArrayList<AccelData> recentMeasurements;

    private int queue_pos = 0;


    public SensorExcitation(){
        currAcceleration = new AccelData();
        recentMeasurements = new ArrayList<>();
    }
    /**
     * @param event: change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        currAcceleration = new AccelData(event.values[0], event.values[1], event.timestamp);
        // put new element to the queue of sensor measurements
        recentMeasurements.add(currAcceleration);
        super.lstnr.excited(currAcceleration);
    }

    public void reset() {
        currAcceleration = new AccelData();
        queue_pos = 0;
        recentMeasurements.clear();
        recentMeasurements.add(currAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not used
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
        return currAcceleration;
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
        while (recentMeasurements.size() > queue_pos
                && recentMeasurements.get(queue_pos).timestamp < timestamp) {
            ++queue_pos;
        }

        if(queue_pos == 0 || queue_pos == recentMeasurements.size()){
            return currAcceleration;
        } else {
            return recentMeasurements.get(queue_pos);
        }
    }

    /**
     * @param timestamp closest time moment w.r.t. start of the simulation when acceleration measured
     * @return measurement of accelerometer in X,Y axis at time point @timestamp
     * calculated as linear interpolation of two closet recorded readings
     */
    @Override
    public double[] getAcceleration(long timestamp) {
        // poll entries of the queue until the first reading with timestep greater larger than wanted timestep found
        AccelData temp = getAccelerationMeasurement(timestamp);
        return new double[]{temp.xAcceleration, temp.yAcceleration};
    }

    /**
     * If the given timestamp lies between to timesteps in the list this method uses linear
     * interpolation between the two values to calculate the return value
     * @param queue_pos
     * @param timestamp
     * @return
     */
    private double[] interpolate(int queue_pos, long timestamp) {
        AccelData curr = recentMeasurements.get(queue_pos);
        AccelData prev = recentMeasurements.get(queue_pos - 1);
        long factor = (timestamp - prev.timestamp)/(curr.timestamp - prev.timestamp);
        return new double[] {prev.xAcceleration +
        (curr.xAcceleration - prev.yAcceleration) * factor,
                prev.yAcceleration + (curr.yAcceleration - prev.yAcceleration) * factor};

}
}
