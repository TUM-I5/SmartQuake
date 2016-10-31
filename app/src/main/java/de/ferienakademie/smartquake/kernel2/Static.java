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

import android.util.Log;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;


/**
 * Created by Claudius, Lukas and John on 23/09/16.
 */
public class Static extends ImplicitSolver {

    /**
     *
     * @param k1
     * @param xDot
     * @param delta_t is necessary to precalculate left and right hand side
     */
    public Static(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot, double delta_t) {
        super(k1, accelerationProvider, xDot);
        initialize(delta_t);
    }

    //Right and left hand side matrix
    DenseMatrix64F A; //left

    //old load vector
    DenseMatrix64F fLoad_old;

    double delta_t;


    @Override
    public void nextStep(double t, double delta_t) {

        //fLoad.zero();
        //fLoad.set(4, 0, 0.001);

        Log.i("Solver K: ", K.toString());
        Log.i("Solver F: ", fLoad.toString());

        //Solve
        solver.solve(fLoad,x); //solver.A*acc = RHS
        Log.i("Solver: ", x.toString());

    }


    private void initialize(double delta_t) {
        //set gamma to 1/2, beta to 1/4
        //initialise left side matrix
        A = K.copy();

        this.delta_t = delta_t;


        //LU solver
        solver = LinearSolverFactory.lu(k1.getNumberOfDOF());
        solver.setA(A);


        //initialize fLoad_old
        fLoad = new DenseMatrix64F(k1.getNumberOfDOF(), 1);
        fLoad.zero();
    }

    /**
     *
     *
     * @return ddotx_n+1
     */
    private void getAcceleration(){


    }
}

//solver.solve(input,output);