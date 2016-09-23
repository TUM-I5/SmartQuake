package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.interfaces.linsol.LinearSolver;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class ImplicitSolver extends Solver {

    /**
     *
     * @param k1
     * @param xDot
     */
    public ImplicitSolver(Kernel1 k1, DenseMatrix64F xDot) {
        super(k1, xDot);
    }
    //solver for LU decomposition
    LinearSolver<DenseMatrix64F> solver;

}
