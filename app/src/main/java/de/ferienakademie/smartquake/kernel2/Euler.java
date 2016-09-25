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
