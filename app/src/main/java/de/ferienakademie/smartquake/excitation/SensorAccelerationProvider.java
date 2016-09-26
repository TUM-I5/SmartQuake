package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class SensorAccelerationProvider extends StoredAccelerationProvider implements SensorEventListener {
    private long baseTime; //in nanoseconds
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private GravityProvider gravityProvider;

    public SensorAccelerationProvider(SensorManager sensorManager)
    {
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
        baseTime = SystemClock.elapsedRealtimeNanos();
        readings = new ArrayList<>();
        readings.add(new AccelData());
        gravityProvider.init();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData currentAcceleration = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.timestamp-baseTime);
        // put new element to the queue of sensor measurements
        readings.add(currentAcceleration);
    }

    @Override
    public AccelData getAccelerationMeasurement(){
        AccelData data = super.getAccelerationMeasurement();
        gravityProvider.getGravity(data);
        return data;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //ignored
    }

    public void setActive()
    {
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        gravityProvider.setActive();
    }

    public void setInactive()
    {
        sensorManager.unregisterListener(this);
        gravityProvider.setInactive();
    }
}
