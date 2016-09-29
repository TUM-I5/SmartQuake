package de.ferienakademie.smartquake.excitation;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by David Schneller on 25.09.2016.
 * Super class for all acceleration providers that could be saved in the file after simulation: NO sinusoidal acceleration
 */
public abstract class StoredAccelerationProvider extends AccelerationProvider {
    protected ArrayList<AccelData> readings = new ArrayList<>(); //list of stored Eartquake data
    protected int currentPosition;
    protected int tick; //current simulation tick
    protected double timeStep; //in nanoseconds

    /**
     * @param timeStep  timeStep of the simulation in nanoseconds
     */
    @Override
    public void initTime(double timeStep) {
        this.timeStep = timeStep;
        this.tick = 0;
        currentPosition = 0;
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        long currTime = (long) (tick * timeStep);
        while (readings.size() - 1 > currentPosition
                && readings.get(currentPosition).timestamp < currTime) {
            ++currentPosition;
        }
        ++tick;

        return readings.get(currentPosition);
    }

    /**
     *
     * @param time of simulation in seconds for which external simulationto be retrieved
     * @return first 4d accelerations vector reading after the time point of interest
     */
    public AccelData getAccelerationMeasurement(double time){
        while (readings.size() - 1 > currentPosition
                && readings.get(currentPosition).timestamp < (long)(time * 1e9)) {
            ++currentPosition;
        }

        return readings.get(currentPosition);
    }

    /**
     * Store the data to a file
     */
    public void saveFileIfDataPresent(Context c, String fileName) throws IOException {
        String readingString;
        OutputStreamWriter outputStreamReader = new OutputStreamWriter(c.openFileOutput("Last.earthquake", Context.MODE_PRIVATE));
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamReader);

        for (int i = 0; i < readings.size(); i++) {
            readingString = String.format(Locale.ENGLISH, "%d;%20f;%20f;%20f;%20f\n", readings.get(i).timestamp,
                    readings.get(i).xAcceleration, readings.get(i).yAcceleration,
                    readings.get(i).xGravity, readings.get(i).yGravity);
            bufferedWriter.write(readingString);
        }
        bufferedWriter.close();
        outputStreamReader.close();
    }

    /**
     * If the given timestamp lies between to timesteps in the list this method uses linear
     * interpolation between the two values to calculate the return value
     *
     * @param queue_pos the position from which accelerometer readings will be read
     * @param timestamp time point for which interpolation of measurements are prefromed
     * @return weighted average of the two accelerometer measurements around time point of interest
     */
    protected double[] interpolate(int queue_pos, long timestamp) {
        AccelData curr = readings.get(queue_pos);
        AccelData prev = readings.get(queue_pos - 1);
        long factor = (timestamp - prev.timestamp) / (curr.timestamp - prev.timestamp);
        return new double[]{prev.xAcceleration +
                (curr.xAcceleration - prev.yAcceleration) * factor,
                prev.yAcceleration + (curr.yAcceleration - prev.yAcceleration) * factor};

    }
}
