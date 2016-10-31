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

package de.ferienakademie.smartquake.preferenceElements;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by Vincent Stimper on 29.09.16.
 */
public class EditTextPreferenceInt extends android.preference.EditTextPreference {

    public EditTextPreferenceInt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        CharSequence text = getSummary();
        int val = Integer.parseInt((String) text);
        int maxVal = 1000;
        int minVal = 2;
        if (val > maxVal) {
            setText(Integer.toString(maxVal));
        } else if (val< minVal) {
            setText(Integer.toString(minVal));
        } else {
                setText(Integer.toString(val));
        }
        setSummary(text);
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
