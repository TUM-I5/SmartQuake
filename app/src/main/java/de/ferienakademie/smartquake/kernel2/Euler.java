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

package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;


/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class Euler extends ExplicitSolver {

    /**
     *
     * @param k1
     * @param xDot
     */
    DenseMatrix64F averageXDot;
    public Euler(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot) {
        super(k1, accelerationProvider, xDot);
        averageXDot =xDot.copy();
    }

    @Override
    public void nextStep(double t, double delta_t) {
        //pure euler at the moment
        //store old (n-1) velocity
        getAcceleration();
        DenseMatrix64F oldxDot = xDot.copy();

        //velocity at n
        CommonOps.addEquals(xDot, delta_t, xDotDot);

        //create average matrix of velocities at step n and n+1
        //CommonOps.addEquals(averageXDot, 1, oldxDot);

        //displacement at step n+1
        CommonOps.addEquals(x, 1*delta_t, xDot);

    }



}
