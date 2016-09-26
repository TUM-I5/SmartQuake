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
    //Sorry, setting protected is dirty, I know... But I do like getters/setters less.
    protected ArrayList<double[]> readings = new ArrayList<>();
    private ArrayList<Long> reading_ts = new ArrayList<>();
    protected int currentPosition;

    public SensorGravityProvider(SensorManager manager){
        this.manager = manager;
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
            reading_ts.add(sensorEvent.timestamp);
            readings.add(currentGravity);
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not used
    }

    public void init(){
        readings = new ArrayList<>();
        readings.add(new double[]{0,0});
    }

    public void setInactive() {
        manager.unregisterListener(this);
    }

    public void setActive(){
        manager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_UI);
    }


}
