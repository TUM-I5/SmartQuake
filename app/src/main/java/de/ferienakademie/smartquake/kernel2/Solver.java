package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

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

        this.M = k1.getMassMatrix();
        this.K = k1.getStiffnessMatrix();
        this.C = k1.getDampingMatrix();
        this.x = k1.getDisplacementVector();

        this.xDot = xDot;

        //fill xDotDot with zeros
        xDotDot = new DenseMatrix64F(k1.getNumberofDOF(), 1);
        xDotDot.zero();

        //create and fill fLoad vector with zeros
        fLoad = new DenseMatrix64F(k1.getNumberofDOF(),1);
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

}
