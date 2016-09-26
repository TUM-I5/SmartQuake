package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class SensorAccelerationProvider extends StoredAccelerationProvider implements SensorEventListener {
    private long baseTime; //in nanoseconds
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private GravityProvider gravityProvider;
    private int sensorRate;
    private boolean gravityActive;

    public SensorAccelerationProvider(SensorManager sensorManager)
    {
        gravityActive = true;
        this.sensorManager = sensorManager;
        if(sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION).size() == 0){
           //gravity cannot be excluded from Sensor
            gravityProvider = new SoftwareGravityProvider();
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            // gravity can be excluded
            gravityProvider = new SensorGravityProvider(sensorManager);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }

    }

    @Override
    public void initTime(double timeStep) {
        super.initTime(timeStep);
        sensorRate = (int)(timeStep/2);
        baseTime = SystemClock.elapsedRealtimeNanos();
        gravityProvider.setBaseTime(baseTime);
        readings = new ArrayList<>();
        readings.add(new AccelData());
        gravityProvider.init(timeStep);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData currentAcceleration = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.timestamp-baseTime);
        // put new element to the queue of sensor measurements
        readings.add(currentAcceleration);

        notifyNewAccelData(currentAcceleration);
    }

    @Override
    public AccelData getAccelerationMeasurement(){
        AccelData data = super.getAccelerationMeasurement();
        if(gravityActive) {
            gravityProvider.getGravity(data);
        }
        return data;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //ignored
    }

    public void setActive()
    {
        sensorManager.registerListener(this, accelerometer, sensorRate);
        gravityProvider.setActive();
    }

    public void setInactive()
    {
        sensorManager.unregisterListener(this);
        gravityProvider.setInactive();
    }

    /**
     *
     * @param active activates gravity if true
     */
    public void setGravityActive(boolean active){
            gravityActive = true;
    }
}
