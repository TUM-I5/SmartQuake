package de.ferienakademie.smartquake.excitation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by David Schneller on 25.09.2016.
 */
public abstract class StoredAccelerationProvider extends AccelerationProvider {
    //Sorry, setting protected is dirty, I know... But I do like getters/setters less.
    protected ArrayList<AccelData> readings = new ArrayList<>();
    protected int currentPosition;
    protected int tick; //current simulation tick
    protected double timeStep; //in nanoseconds

    /**
     * @param timeStep  timeStep of the simulation in nanoseconds
     */
    @Override
    public void initTime(double timeStep) {
        this.timeStep = timeStep;
        currentPosition = 0;
    }

    @Override
    public double[] getAcceleration() {
        AccelData temp = getAccelerationMeasurement();
        return new double[]{temp.xAcceleration, temp.yAcceleration, temp.xGravity, temp.yGravity};
    }

    @Override
    public double[] getAcceleration(double time) {
        while (readings.size() - 1 > currentPosition
                && readings.get(currentPosition).timestamp < time) {
            ++currentPosition;
        }
        ++tick;

        AccelData data = readings.get(currentPosition);
        notifyNewAccelData(data);
        return new double[]{data.xAcceleration, data.yAcceleration};
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        long currTime = (long) (tick * timeStep);
        while (readings.size() - 1 > currentPosition
                && readings.get(currentPosition).timestamp < currTime) {
            ++currentPosition;
        }
        ++tick;

        AccelData data = readings.get(currentPosition);
        return data;
    }

    /**
     * Store the data to a file
     * @param outputStream reference to a stream passing readings to internal storage
     */
    public void saveFile(OutputStream outputStream) throws IOException {
        String readingString;
        OutputStreamWriter outputStreamReader;
        BufferedWriter bufferedWriter;
        outputStreamReader = new OutputStreamWriter(outputStream);
        bufferedWriter = new BufferedWriter(outputStreamReader);
        for (int i = 0; i < readings.size(); i++) {
            readingString = String.format(Locale.ENGLISH, "%d;%20f;%20f;%20f;%20f\n", readings.get(i).timestamp,
                    readings.get(i).xAcceleration, readings.get(i).yAcceleration,
                    readings.get(i).xGravity, readings.get(i).yGravity);
            bufferedWriter.write(readingString);
        }
        bufferedWriter.flush();
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
