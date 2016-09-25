package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class SensorAccelerationProvider extends StoredAccelerationProvider implements SensorEventListener {
    private long baseTime; //in nanoseconds

    @Override
    public void initTime(double timeStep) {
        super.initTime(timeStep);
        baseTime = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData currentAcceleration = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.timestamp-baseTime);
        // put new element to the queue of sensor measurements
        readings.add(currentAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //ignored
    }
}
