package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;


/**
 * Created by Felix Wechsler on 21/09/16.
 */
public interface TimeIntegrationSolver {
    /**
     * This is a interface for the time integration solver.
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
     *        time step in seconds
     *
     */
    void nextStep(DenseMatrix64F x, DenseMatrix64F xDot , DenseMatrix64F xDotDot, double t, double delta_t);

}
