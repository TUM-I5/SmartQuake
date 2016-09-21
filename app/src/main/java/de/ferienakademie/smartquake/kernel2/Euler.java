package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;

/**
 * Created by lordfelice on 21/09/16.
 */
public class Euler implements TimeIntegrationSolver {

    @Override
    public void nextStep(DenseMatrix64F x, DenseMatrix64F xDot, DenseMatrix64F xDotDot, double t, double delta_t) {

    }
}
