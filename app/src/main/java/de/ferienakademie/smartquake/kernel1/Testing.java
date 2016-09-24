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

        DenseMatrix64F LoadVector = new DenseMatrix64F(6,1);
        DenseMatrix64F Displacement = new DenseMatrix64F(6,1);
        Displacement.zero();

        LoadVector.add(0,0,0);
        LoadVector.add(1,0,0);
        LoadVector.add(2,0,0);
        LoadVector.add(3,0,1);
        LoadVector.add(4,0,0);
        LoadVector.add(5,0,0);
        Log.d("Cantilever Disp.test", "doener");
        LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.lu(6);
        solver.setA(kern1.getStiffnessMatrix());
        solver.solve(LoadVector,Displacement);
        Log.d("Cantilever Disp.test", Displacement.toString());

    }
    public static void rotatedcantiLeverStaticTest(Structure structure){
        Kernel1 kern1 = new Kernel1(structure);
        kern1.initMatrices();

        DenseMatrix64F LoadVector = new DenseMatrix64F(6,1);
        DenseMatrix64F Displacement = new DenseMatrix64F(6,1);
        Displacement.zero();

        LoadVector.add(0,0,0);
        LoadVector.add(1,0,0);
        LoadVector.add(2,0,0);
        LoadVector.add(3,0,1);
        LoadVector.add(4,0,0);
        LoadVector.add(5,0,0);
        Log.d("Cantilever Disp.test", "doener");
        LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.lu(6);
        solver.setA(kern1.getStiffnessMatrix());
        solver.solve(LoadVector,Displacement);
        Log.d("Cantilever Disp.test", Displacement.toString());

    }

}
