package de.ferienakademie.smartquake.excitation;

/**
 * Created by user on 21.09.2016.
 */

public abstract class AccelerationProvider {
    ExcitationListener lstnr = EmptyExcitationListener.getInstance();


    public void registerLstnr(ExcitationListener newLstnr){
        lstnr = newLstnr;
    }

    public void deregisterLstnr(){
        lstnr = EmptyExcitationListener.getInstance();
    }

    /**
     * has to inform the listener
     * @return first element acceleration in X axis, second element acceleration in Y axis
     */
    public abstract double[] getAcceleration();

    /**
     * has to inform the listener
     * @return datastructure with timestamp, X axis acceleration, Y axis acceleration
     */
    public abstract AccelData getAccelerationMeasurement();

    /**
     * has to inform the listener
     * @param timestamp closest time myoment w.r.t. start of the simulation when accelearation measured
     * @return first element acceleration in X axis, second element acceleration in Y axis
     */
    public abstract AccelData getAccelerationMeasurement(long timestamp);

    /**
     * has to inform the listener
     * @param timestamp closest time moment w.r.t. start of the simulation when accelearation measured
     * @return first element acceleration in X axis, second element acceleration in Y axis
     */
    public abstract double[] getAcceleration(long timestamp);
}
