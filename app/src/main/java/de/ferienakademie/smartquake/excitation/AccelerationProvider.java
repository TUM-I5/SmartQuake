package de.ferienakademie.smartquake.excitation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by user on 21.09.2016.
 */

public interface AccelerationProvider {

    /**
     * has to inform the listener
     * @return first element acceleration in X axis, second element acceleration in Y axis
     *          third element gravity (acceleration) in X axis,
     *          fourth element gravity (acceleration) in Y axis
     */
    double[] getAcceleration();

    /**
     * has to inform the listener
     * @return datastructure with timestamp, X axis acceleration, Y axis acceleration
     */
    AccelData getAccelerationMeasurement();

    /**
     *
     * @param timeStep timeStep of the simulation in nanoseconds
     */
    void initTime(double timeStep);

    void saveFile(OutputStream outputStream) throws IOException;

    void setActive();

    void setInactive();
}
