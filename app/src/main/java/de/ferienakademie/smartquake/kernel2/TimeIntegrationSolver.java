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


/**
 * Created by Felix Wechsler on 21/09/16.
 */
public interface TimeIntegrationSolver {


    /**
     * This is a interface for the time integration solver.
     *
     * @param t
     *        global time since start in seconds
     *
     * @param delta_t
     *        time step in seconds
     *
     */
    void nextStep( double t, double delta_t);

    void nextStepLumped(double t, double delta_t);

    DenseMatrix64F getFLoad();

    void setFLoad(DenseMatrix64F vec);

    DenseMatrix64F getX();

    DenseMatrix64F getXDotDot();

    DenseMatrix64F getXDot();

    void setGroundDisplacement(double delta_t, double[] currExcitation);

    public double[] getGroundDisplacement();

    }
