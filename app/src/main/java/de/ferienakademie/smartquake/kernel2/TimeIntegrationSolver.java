package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;


/**
 * Created by Felix Wechsler on 21/09/16.
 */
public interface TimeIntegrationSolver {


    /**
     * This is a interface for the time integration solver.
     *
     * @param t
     *        global time since start in seconds
     *
     * @param delta_t
     *        time step in seconds
     *
     */
    void nextStep( double t, double delta_t);

    void nextStepLumped(double t, double delta_t);

    DenseMatrix64F getFLoad();

    void setFLoad(DenseMatrix64F vec);

    DenseMatrix64F getX();

    DenseMatrix64F getXDotDot();

    DenseMatrix64F getXDot();

    void setGroundDisplacement(double delta_t, double[] currExcitation);

    public double[] getGroundDisplacement();

    }
