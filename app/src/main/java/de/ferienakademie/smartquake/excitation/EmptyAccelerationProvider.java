package de.ferienakademie.smartquake.excitation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by David Schneller on 25.09.2016.
 */
public class EmptyAccelerationProvider extends AccelerationProvider {
    @Override
    public double[] getAcceleration() {
        return new double[] { Double.NaN, Double.NaN };
    }

    @Override
    public double[] getAcceleration(double time) {return new double[]{ Double.NaN, Double.NaN };}

    @Override
    public AccelData getAccelerationMeasurement() {
        return null;
    }

    @Override
    public AccelData getAccelerationMeasurement(double time) {
        return null;
    }

    @Override
    public void initTime(double timeStep) {

    }

    @Override
    public void saveFile(OutputStream outputStream) throws IOException {
        outputStream.close();
    }

    @Override
    public void setActive() {

    }

    @Override
    public void setInactive() {

    }

    @Override
    public void addObserver(AccelerationProviderObserver observer) {

    }

    @Override
    public void removeObserver(AccelerationProviderObserver observer) {

    }
}
