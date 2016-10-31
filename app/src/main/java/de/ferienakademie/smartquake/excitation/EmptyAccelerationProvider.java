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

import android.content.Context;
import java.io.IOException;

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
    public void saveFileIfDataPresent(Context c, String fileName) throws IOException {

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
