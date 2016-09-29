package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
public interface EarthQuakeListEntry extends Serializable {
    AccelerationProvider getAccelerationProvider(Context context) throws IOException;

    boolean hasProgress();

    String toString();
}
