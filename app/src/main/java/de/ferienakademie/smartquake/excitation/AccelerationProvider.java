package de.ferienakademie.smartquake.excitation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 21.09.2016.
 */

public abstract class AccelerationProvider {

    List<AccelerationProviderObserver> accelerationProviderObservers = new LinkedList<>();

    /**
     * returns acceleration reading for the next frame timestep
     * return = Fex(tp+dt), where tp - simulation time of last CALL of the function, dt - update rate of frames in nanosecs
     * has to inform the listener
     * @return first element acceleration in X axis, second element acceleration in Y axis
     *          third element gravity (acceleration) in X axis,
     *          fourth element gravity (acceleration) in Y axis
     */
    public abstract double[] getAcceleration();

    /**
     * @param time in seconds in reference frame of simulation for which value are returned
     * @return first element acceleration in X axis, second element acceleration in Y axis
     *          third element gravity (acceleration) in X axis,
     *          fourth element gravity (acceleration) in Y axis
     */
    public abstract double[] getAcceleration(double time);

    /**
     * has to inform the listener
     * @return datastructure with timestamp, X axis acceleration, Y axis acceleration
     */
    public abstract AccelData getAccelerationMeasurement();

    /**
     * datastructure with timestamp, X axis acceleration, Y axis acceleration for arbitrary simulation time
     * @param time
     * @return
     */
    public abstract AccelData getAccelerationMeasurement(double time);

    /**
     *
     * @param timeStep timeStep of the simulation in nanoseconds
     */
    public abstract void initTime(double timeStep);

    public abstract void saveFile(OutputStream outputStream) throws IOException;

    public abstract void setActive();

    public abstract void setInactive();

    public void addObserver(AccelerationProviderObserver observer) {
        if (!accelerationProviderObservers.contains(observer)) {
            accelerationProviderObservers.add(observer);
        }
    }

    public void removeObserver(AccelerationProviderObserver observer) {
        accelerationProviderObservers.remove(observer);
    }

    protected void notifyNewAccelData(AccelData data) {
        for (AccelerationProviderObserver o : accelerationProviderObservers) {
            o.onNewAccelerationValue(data);
        }
    }

    protected void notifyNewReplayPercent(double percent) {
        for (AccelerationProviderObserver o: accelerationProviderObservers) {
            o.onNewReplayPercent(percent);
        }
    }
}
