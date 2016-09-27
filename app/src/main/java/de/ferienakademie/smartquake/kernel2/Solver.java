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
    double[] groundPosition;
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

        if(PreferenceReader.useModalAnalysis()){
            this.M = k1.getMassMatrix();
            this.K = k1.getStiffnessMatrix();
            this.C = k1.getDampingMatrix();
            this.x = new DenseMatrix64F(k1.getNumberofDOF(), 1);
        }
        else {
            this.M = k1.getMassMatrix();
            this.K = k1.getStiffnessMatrix();
            this.C = k1.getDampingMatrix();
            this.x = new DenseMatrix64F(k1.getNumberofDOF(), 1);
        }

        this.xDot = xDot;

        //fill xDotDot with zeros
        xDotDot = new DenseMatrix64F(k1.getNumberofDOF(), 1);
        xDotDot.zero();

        //create and fill fLoad vector with zeros
        fLoad = new DenseMatrix64F(k1.getNumberofDOF(),1);

        //create ground position, velocity and acceleration
        groundPosition = new double[2];
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

    public double[] getGroundPosition() { return groundPosition; }

    /**
     * Updates ground position
     * @param delta_t
     */
    public void setGroundPosition(double delta_t){

        //Initialize new acceleration and save old velocity
        double[] acc_new = accelerationProvider.getAcceleration();
        double[] velo_old = groundVelocity.clone();

        //Get new velocity
        groundVelocity[0] = 0.5*delta_t*(groundAcceleration[0]+acc_new[0]);
        groundVelocity[1] = 0.5*delta_t*(groundAcceleration[1]+acc_new[1]);

        //Get new position
        groundPosition[0] = 0.5*delta_t*(velo_old[0]+groundVelocity[0]);
        groundPosition[1] = 0.5*delta_t*(velo_old[1]+groundVelocity[1]);

    }

}
