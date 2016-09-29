package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
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
