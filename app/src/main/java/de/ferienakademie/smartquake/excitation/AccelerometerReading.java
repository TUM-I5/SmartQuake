package de.ferienakademie.smartquake.excitation;

/**
 * Created by user on 22.09.2016.
 */
public class AccelerometerReading {

    public long timestamp;
    public double xAcceleration;
    public double yAcceleration;

    public AccelerometerReading(){
        timestamp = 0;
        xAcceleration = 0.0;
        yAcceleration = 0.0;
    }

    public AccelerometerReading(AccelerometerReading pivotObject){
        timestamp = pivotObject.timestamp;
        xAcceleration = pivotObject.xAcceleration;
        yAcceleration = pivotObject.yAcceleration;
    }
}
