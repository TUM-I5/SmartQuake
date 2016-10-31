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


/**
 * uses only software emulation to get the current gravity-values
 * used when phone has NO linear accelerometer
 */
public class SoftwareGravityProvider extends GravityProvider{
    private double lastXGravity = 0;
    private double lastYGravity = 9.81;
    private final double alpha = 0.8;

    /**
     * fills in gravity properties with acceleration passed through low-frequency filter
     * @param data datastrucure with acceleretation along X,Y axis already provided
     */
    @Override
    public void getGravity(AccelData data) {
        lastXGravity = alpha * lastXGravity + (1 - alpha) * data.xAcceleration;
        lastYGravity = alpha * lastYGravity + (1 - alpha) * data.yAcceleration;

        data.xGravity = lastXGravity;
        data.yGravity = lastYGravity;
        data.xAcceleration = data.xAcceleration - lastXGravity;
        data.yAcceleration = data.yAcceleration - lastYGravity;
    }

    @Override
    public void init(double timestep, long baseTime) {
        lastYGravity = 9.81;
        lastXGravity = 0;
    }

    @Override
    public void setActive() {
        //noop
    }

    @Override
    public void setInactive() {
        //noop
    }
}
