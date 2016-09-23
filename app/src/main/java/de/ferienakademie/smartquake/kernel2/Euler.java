package de.ferienakademie.smartquake.kernel2;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class Euler implements TimeIntegrationSolver {

    //vector of acceleration=xDotDot
    DenseMatrix64F xDotDot;
    //vector of velocity=xDot
    DenseMatrix64F xDot;
    //vector of displacement=x
    DenseMatrix64F x;

    //Stiffness Matrix
    DenseMatrix64F K;
    //Damping Matrix
    DenseMatrix64F C;
    //Mass Matrix
    DenseMatrix64F M;

    //connection to kernel1
    Kernel1 k1;


    //TEMPORARLY
    double[] acceleration;

    public Euler(Kernel1 k1, DenseMatrix64F xDot){
        this.k1 = k1;

        this.M = k1.getMassMatrix();
        this.K = k1.getStiffnessMatrix();
        this.C = k1.getDampingMatrix();
        this.x = k1.getDisplacementVector();

        this.xDot = xDot;
        xDotDot = new DenseMatrix64F(k1.getNumDOF(),1);
        xDotDot.zero();
    }

    @Override
    public void nextStep(double t, double delta_t) {
        //store old (n-1) velocity
        getForce();
        DenseMatrix64F oldxDot = xDot.copy();

        //velocity at n
        CommonOps.addEquals(xDot, delta_t, xDotDot);

        //create average matrix of velocities at step n and n+1
        DenseMatrix64F averageXDot=xDot.copy();
        CommonOps.addEquals(averageXDot,1, oldxDot);

        //displacement at step n+1
        CommonOps.addEquals(x, 1/2.0 * delta_t, oldxDot);

    }



    public void getForce(){
        acceleration=k1.getAccelerationProvider().getAcceleration();
        for(int j=6; j<k1.getNumDOF(); j+=3){
            xDotDot.set(j,0, 2000*acceleration[0]-5*xDot.get(j,0)-100*k1.getDisplacementVector().get(j, 0));
            xDotDot.set(j+1,0, 2000*acceleration[1]-5*xDot.get(j+1,0)-100*k1.getDisplacementVector().get(j+1, 0));
        }
    }

}
