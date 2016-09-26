package de.ferienakademie.smartquake.excitation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.activity.SimulationActivity;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class FileAccelerationProvider extends StoredAccelerationProvider {
    SimulationActivity activity;

    public FileAccelerationProvider(SimulationActivity activity){
        super();
        this.activity = activity;
    }

    /**
     * Load acceleration data from a file
     * @param inputStream stream that passes readings from a file to excitation manager
     */
    public void load(InputStream inputStream) throws IOException {
        readings = new ArrayList<>();
        AccelData currentReading = new AccelData();
        String readingString;
        String[] readStringSplit;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        readingString = bufferedReader.readLine();
        while (readingString != null) {
            readStringSplit = readingString.split(";");

            currentReading.timestamp = Long.parseLong(readStringSplit[0]);
            currentReading.xAcceleration = Double.parseDouble(readStringSplit[1]);
            currentReading.yAcceleration = Double.parseDouble(readStringSplit[2]);
            currentReading.yGravity = Double.parseDouble(readStringSplit[3]);
            currentReading.yGravity = Double.parseDouble(readStringSplit[4]);

            readings.add(new AccelData(currentReading));
            readingString = bufferedReader.readLine();
        }
    }

    public void setActive()
    {

    }

    public void setInactive()
    {

    }
}
