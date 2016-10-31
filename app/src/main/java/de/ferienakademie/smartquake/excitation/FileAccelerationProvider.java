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

package de.ferienakademie.smartquake.excitation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class FileAccelerationProvider extends StoredAccelerationProvider {

    public boolean isEmpty() {
        return this.readings.isEmpty();
    }

    @Override
    public double[] getAcceleration() {
        AccelData temp = super.getAccelerationMeasurement();
            double percentage = (readings.get(currentPosition).timestamp*100.0)/
                    readings.get(readings.size()-1).timestamp;
        modifyData(temp);
        notifyNewAccelData(temp);
        notifyNewReplayPercent(percentage);
        return AccelData.toArray(temp);
    }

    @Override
    public double[] getAcceleration(double time) {
        AccelData temp = getAccelerationMeasurement(time);
        double percentage = (readings.get(currentPosition).timestamp*100.0)/
                readings.get(readings.size()-1).timestamp;
        modifyData(temp);
        notifyNewAccelData(temp);
        notifyNewReplayPercent(percentage);
        return AccelData.toArray(temp);
    }

    /**
     * Load acceleration data from a file
     *
     * @param inputStream stream that passes readings from a file to excitation manager
     */
    public void load(InputStream inputStream) throws IOException {
        readings = new ArrayList<>();
        AccelData currentReading = new AccelData();
        String readingString;
        String[] readStringSplit;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        readingString = bufferedReader.readLine();
        while (readingString != null) {
//            Log.v("FILE_ACCEL", readingString);
            readStringSplit = readingString.split(";");

            currentReading.timestamp = Long.parseLong(readStringSplit[0]);
            currentReading.xAcceleration = Double.parseDouble(readStringSplit[1]);
            currentReading.yAcceleration = Double.parseDouble(readStringSplit[2]);
            currentReading.xGravity = Double.parseDouble(readStringSplit[3]);
            currentReading.yGravity = Double.parseDouble(readStringSplit[4]);

            readings.add(new AccelData(currentReading));
            readingString = bufferedReader.readLine();
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

    public void setActive() {

    }

    public void setInactive() {

    }

    public ArrayList<AccelData> readData(){
        return this.readings;
    }
}
