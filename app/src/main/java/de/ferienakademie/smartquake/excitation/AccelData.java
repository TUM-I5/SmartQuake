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
 * Created by user on 22.09.2016.
 * Data structure to store measurements provided by sensors
 */
public class AccelData implements Comparable {

    public long timestamp;
    public double xAcceleration; // acceleration decoupled from gravity applied to the phone in X direction to the right
    public double yAcceleration; // acceleration decoupled from gravity applied to the phone in Y direction to the top
    public double xGravity;      // gravitation applied to the phone in X direction to the lefr (?)
    public double yGravity;      // gravitation applied to the phone in Y direction to the bottom

    public AccelData(){
        timestamp = 0;
        xAcceleration = 0.0;
        yAcceleration = 0.0;
        xGravity = 0.0;
        yGravity = 0.0;
    }

    public AccelData(AccelData pivotObject){
        timestamp = pivotObject.timestamp;
        xAcceleration = pivotObject.xAcceleration;
        yAcceleration = pivotObject.yAcceleration;
        xGravity = pivotObject.xGravity;
        yGravity = pivotObject.yGravity;
    }

    public AccelData(double xAccel, double yAccel, long timestamp) {
        this.xAcceleration = xAccel;
        this.yAcceleration = yAccel;
        this.timestamp = timestamp;
        xGravity = 0;
        yGravity = 0;
    }

    public AccelData(double xAccel, double yAccel, double xGravity, double yGravity, long timestamp) {
        this.xAcceleration = xAccel;
        this.yAcceleration = yAccel;
        this.xGravity = xGravity;
        this.yGravity = yGravity;
        this.timestamp = 0;
    }

    @Override
    public int compareTo(Object o) {
        return Long.compare(timestamp, ((AccelData)o).timestamp);
    }

    public static double[] toArray(AccelData data){
        return new double[]{data.xAcceleration, data.yAcceleration, data.xGravity, data.yGravity};
    }
}
