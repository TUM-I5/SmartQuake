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
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.managers.PreferenceReader;


/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class Solver implements TimeIntegrationSolver {


    AccelerationProvider accelerationProvider;

    // Acceleration
    DenseMatrix64F xDotDot;

    // Velocity
    DenseMatrix64F xDot;

    // Displacement
    DenseMatrix64F x;

    //vector for fLoad
    DenseMatrix64F fLoad;

    //Stiffness Matrix
    DenseMatrix64F K;

    //Damping Matrix
    DenseMatrix64F C;

    //Mass Matrix
    DenseMatrix64F M;

    //connection to kernel1
    SpatialDiscretization k1;

    //Ground position, velocity and acceleration
    double[] groundDisplacement;
    double[] groundVelocity;
    double[] groundAcceleration;

    /**
     *
     * @param k1
     *          Connection to kernel 1
     * @param xDot
     *          Stores the velocity
     */
    public Solver(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot) {
        this.k1 = k1;
        this.accelerationProvider = accelerationProvider;

        //x and xDot set
        this.x = new DenseMatrix64F(k1.getNumberOfDOF(), 1);
        this.xDot = xDot;

        //fill xDotDot with zeros
        xDotDot = new DenseMatrix64F(k1.getNumberOfDOF(), 1);
        xDotDot.zero();

        //it depends on Modal analysis which matrices we have to use
        if(PreferenceReader.useModalAnalysis()){
            this.M = k1.getMassMatrixModalAnalysis();
            this.K = k1.getStiffnessMatrixModalAnalysis();
            this.C = k1.getDampingMatrixModalAnalysis();
            //TODO: this looks really strange. In modal analysis xDot will be filled with zeros, in normal case
            //TODO: the given vector is used. WTF?
            x = new DenseMatrix64F(M.getNumRows(),1);
            this.xDot = new DenseMatrix64F(M.getNumRows(),1);
            xDotDot = new DenseMatrix64F(M.getNumRows(),1);
        } else {
            this.M = k1.getMassMatrix();
            this.K = k1.getStiffnessMatrix();
            this.C = k1.getDampingMatrix();
        }

        //create and fill fLoad vector with zeros
        //TODO: in modal analysis case fLoad should have other dimensions, right?
        fLoad = new DenseMatrix64F(k1.getNumberOfDOF(),1);

        //create ground position, velocity and acceleration
        groundDisplacement = new double[2];
        groundVelocity = new double[2];
        groundAcceleration = new double[2];

    }

    /**
     * This method calculates the position at the new time
     * @param t
     *        global time since start in seconds
     *
     * @param deltaT
     *        time step size
     */
    public void nextStep(double t, double deltaT) {
        //will be overwritten in the subclasses
    }


    public void nextStepLumped(double t, double delta_t){
        //will be overwritten in subclasses
    }
    /**
     *
     * @return
     */
    public AccelerationProvider getAccelerationProvider() {
        return accelerationProvider;
    }

    /**
     *
     * @return
     */
    public DenseMatrix64F getFLoad(){
        return fLoad;
    }


    /**
     *
     * @param vec
     */
    @Override
    public void setFLoad(DenseMatrix64F vec) {
        fLoad = vec.copy();
    }


    public DenseMatrix64F getX(){
        return x;
    }

    public DenseMatrix64F getXDotDot(){
        return xDotDot;
    }

    public DenseMatrix64F getXDot(){
        return xDot;
    }

    public double[] getGroundDisplacement() { return groundDisplacement; }


    /**
     * Updates ground position
     * @param delta_t
     */
    public void setGroundDisplacement(double delta_t, double[] acc_new){

        // save old values at t^n
        double[] vel_old  = groundVelocity.clone();
        double[] acc_old  = groundAcceleration.clone();


        // Set new accelerations at t^{n+1} and scale them with LoadVectorScaling
        double loadVectorScaling = PreferenceReader.getLoadVectorScaling();
        groundAcceleration[0] = loadVectorScaling * acc_new[0];
        groundAcceleration[1] = loadVectorScaling * acc_new[1];


        // Calculate new velocity at t^{n+1}
        groundVelocity[0] += 0.5*delta_t*(acc_old[0]+groundAcceleration[0]);
        groundVelocity[1] += 0.5*delta_t*(acc_old[1]+groundAcceleration[1]);


        //Get new position
        groundDisplacement[0] += 0.5*delta_t*(vel_old[0]+groundVelocity[0]);
        groundDisplacement[1] += 0.5*delta_t*(vel_old[1]+groundVelocity[1]);

    }

}
