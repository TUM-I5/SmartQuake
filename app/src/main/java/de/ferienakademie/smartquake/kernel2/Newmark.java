package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

/**
 * Created by Claudius, Lukas und John on 23/09/16.
 */
public class Newmark extends ImplicitSolver {

    /**
     *
     * @param k1
     * @param xDot
     * @param delta_t is necessary to precalculate left and right hand side
     */
    public Newmark(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot, double delta_t) {
        super(k1, accelerationProvider, xDot);
        initialise(delta_t);
    }

    //Right and left hand side matrix
    DenseMatrix64F B;
    DenseMatrix64F A;
    DenseMatrix64F V;

    //Fixed time step
    double delta_t;


    @Override
    public void nextStep(double t, double delta_t) {

        DenseMatrix64F xDotDot_old = xDotDot;

        if(this.delta_t != delta_t){
            //TODO Throw exception
        }

        //Get acceleration
        xDotDot = getAcceleration(t);

        //Calculate velocity
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot);
        CommonOps.addEquals(xDot,delta_t/2.0,xDotDot_old);

        //Calculate displacement
        CommonOps.addEquals(x,delta_t,xDot); //x = x + delta_t*xDot
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot); //x = delta_t**2*xDotDot/4
        CommonOps.addEquals(x,delta_t*delta_t/4.0,xDotDot_old); // x = x + delta_t**2*xDotDot_old/4

    }


    private void initialise(double delta_t) {
        //set gamma to 1/2, beta to 1/4
        //initialise left side matrix
        A = new DenseMatrix64F(k1.getNumberofDOF(),k1.getNumberofDOF());
        A.zero();

        this.delta_t = delta_t;

        CommonOps.addEquals(A,1,M); //AreaOfCrossSection = AreaOfCrossSection + M
        CommonOps.addEquals(A,delta_t/2.0,C); //AreaOfCrossSection = AreaOfCrossSection + delta_t/2*C
        CommonOps.addEquals(A,delta_t*delta_t/4.0,K); //AreaOfCrossSection = AreaOfCrossSection + delta_t**2*K/4

        //LU solver
        solver = LinearSolverFactory.lu(k1.getNumberofDOF());
        solver.setA(A);

        //initialise right side matrices: F_ext - V*dotx - K*x - B*ddotx
        B = new DenseMatrix64F(k1.getNumberofDOF(),k1.getNumberofDOF());
        B.zero();

        CommonOps.addEquals(B,delta_t/2.0,C);
        CommonOps.addEquals(B,delta_t*delta_t/4.0,K);

        V = new DenseMatrix64F(k1.getNumberofDOF(),k1.getNumberofDOF());
        V.zero();

        CommonOps.addEquals(V,1,C);
        CommonOps.addEquals(V,delta_t,K);
    }

    /**
     *
     * @param t time
     * @return ddotx_n+1
     */
    private DenseMatrix64F getAcceleration(double t){

        DenseMatrix64F acc = new DenseMatrix64F(k1.getNumberofDOF(),1);
        acc.zero();

        //Get external forces
        DenseMatrix64F f_load = k1.getLoadVector();

        //initialize right hand side
        DenseMatrix64F RHS = new DenseMatrix64F(k1.getNumberofDOF(),1);
        RHS.zero();

        //Calculate RHS
        CommonOps.multAdd(-1,K,x,RHS); //RHS = RHS - K*x
        CommonOps.multAdd(-1,V,xDot,RHS); //RHS = RHS - V*xDot
        CommonOps.multAdd(-1,B,xDotDot,RHS); //RHS = RHS - B*xDotDot
        CommonOps.addEquals(RHS,1,f_load); //RHS = RHS + f_load

        //Solve
        solver.solve(RHS,acc); //solver.AreaOfCrossSection*acc = RHS

        return acc;
    }
}

//solver.solve(input,output);