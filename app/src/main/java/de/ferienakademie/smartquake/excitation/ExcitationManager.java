package de.ferienakademie.smartquake.excitation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by user on 21.09.2016.
 */
public class ExcitationManager implements SensorEventListener, AccelerationProvider {

    double Xacceleration;
    double Yacceleration;

    /**
     * @param event:  change of accelerometer measurements
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Xacceleration = event.values[0];
        Yacceleration = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     *
     * @return latest measurements of accelerometer in X,Y axis
     */
    @Override
    public double[] getAccelaration() {
        return new double[] {Xacceleration, Yacceleration};
    }
}
