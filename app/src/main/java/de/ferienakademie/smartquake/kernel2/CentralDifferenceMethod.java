package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.junit.runner.Describable;

/**
 * Created by Felix Wechsler on 22/09/16.
 */
public class CentralDifferenceMethod implements TimeIntegrationSolver {

    //Matrices for the calculuation of the forces
    DenseMatrix64F StiffnessMatrix;
    DenseMatrix64F MassMatrix;
    DenseMatrix64F DampingMatrix;

    /**
     *
     * @param StiffnessMatrix
     * @param MassMatrix
     * @param DampingMatrix
     */
    public CentralDifferenceMethod(DenseMatrix64F StiffnessMatrix, DenseMatrix64F  MassMatrix, DenseMatrix64F DampingMatrix){
        this.StiffnessMatrix = StiffnessMatrix;
        this.MassMatrix = MassMatrix;
        this.DampingMatrix = DampingMatrix;

    }

    /**
     *
     * @param x
     *        displacement
     *
     * @param xDot
     *        velocity
     *
     * @param xDotDot
     *        acceleration
     *
     * @param t
     *        global time since start in seconds
     *
     * @param delta_t
     */
    public void nextStep(DenseMatrix64F x, DenseMatrix64F xDot , DenseMatrix64F xDotDot, double t, double delta_t){



    }


}
