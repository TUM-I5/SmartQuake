package de.ferienakademie.smartquake;

import android.app.Application;

import de.ferienakademie.smartquake.eigenvalueProblems.GenEig;
import de.ferienakademie.smartquake.managers.PreferenceReader;

public class SmartQuake extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceReader.init(this);
    }
}
