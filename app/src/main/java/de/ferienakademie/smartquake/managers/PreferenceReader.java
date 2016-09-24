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
}
