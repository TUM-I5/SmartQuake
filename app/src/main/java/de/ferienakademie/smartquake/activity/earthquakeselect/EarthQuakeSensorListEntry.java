package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;
import android.hardware.SensorManager;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.SensorAccelerationProvider;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
public class EarthQuakeSensorListEntry implements EarthQuakeListEntry {

    private static final long serialVersionUID = 7439074565423440461L;
    
    @Override
    public AccelerationProvider getAccelerationProvider(Context context) {
        return new SensorAccelerationProvider((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
    }

    @Override
    public boolean hasProgress() {
        return false;
    }

    @Override
    public String toString() {
        return "Sensors";
    }
}
