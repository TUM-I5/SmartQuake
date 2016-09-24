package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.Kernel1;

import android.util.Log;

/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class ExplicitSolver extends Solver {
    //for the direct sensor input
    double[] acceleration;

    DenseMatrix64F tempVector;
    //to solve M * xDotDot = f(x, xDot, t)
    LinearSolver<DenseMatrix64F> linearSolverM;
    /**
     *
     * @param k1
     * @param xDot
     */
    public ExplicitSolver(Kernel1 k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot) {
        super(k1, accelerationProvider, xDot);

        //sets up fast linear solver
        linearSolverM = LinearSolverFactory.chol(k1.getNumDOF());
        for(int i=0; i<k1.getNumDOF(); i++){
            M.set(i,i,0.0001);
        }
        linearSolverM.setA(M);

        tempVector = new DenseMatrix64F(k1.getNumDOF(),1);

        //JUST FOR TESTING
       /* C.zero();
        K.zero();
        for (int j = 6; j < k1.getNumDOF(); j += 3) {
            C.set(j,j,0);
            C.set(j+1,j+1,0);
            K.set(j,j,0.001);
            K.set(j+1,j+1,0.001);
        }


        C = k1.getDampingMatrix();
        K = k1.getStiffnessMatrix();*/
    }


    /**
     * This method provides for all explicit solver the acceleration of all nodes
     */
    public void getAcceleration() {
        //just temporarlily bypass kernel1
        //acceleration = getAccelerationProvider().getAcceleration();
        //k1.updateLoadVector(acceleration);

        //tempVector = k1.getLoadVector();
        //tempVector = k1.getLoadVector().copy();


        //for (int j = 6; j < k1.getNumDOF(); j += 3) {
        //    tempVector.set(j, 0, 20 * acceleration[0] );
        //    tempVector.set(j + 1, 0, 20 * acceleration[1] );
        //}

        tempVector = fLoad;

        // next two steps calculating this: tempVecotr= tempVector - C*xDot - K*x
        // 1.: tempVector = tempVector - C*xDot
        //CommonOps.multAdd(-1, C,xDot,tempVector);

        multMatrices(C,xDot, tempVector);

        subMatrices(tempVector, fLoad);

        multMatrices(K, x, tempVector);

        subMatrices(tempVector, fLoad);

        //2.: tempVector = tempVector - K*x
        //CommonOps.multAdd(-1, K,x,tempVector);

        //Log.d("Acceleretation", tempVector.toString());


        xDotDot = tempVector;
        for( int i =0; i<k1.getNumDOF(); i++){
            xDotDot.set(i,0, 100000*1/628.0*xDotDot.get(i,0));
            //xDotDot.set(i,0, -10);
        }

        //xDotDot = tempVector.copy();
        //linearSolverM.solve(tempVector, xDotDot);


        //Log.e("messagen for felix", xDotDot.toString());
    }


    //JUST FOR TESTING
    //DONT USE
    public void multMatrices(DenseMatrix64F matrix, DenseMatrix64F vector,  DenseMatrix64F resultVector){
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                resultVector.add(i,0, matrix.get(i,j)*vector.get(j,0));
            }
        }
    }

    public void subMatrices(DenseMatrix64F vector1, DenseMatrix64F resultVector){
        for(int i=0; i<15; i++){
            resultVector.set(i,0,resultVector.get(i,0)-vector1.get(i,0));
        }
    }
}
