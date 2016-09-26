package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 26.09.16.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.security.acl.AclEntry;
import java.util.ArrayList;

/**
 * uses the gravity sensor to get gravity
 */
public class SensorGravityProvider extends GravityProvider implements SensorEventListener {
    SensorManager manager;
    Sensor gSensor;
    private int sampleRate;
    private long baseTime;
    private ArrayList<double[]> readings;
    private ArrayList<Long> reading_ts;
    protected int currentPosition;

    public SensorGravityProvider(SensorManager manager){
        this.manager = manager;
        gSensor = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void getGravity(AccelData data) {
        while (readings.size() - 1 > currentPosition
                && reading_ts.get(currentPosition) < data.timestamp) {
            ++currentPosition;
        }

        data.xGravity = readings.get(currentPosition)[0];
        data.yGravity = readings.get(currentPosition)[1];
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
            double[] currentGravity =
                    new double[]{sensorEvent.values[0], sensorEvent.values[1]};
            // put new element to the queue of sensor measurements
            reading_ts.add(sensorEvent.timestamp - baseTime);
            readings.add(currentGravity);
        }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not used
    }

    @Override
    public void init(double timestep, long baseTime){
        readings = new ArrayList<>();
        readings.add(new double[]{0,0});
        reading_ts.add((long)0);
        sampleRate = (int)(timestep/2);
    }

    public void setInactive() {
        manager.unregisterListener(this);
        readings = new ArrayList<>();
        reading_ts = new ArrayList<>();
        readings.add(new double[]{0,0});
        reading_ts.add((long)0);
    }

    public void setActive(){
        manager.registerListener(this, gSensor, sampleRate);
        readings = new ArrayList<>();
        reading_ts = new ArrayList<>();
        readings.add(new double[]{0,0});
        reading_ts.add((long)0);
    }


}
