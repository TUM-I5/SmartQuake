package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


import org.ejml.data.DenseMatrix64F;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class Euler implements TimeIntegrationSolver {

    @Override
    public void nextStep(DenseMatrix64F x, DenseMatrix64F xDot , DenseMatrix64F xDotDot, double t, double delta_t) {
        //store old (n-1) velocity
        DenseMatrix64F oldxDot = xDot.copy();

        //velocity at n
        CommonOps.addEquals(xDot, delta_t, xDotDot);

        //create average matrix of velocities at step n and n+1
        DenseMatrix64F averageXDot=xDot.copy();
        CommonOps.addEquals(averageXDot,1, oldxDot);

        //displacement at step n+1
        CommonOps.addEquals(x, 0.5 * delta_t, averageXDot);

    }

}
