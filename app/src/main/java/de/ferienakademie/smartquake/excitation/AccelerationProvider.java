package de.ferienakademie.smartquake.excitation;

/**
 * Created by user on 21.09.2016.
 */

public interface AccelerationProvider {
    /**
     * @return first element acceleration in X axis, second element acceleration in Y axis
     */
    double[] getAcceleration();

    /**
     * @param timestamp closest time moment w.r.t. start of the simulation when accelearation measured
     * @return first element acceleration in X axis, second element acceleration in Y axis
     */
    double[] getAcceleration(double timestamp);
}
