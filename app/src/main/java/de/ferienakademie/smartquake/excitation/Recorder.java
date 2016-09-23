package de.ferienakademie.smartquake.excitation;

import java.util.ArrayList;

/**
 * Created by simon on 22.09.16.
 */

/**
 * Records and replays stored acceleration data by Listening to the SensorExcitation-Object
 */
public class Recorder implements ExcitationListener, AccelerationProvider {
    ArrayList<AccelData> readings;
    int currPos;

    public Recorder(){
        initRecord();
        readings = new ArrayList<>();
        readings.add(new AccelData());
        currPos = 0;
    }

    /**
     * has to be called before the replay is started
     */
    public void initReplay(){
        currPos = 0;
    }

    /**
     * has to be called before recording is started
     */
    public void initRecord(){
        readings = new ArrayList<>();
        readings.add(new AccelData());
    }

    @Override
    public void excited(AccelData reading) {
        readings.add(reading);
    }

    @Override
    @Deprecated
    public double[] getAcceleration() {
        return new double[0];
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        return null;
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
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

    @Override
    public double[] getAcceleration(long timestamp) {
        if(currPos == readings.size()){
            return null;
        } else {
            AccelData temp = getAccelerationMeasurement(timestamp);
            return new double[]{temp.xAcceleration, temp.yAcceleration};
        }
    }
}
