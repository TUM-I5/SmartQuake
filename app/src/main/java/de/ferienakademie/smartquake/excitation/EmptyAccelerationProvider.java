package de.ferienakademie.smartquake.excitation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class EmptyAccelerationProvider implements AccelerationProvider {
    @Override
    public double[] getAcceleration() {
        return new double[] { Double.NaN, Double.NaN };
    }

    @Override
    public AccelData getAccelerationMeasurement() {
        return null;
    }

    @Override
    public void initTime(double timeStep) {

    }

    @Override
    public void saveFile(OutputStream outputStream) throws IOException {

    }

    @Override
    public void setActive() {

    }

    @Override
    public void setInactive() {

    }
}