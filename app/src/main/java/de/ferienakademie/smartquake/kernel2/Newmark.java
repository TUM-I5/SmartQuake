package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Claudius, Lukas and John on 23/09/16.
 */
public class Newmark extends ImplicitSolver {

    /**
     *
     * @param k1
     * @param xDot
     * @param delta_t is necessary to precalculate left and right hand side
     */
    public Newmark(Kernel1 k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot, double delta_t) {
        super(k1, accelerationProvider, xDot);
        initialize(delta_t);

        //DEBUG
        Log.d("const", "Newmark: ");
        bla = false;
    }

    //Right and left hand side matrix
    DenseMatrix64F B; //right
    DenseMatrix64F A; //left

    //old load vector
    DenseMatrix64F fLoad_old;

    //Fixed time step
    double delta_t;

    //DEBUGGING
    boolean bla;


    @Override
    public void nextStep(double t, double delta_t) {

        DenseMatrix64F xDotDot_old = xDotDot;

        if(this.delta_t != delta_t){
            //TODO Throw exception
        }

        //Get acceleration
        setAcceleration();

        //TODO: Remove debugshit
        Log.d(xDotDot.toString(), "se");





        //Calculate velocity
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot);
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot_old);

        //Calculate displacement
        CommonOps.addEquals(x,delta_t,xDot); //x = x + delta_t*xDot
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot); //x = delta_t**2*xDotDot/4
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot_old); // x = x + delta_t**2*xDotDot_old/4

        //update fLoad_old
        fLoad_old = fLoad;

        //Log.d(xDotDot.toString(), "nextStep: ");

    }


    private void initialize(double delta_t) {
        //set gamma to 1/2, beta to 1/4
        //initialise left side matrix
        A = new DenseMatrix64F(k1.getNumDOF(),k1.getNumDOF());
        A.zero();

        this.delta_t = delta_t;

        CommonOps.addEquals(A,1,M); //A = A + M
        CommonOps.addEquals(A,delta_t/2.0,C); //A = A + delta_t/2*C
        CommonOps.addEquals(A,delta_t*delta_t/4.0,K); //A = A + delta_t**2*K/4

        //LU solver
        solver = LinearSolverFactory.lu(k1.getNumDOF());
        solver.setA(A);

        //initialise right side matrices: F_ext - K*dotx - B*ddotx
        B = new DenseMatrix64F(k1.getNumDOF(),k1.getNumDOF());
        B.zero();

        CommonOps.addEquals(B,delta_t/2.0,C);
        CommonOps.addEquals(B,delta_t*delta_t/4.0,K);
        CommonOps.addEquals(B,-1,M);

        //initialize fLoad_old
        fLoad_old = new DenseMatrix64F(k1.getNumDOF(), 1);
        fLoad_old.zero();


    }

    /**
     *
     * @param t time
     * @return ddotx_n+1
     */
    private void setAcceleration(){

        //initialize right hand side
        DenseMatrix64F RHS = new DenseMatrix64F(k1.getNumDOF(),1);
        RHS.zero();

        //Calculate RHS
        CommonOps.multAdd(-1,K,xDot,RHS); //RHS = RHS - K*x
        CommonOps.multAdd(-1,B,xDotDot,RHS); //RHS = RHS - B*xDotDot
        CommonOps.addEquals(RHS,1,fLoad); //RHS = RHS + fLoad
        CommonOps.addEquals(RHS,-1,fLoad_old); //RHS = RHS - fLoad_old

        if(!bla){
            Log.d(xDotDot.toString(), "setAcceleration: ");
        }
        //Solve
        solver.solve(RHS,xDotDot); //solver.A*xDotDot = RHS

        if(!bla){
            Log.d(xDotDot.toString(), "setAcceleration: ");
        }

        bla = true;

    }
}

//solver.solve(input,output);