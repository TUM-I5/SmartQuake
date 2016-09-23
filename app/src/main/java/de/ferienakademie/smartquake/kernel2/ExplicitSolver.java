package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class ExplicitSolver extends Solver {
    double[] acceleration;

    /**
     *
     * @param k1
     * @param xDot
     */
    public ExplicitSolver(Kernel1 k1, DenseMatrix64F xDot) {
        super(k1, xDot);
    }


    /**
     * This method provides for all explicit solver the acceleration of all nodes
     */
    public void getAcceleration() {
        acceleration = k1.getAccelerationProvider().getAcceleration();
        for (int j = 6; j < k1.getNumDOF(); j += 3) {
            xDotDot.set(j, 0, 2000 * acceleration[0] - 5 * xDot.get(j, 0) - 100 * k1.getDisplacementVector().get(j, 0));
            xDotDot.set(j + 1, 0, 2000 * acceleration[1] - 5 * xDot.get(j + 1, 0) - 100 * k1.getDisplacementVector().get(j + 1, 0));
        }
    }
}
