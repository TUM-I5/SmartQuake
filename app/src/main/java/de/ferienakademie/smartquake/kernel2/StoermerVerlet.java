package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


import org.ejml.data.DenseMatrix64F;

/**
 * Created by Yehor on 22.09.2016.
 */
public class StoermerVerlet implements TimeIntegrationSolver {
    @Override
    public void nextStep(DenseMatrix64F x, DenseMatrix64F xDot , DenseMatrix64F xDotDot, double t, double delta_t) {

        //velocity at n+1/2
        DenseMatrix64F midpointXdot = xDot.copy();
        CommonOps.addEquals(midpointXdot, 0.5 * delta_t, xDotDot);

        //displacement at step n+1
        CommonOps.addEquals(x, delta_t, midpointXdot);

        //accelerations at n+1
        //TODO include frequency constant or implement arbitrary force calculation
        CommonOps.changeSign(x,xDotDot);

        //velocity at n+1
        CommonOps.addEquals(midpointXdot, 0.5 * delta_t, xDotDot);

    }
}
