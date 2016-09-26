package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

/**
 * Created by Claudius, Lukas and John on 23/09/16.
 */
public class Static extends ImplicitSolver {

    /**
     *
     * @param k1
     * @param xDot
     * @param delta_t is necessary to precalculate left and right hand side
     */
    public Static(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot, double delta_t) {
        super(k1, accelerationProvider, xDot);
        initialize(delta_t);
    }

    //Right and left hand side matrix
    DenseMatrix64F A; //left

    //old load vector
    DenseMatrix64F fLoad_old;

    double delta_t;


    @Override
    public void nextStep(double t, double delta_t) {

        fLoad.zero();
        fLoad.set(4, 0, 0.001);

        Log.i("Solver K: ", K.toString());
        Log.i("Solver F: ", fLoad.toString());

        //Solve
        solver.solve(fLoad,x); //solver.A*acc = RHS
        Log.i("Solver: ", x.toString());

    }


    private void initialize(double delta_t) {
        //set gamma to 1/2, beta to 1/4
        //initialise left side matrix
        A = K.copy();

        this.delta_t = delta_t;


        //LU solver
        solver = LinearSolverFactory.lu(k1.getNumberofDOF());
        solver.setA(A);


        //initialize fLoad_old
        fLoad = new DenseMatrix64F(k1.getNumberofDOF(), 1);
        fLoad.zero();
    }

    /**
     *
     *
     * @return ddotx_n+1
     */
    private void getAcceleration(){


    }
}

//solver.solve(input,output);