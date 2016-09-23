package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.kernel1.Kernel1;

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
    public ExplicitSolver(Kernel1 k1, DenseMatrix64F xDot) {
        super(k1, xDot);

        //sets up fast linear solver
        linearSolverM = LinearSolverFactory.chol(k1.getNumDOF());
        linearSolverM.setA(M);

        tempVector = new DenseMatrix64F(k1.getNumDOF(),1);
    }


    /**
     * This method provides for all explicit solver the acceleration of all nodes
     */
    public void getAcceleration() {

        tempVector = k1.getLoadVector().copy();

        C.zero();
        K.zero();
        for (int j = 6; j < k1.getNumDOF(); j += 3) {
            C.set(j,j,5);
            C.set(j+1,j+1,5);
            K.set(j,j,100);
            K.set(j+1,j+1,100);
            tempVector.set(j, 0, 2000 * acceleration[0] );
            tempVector.set(j + 1, 0, 2000 * acceleration[1] );
        }




        // next two steps calculating this: tempVecotr= tempVector - C*xDot - K*xD
        // 1.: tempVector = tempVector - C*xDot
        CommonOps.multAdd(-1, C,xDot,tempVector);

        //2.: tempVector = tempVector - K*x
        CommonOps.multAdd(-1, K,x,tempVector);


        linearSolverM.solve(tempVector, xDotDot);

        //OLDSTUFF JUST LEAVE IT HERE
       /* acceleration = k1.getAccelerationProvider().getAcceleration();
        for (int j = 6; j < k1.getNumDOF(); j += 3) {
            xDotDot.set(j, 0, 2000 * acceleration[0] - 5 * xDot.get(j, 0) - 100 * k1.getDisplacementVector().get(j, 0));
            xDotDot.set(j + 1, 0, 2000 * acceleration[1] - 5 * xDot.get(j + 1, 0) - 100 * k1.getDisplacementVector().get(j + 1, 0));
        }
        */
    }
}
