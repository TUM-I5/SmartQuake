package de.ferienakademie.smartquake.excitation;

/**
 * Created by user on 22.09.2016.
 */
public class AccelData {

    public long timestamp;
    public double xAcceleration;
    public double yAcceleration;

    public AccelData(){
        timestamp = 0;
        xAcceleration = 0.0;
        yAcceleration = 0.0;
    }

    public AccelData(AccelData pivotObject){
        timestamp = pivotObject.timestamp;
        xAcceleration = pivotObject.xAcceleration;
        yAcceleration = pivotObject.yAcceleration;
    }

    public AccelData(double xAccel, double yAccel, long timestamp) {
        this.xAcceleration = xAccel;
        this.yAcceleration = yAccel;
        this.timestamp = timestamp;
    }
}
