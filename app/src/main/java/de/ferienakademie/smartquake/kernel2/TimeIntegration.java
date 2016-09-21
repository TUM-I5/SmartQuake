package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    /*
    * @param k1
    *          object of type structure to obtain all matrices
    *
    **/
    public void startSimulation(Structure k1){

        TimeIntegrationSolver solver = new Euler();
        DenseMatrix64F xDot = new DenseMatrix64F(k1.getNumDOF());
        xDot.zero();

        for (int i = 0; i < 100000; i++) {

        }

    }

}
