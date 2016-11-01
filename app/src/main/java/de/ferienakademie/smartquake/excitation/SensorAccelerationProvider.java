// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import java.util.ArrayList;


/**
 * Created by David Schneller on 25.09.2016.
 * Acceleration provider that is used whenever shaking from phone is applied
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
        readings = new ArrayList<>();
        readings.add(new AccelData());
        gravityProvider.init(sensorRate, baseTime);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccelData currentAcceleration = new AccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.timestamp-baseTime);
        // put new element to the queue of sensor measurements
        readings.add(currentAcceleration);
    }

    /**
     * not used in current code
     * @return
     */
    @Override
    public double[] getAcceleration(){
        return AccelData.toArray(getAccelerationMeasurement());
    }

    @Override
    public double[] getAcceleration(double time){
        return AccelData.toArray(getAccelerationMeasurement(time));
    }

    @Override
    public AccelData getAccelerationMeasurement(){
        AccelData data = readings.get(readings.size()-1);
        gravityProvider.getGravity(data);
        modifyData(data);
        notifyNewAccelData(data);
        return data;
    }

    @Override
    public AccelData getAccelerationMeasurement(double time){
        AccelData data = super.getAccelerationMeasurement(time);
        gravityProvider.getGravity(data);
        modifyData(data);
        notifyNewAccelData(data);
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
}
