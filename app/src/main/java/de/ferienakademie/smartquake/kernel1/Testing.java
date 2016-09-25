package de.ferienakademie.smartquake.kernel1;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.eigenvalueProblems.GenEig;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by alex on 23.09.16.
 */
public class Testing {
    public static void cantiLeverStaticTest(Structure structure){
        SpatialDiscretization kern1 = new SpatialDiscretization(structure);
        kern1.initializeMatrices();

        DenseMatrix64F LoadVector = new DenseMatrix64F(9,1);
        DenseMatrix64F Displacement = new DenseMatrix64F(9,1);
        Displacement.zero();

        LoadVector.add(0,0,0);
        LoadVector.add(1,0,0);
        LoadVector.add(2,0,0);
        LoadVector.add(3,0,0);
        LoadVector.add(4,0,0);
        LoadVector.add(5,0,0);
        LoadVector.add(6,0,1);
        LoadVector.add(7,0,0);
        LoadVector.add(8,0,0);


        //Solving eigenvalue problem test
        //GenEig eigen = new GenEig(kern1.getStiffnessMatrix(),kern1.getMassMatrix()); //TODO does it something inplace???
        //double[] frequences = eigen.getLambda();



        LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.lu(kern1.getNumberofDOF());
        solver.setA(kern1.getStiffnessMatrix());
        solver.solve(LoadVector,Displacement);

        double eps = 10e-8;

        if ( Math.abs(Displacement.get(4,0)-structure.getBeams().get(0).getLength()/structure.getBeams().get(0).getMaterial().getAxialStiffnessOfBar())>eps){
            throw new RuntimeException("Cantilever test failed");
        }else{
            Log.d("Testing: ", "Cantilever test passed");
        }
    }
    public static void rotatedcantiLeverStaticTest(Structure structure){
        SpatialDiscretization kern1 = new SpatialDiscretization(structure);
        kern1.initializeMatrices();

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
