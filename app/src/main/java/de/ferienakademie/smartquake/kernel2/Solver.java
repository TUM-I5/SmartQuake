package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class Solver implements TimeIntegrationSolver {

    //vector of acceleration=xDotDot
    DenseMatrix64F xDotDot;
    //vector of velocity=xDot
    DenseMatrix64F xDot;
    //vector of displacement=x
    DenseMatrix64F x;

    //Stiffness Matrix
    DenseMatrix64F K;
    //Damping Matrix
    DenseMatrix64F C;
    //Mass Matrix
    DenseMatrix64F M;

    //connection to kernel1
    Kernel1 k1;


    /**
     *
     * @param k1
     *          Connection to kernel 1
     * @param xDot
     *          Stores the velocity
     */
    public Solver(Kernel1 k1, DenseMatrix64F xDot) {
        this.k1 = k1;

        this.M = k1.getMassMatrix();
        this.K = k1.getStiffnessMatrix();
        this.C = k1.getDampingMatrix();
        this.x = k1.getDisplacementVector();

        this.xDot = xDot;

        //fill xDotDot with zeros
        xDotDot = new DenseMatrix64F(k1.getNumDOF(), 1);
        xDotDot.zero();
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
}
