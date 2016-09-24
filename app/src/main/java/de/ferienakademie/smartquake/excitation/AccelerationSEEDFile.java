package de.ferienakademie.smartquake.excitation;

/**
 * Created by David Schneller on 21.09.2016.
 */
public class AccelerationSEEDFile {/*
    private DataInput stream;
    private double[] cachedData;
    private int cachedDataPosition;

    public AccelerationSEEDFile(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

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
    */
}
