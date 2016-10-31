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
import java.util.ArrayList;

/**
 * uses the gravity sensor to get gravity
 * Gravitation readings provider that is used when phone has linear accelerometer
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
        reading_ts = new ArrayList<>();
        readings = new ArrayList<>();
        baseTime = 0;
    }

    /**
     * fills in gravitation components of acceleration vector with values of reading closest to the timestep of the data parameter
     * @param  data 4d acceleeation vecotor mesurement
     */
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
        this.baseTime = baseTime;
    }

    public void setInactive() {
        manager.unregisterListener(this);
    }

    public void setActive(){
        manager.registerListener(this, gSensor, sampleRate);
    }

}
