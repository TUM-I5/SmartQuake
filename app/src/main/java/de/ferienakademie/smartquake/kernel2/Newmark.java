package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.managers.PreferenceReader;

/**
 * Created by Claudius, Lukas and John on 23/09/16.
 * This class implements a solver. This is a implicit solver. We use the \beta-Newmark implementation.
 * It is unconditional stable.
 */
public class Newmark extends ImplicitSolver {

    //Right and left hand side matrix
    DenseMatrix64F B; //right
    DenseMatrix64F A; //left


    //right hand side of implicit differential equation
    DenseMatrix64F RHS;
    //old load vector
    DenseMatrix64F fLoad_old;
    //old xDotDot
    DenseMatrix64F xDotDot_old;

    //Fixed time step
    double delta_t;


    /**
     *
     * @param k1
     * @param xDot
     * @param delta_t is necessary to precalculate left and right hand side
     */
    public Newmark(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot, double delta_t) {
        super(k1, accelerationProvider, xDot);
        //initialization
        initialize(delta_t);
    }


    /**
     * This method initializes RHS, the solverFactor, A, delta_t, B
     * @param delta_t
     */
    private void initialize(double delta_t) {
        //set gamma to 1/2, beta to 1/4
        //initialise left side matrix
        A = M.copy();

        //delta_t fixed
        this.delta_t = delta_t;

        //A is calculated
        CommonOps.addEquals(A,delta_t/2.0,C); //A = A + delta_t/2*C
        CommonOps.addEquals(A,delta_t*delta_t/4.0,K); //A = A + delta_t**2*K/4

        //LU solver
        //TODO: number of rows for modal analysis case is k1.getNumberOfUnconstraintDOF
        solver = LinearSolverFactory.lu(k1.getNumberOfDOF());
        solver.setA(A);

        //initialise right side matrices: F_ext - K*xDot- B*xDotDot
        B = new DenseMatrix64F(C.getNumRows(), C.getNumRows());
        CommonOps.addEquals(B,delta_t/2.0,C);
        CommonOps.addEquals(B,delta_t*delta_t/4.0,K);
        CommonOps.addEquals(B,-1,M);

        //initialize fLoad_old
        fLoad_old = new DenseMatrix64F(C.getNumRows(), 1);

    }


    /**
     * @param t
     *        global time since start in seconds
     * @param delta_t
     *        time step
     */
    @Override
    public void nextStep(double t, double delta_t) {

        xDotDot_old = xDotDot.copy();

        //Get acceleration
        //Modal analysis has diagonalized matrices. so getAcceleration isf faster
        if(PreferenceReader.useModalAnalysis()){
            getAccelerationModalAnalysis();
        }
        else{
            //normal matrices
            getAcceleration();
        }

        //Calculate velocity
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot);
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot_old);

        //Calculate displacement
        CommonOps.addEquals(x,delta_t,xDot); //x = x + delta_t*xDot
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot); //x = delta_t**2*xDotDot/4
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot_old); // x = x + delta_t**2*xDotDot_old/4


        //update fLoad_old
        fLoad_old = fLoad.copy();
        //diagonalizes everything
    }


    /**
     * This method gets the acceleration. It will be written in place into xDotDot
     * It is calculated out of fload Vec
     */
    private void getAcceleration(){

        //initialize right hand side
        RHS = fLoad.copy();

        //Calculate RHS
        CommonOps.multAdd(-delta_t,K,xDot,RHS); //RHS = RHS - delta_t*K*xDot
        CommonOps.multAdd(-1,B,xDotDot,RHS); //RHS = RHS - B*xDotDot
        CommonOps.addEquals(RHS,-1,fLoad_old); //RHS = RHS - fLoad_old

        //Solve to get xDotDot
        solver.solve(RHS,xDotDot); //solver.A*acc = RHS
    }


    private void getAccelerationModalAnalysis(){

        //initialize right hand side
        RHS = fLoad.copy();

        //Calculate RHS
        multAddDiagMatrix(-delta_t,K,xDot,RHS); //RHS = RHS - delta_t*K*xDot
        multAddDiagMatrix(-1,B,xDotDot,RHS); //RHS = RHS - B*xDotDot
        CommonOps.addEquals(RHS,-1,fLoad_old); //RHS = RHS - fLoad_old

        //Solve to get xDotDot; A*xDotDot = RHS
        for(int i = 0; i<RHS.getNumRows(); i++){
            xDotDot.set(i,0,RHS.get(i,0)/A.get(i,i));
        }
    }

    /**
     * result = result + skalar*matrix*vec
     * @param delta_t
     * @param matrix
     * @param vec
     * @param result
     */
    private void multAddDiagMatrix(double delta_t, DenseMatrix64F matrix, DenseMatrix64F vec, DenseMatrix64F result){
        if (matrix.getNumCols() != vec.getNumRows() || result.getNumRows() != matrix.getNumRows()) {
            throw new AssertionError("Try to multiply and add matrices of uncompatible size.");
        }
        for(int i = 0; i< k1.getNumberOfModes(); i++) {
            result.set(i, 0, result.get(i, 0) + delta_t * matrix.get(i, i) * vec.get(i, 0));
        }
    }

    private void addEqualsDiagMatrix(){

    }

}
