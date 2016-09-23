package de.ferienakademie.smartquake.excitation;

import android.content.Context;

import java.io.FileOutputStream;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.File;

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

    /**
     *
     * @param timestamp closest time myoment w.r.t. start of the simulation when accelearation measured
     * @return acceleration along X, Y axis form array of stored values at time point closest to timestemp
     */
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

    /**
     *
     * @param filename nam eof the file where the readings of excitation to be saved
     * @param activityContext link to the context of the program
     */
    public void saveFile(String filename, Context activityContext){
        String readingString;
        FileOutputStream outputStream;
        try {
            outputStream = activityContext.openFileOutput(filename, Context.MODE_PRIVATE);
            for (int i = 0; i < readings.size(); i++) {
                readingString = String.format("%d %f %f",readings.get(i).timestamp,
                        readings.get(i).xAcceleration,readings.get(i).yAcceleration);
                outputStream.write(readingString.getBytes());
            }
            outputStream.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
