package de.ferienakademie.smartquake.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceReader {

    private static SharedPreferences sharedPref;

    private PreferenceReader() {}

    public static void init(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean includeGravity() {
        return sharedPref.getBoolean("pref_gravity", false);
    }

    public static boolean useModalAnalysis() {
        return sharedPref.getBoolean("pref_modal_analysis", false);
    }

    public static double getLoadVectorScaling() {
        return (double) (5.0f * sharedPref.getFloat("loadVector_slider", 0.2f));
    }

    public static double getExcitationFrequency() {
        return (double) 10.0f * sharedPref.getFloat("frequency_slider", 0.1f);
    }

    public static double getDampingCoefficient() {
        return (double) .5f * sharedPref.getFloat("damping_slider", 0.1f);
    }

    public static boolean massMatrices() {
        return sharedPref.getBoolean("pref_use_lumped", false);
    }

    public static boolean showRawSensorData() {
        return sharedPref.getBoolean("pref_show_raw_sensor_data", false);
    }
}
