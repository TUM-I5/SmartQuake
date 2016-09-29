package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.SinCosExcitation;
import de.ferienakademie.smartquake.managers.PreferenceReader;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
public class EarthQuakeSinusoidalListEntry implements EarthQuakeListEntry {

    private static final long serialVersionUID = 3534478596083501699L;

    @Override
    public AccelerationProvider getAccelerationProvider(Context context) {
        return new SinCosExcitation(PreferenceReader.getExcitationFrequency());
    }

    @Override
    public boolean hasProgress() {
        return false;
    }

    @Override
    public String toString() {
        return "Sinusoidal";
    }
}
