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

package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;
import java.io.FileInputStream;
import java.io.IOException;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;


public class EarthQuakeFileListEntry implements EarthQuakeListEntry {

    private static final long serialVersionUID = -7823325803775057997L;
    private String filename;

    public EarthQuakeFileListEntry(String filename) {
        this.filename = filename;
    }

    @Override
    public AccelerationProvider getAccelerationProvider(Context context) throws IOException {
        FileInputStream input = context.openFileInput(filename);

        FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();
        fileAccelerationProvider.load(input);
        return fileAccelerationProvider;
    }

    @Override
    public boolean hasProgress() {
        return true;
    }

    @Override
    public String toString() {
        return filename.substring(0, filename.length() - 11);
    }
}
