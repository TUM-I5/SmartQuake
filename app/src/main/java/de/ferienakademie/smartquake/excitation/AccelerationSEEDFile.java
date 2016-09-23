package de.ferienakademie.smartquake.excitation;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import edu.sc.seis.seisFile.mseed.DataRecord;

/**
 * Created by David Schneller on 21.09.2016.
 */
public class AccelerationSEEDFile extends AccelerationProvider
{
    private DataInput stream;
    private double[] cachedData;
    private int cachedDataPosition;

    private void reloadData() throws IOException, SeedFormatException, CodecException {
        while (true) //will this hang forever in some cases?
        {
            SeedRecord record;
            record = SeedRecord.read(stream);

            if (record instanceof DataRecord)
            {
                DataRecord dataRecord = (DataRecord)record;
                DecompressedData decompressed = dataRecord.decompress();
                cachedData = decompressed.getAsDouble();
                cachedDataPosition = 0;
                return;
            }
        }
    }

    private double getNextValue()
    {
        if (cachedData == null || cachedDataPosition >= cachedData.length)
        {
            //If there's no more data, we get some.
            try
            {
                reloadData();
            }
            catch (EOFException e)
            {
                //File finished.
                return Double.NaN;
            }
            catch (IOException e)
            {
                return Double.NaN;
            }
            catch (SeedFormatException e)
            {
                return Double.NaN;
            }
            catch (CodecException e)
            {
                return Double.NaN;
            }
        }


        double out = cachedData[cachedDataPosition];

        ++cachedDataPosition;

        return out;
    }

    public AccelerationSEEDFile(InputStream stream)
    {
        this.stream = new DataInputStream(stream);
    }

    @Override
    public double[] getAcceleration()
    {
        //TODO: Scale!
        return new double[] { getNextValue(), 0 };
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        //TODO implementation
        return null;
    }

    @Override
    public AccelData getAccelerationMeasurement(long timestamp) {
        //TODO implementation
        return null;
    }

    @Override
    public double[] getAcceleration(long timestamp)
    {
        //TODO: Scale!
        return new double[] { getNextValue(), 0 };
    }
}
