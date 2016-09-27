package de.ferienakademie.smartquake.excitation;

/**
 * Created by user on 22.09.2016.
 */
public class AccelData implements Comparable {

    public long timestamp;
    public double xAcceleration;
    public double yAcceleration;
    public double xGravity;
    public double yGravity;

    public AccelData(){
        timestamp = 0;
        xAcceleration = 0.0;
        yAcceleration = 0.0;
        xGravity = 0.0;
        yGravity = 0.0;
    }

    public AccelData(AccelData pivotObject){
        timestamp = pivotObject.timestamp;
        xAcceleration = pivotObject.xAcceleration;
        yAcceleration = pivotObject.yAcceleration;
        xGravity = pivotObject.xGravity;
        yGravity = pivotObject.yGravity;
    }

    public AccelData(double xAccel, double yAccel, long timestamp) {
        this.xAcceleration = xAccel;
        this.yAcceleration = yAccel;
        this.timestamp = timestamp;
        xGravity = 0;
        yGravity = 0;
    }

    public AccelData(double xAccel, double yAccel, double xGravity, double yGravity, long timestamp) {
        this.xAcceleration = xAccel;
        this.yAcceleration = yAccel;
        this.xGravity = xGravity;
        this.yGravity = yGravity;
        this.timestamp = 0;
    }

    @Override
    public int compareTo(Object o) {
        return Long.compare(timestamp, ((AccelData)o).timestamp);
    }

    public static double[] toArray(AccelData data){
        return new double[]{data.xAcceleration, data.yAcceleration, data.xGravity, data.yGravity};
    }
}
