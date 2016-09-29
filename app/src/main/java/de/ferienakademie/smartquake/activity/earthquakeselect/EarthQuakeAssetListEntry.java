package de.ferienakademie.smartquake.activity.earthquakeselect;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
public class EarthQuakeAssetListEntry implements EarthQuakeListEntry {

    private static final long serialVersionUID = -3833997704436060940L;
    public String filename;

    public EarthQuakeAssetListEntry(String filename) {
        this.filename = filename;
    }

    @Override
    public AccelerationProvider getAccelerationProvider(Context context) throws IOException {
        InputStream input = context.getAssets().open(filename);

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
