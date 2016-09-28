package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

/**
 * Created by Felix Wechsler on 23/09/16.
 */
public class ExplicitSolver extends Solver {
    //for the direct sensor input
    double[] acceleration;

    DenseMatrix64F tempVector;
    DenseMatrix64F tempVector2;

    //to solve M * xDotDot = f(x, xDot, t)
    LinearSolver<DenseMatrix64F> linearSolverM;
    /**
     *
     * @param k1
     * @param xDot
     */
    public ExplicitSolver(SpatialDiscretization k1, AccelerationProvider accelerationProvider, DenseMatrix64F xDot) {
        super(k1, accelerationProvider, xDot);

        //sets up fast linear solver
        linearSolverM = LinearSolverFactory.chol(k1.getNumberOfDOF());
//        for(int i = 0; i<k1.getNumberOfDOF(); i++){
//            M.set(i,i,0.0001);
//        }
        linearSolverM.setA(M);

        tempVector = new DenseMatrix64F(k1.getNumberOfDOF(),1);
        tempVector2 = new DenseMatrix64F(k1.getNumberOfDOF(),1);


        C = k1.getDampingMatrix();
        K = k1.getStiffnessMatrix();

       // for(int i=0; i<k1.getNumberOfDOF(); i++){
       //     K.set(i,i,K.get(i,i)*0.000000001);
       // }
        //M is inverse at the moment
        //PAY attention
        M = k1.getInverseMassMatrix();
    }


    /**
     * This method provides for all explicit solver the acceleration of all nodes
     */
    public void getAcceleration() {
        //only for testing
       //for(int i=0; i<15; i++){
       //     fLoad.set(i,0,1);
       //}

        // next steps calculating this: tempVecotr= tempVector - C*xDot - K*x
        tempVector2 = fLoad.copy();

        //multMatrices(K,x,tempVector);
        //multMatrices(C, xDot, tempVector);
        //subMatrices(tempVector,tempVector2);

        // 1.: tempVector = tempVector - C*xDot
        CommonOps.multAdd(-1, C,xDot,tempVector2);
        //2.: tempVector = tempVector - K*x
        CommonOps.multAdd(-1, K,x,tempVector2);

        xDotDot = tempVector2;
        for(int i = 0; i<k1.getNumberOfDOF(); i++){
            xDotDot.set(i,0, M.get(i,i)*xDotDot.get(i,0));
        }

    }


    //JUST FOR TESTING
    //DONT USE
    public void multMatrices(DenseMatrix64F matrix, DenseMatrix64F vector,  DenseMatrix64F resultVector){
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                resultVector.add(i,0, -1* matrix.get(i,j)*vector.get(j,0));
            }
        }
    }

    public void subMatrices(DenseMatrix64F vector1, DenseMatrix64F resultVector){
        for(int i=0; i<15; i++){
            resultVector.set(i,0,resultVector.get(i,0)-vector1.get(i,0));
        }
    }
}
