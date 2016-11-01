// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

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
        return sharedPref.getBoolean("pref_use_gravity", false);
    }

    public static boolean useModalAnalysis() {
        return sharedPref.getBoolean("pref_modal_analysis", false);
    }


    public static boolean useModalReduction() {
        return sharedPref.getBoolean("pref_modal_reduction", false);
    }

    public static int getNumberOfModes() {
        return Integer.parseInt(sharedPref.getString("pref_numberOfModes", "10"));
    }

    public static boolean excitationVerticalDirection() {
        return sharedPref.getBoolean("pref_y_excitation", true);
    }

    public static double getLoadVectorScaling() {
        return (double) (5.0f * sharedPref.getFloat("loadVector_slider", 0.2f));
    }

    /*
    public static double getExcitationFrequency() {
        return (double) 10.0f * sharedPref.getFloat("frequency_slider", 0.1f);
    }
    */

    public static double getExcitationFrequency() {
        return Double.parseDouble(sharedPref.getString("pref_excitation_frequency", "1.0"));
    }

    public static double getDampingCoefficient() {
        return (double) .5f * sharedPref.getFloat("damping_slider", 0.1f);
    }

    public static boolean massMatrices() {
        return sharedPref.getBoolean("pref_use_lumped", false);
    }

    public static boolean groundDisplacements() {
        return sharedPref.getBoolean("pref_show_ground_displacements", false);
    }

    public static boolean showRawSensorData() {
        return sharedPref.getBoolean("pref_show_raw_sensor_data", true);
    }

    public static boolean showColors()
    {
        return sharedPref.getBoolean("pref_show_colors", true);
    }
}
