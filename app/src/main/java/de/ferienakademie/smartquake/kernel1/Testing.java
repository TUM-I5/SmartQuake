package de.ferienakademie.smartquake.kernel1;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by alex on 23.09.16.
 */
public class Testing {
    public static void cantiLeverStaticTest(Structure structure){
        Kernel1 kern1 = new Kernel1(structure);
        kern1.initMatrices();

        DenseMatrix64F LoadVector = new DenseMatrix64F(1,6);
        DenseMatrix64F Displacement = new DenseMatrix64F(1,6);

        LoadVector.add(0,0,0);
        LoadVector.add(0,1,0);
        LoadVector.add(0,2,0);
        LoadVector.add(0,3,1);
        LoadVector.add(0,4,0);
        LoadVector.add(0,5,0);

        LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.lu(6);
        solver.setA(kern1.getStiffnessMatrix());
        solver.solve(LoadVector,Displacement);
        Log.d("Cantilever Disp.test", Displacement.toString());
    }
}
