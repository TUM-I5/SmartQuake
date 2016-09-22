package de.ferienakademie.smartquake.excitation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by simon on 22.09.16.
 */
public class Recorder implements ExcitationListener, AccelerationProvider{
    ArrayList<AccelerometerReading> readings;
    int currPos;

    public Recorder(){
        initRecord();
        readings = new ArrayList<>();
        readings.add(new AccelerometerReading());
        currPos = 0;
    }

    public void initReplay(){
        currPos = 0;
    }

    public void initRecord(){
        readings = new ArrayList<>();
        readings.add(new AccelerometerReading());
    }

    @Override
    public void excited(AccelerometerReading reading) {
        readings.add(reading);
    }

    @Override
    @Deprecated
    public double[] getAcceleration() {
        return new double[0];
    }

    @Override
    public double[] getAcceleration(double timestamp) {
        while (readings.size() > currPos
                && readings.get(currPos).timestamp < timestamp) {
            ++currPos;
        }

        if(currPos == readings.size()){
            return null;
        } else {
            return readings.get(currPos);
        }
    }
}
