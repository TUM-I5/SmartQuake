package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import de.ferienakademie.smartquake.kernel1.Kernel1;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    /*
    * @param k1
    *          object of type structure to obtain all matrices, displacements, external forces
    *
    **/
    public void startSimulation(Kernel1 kernel1, Structure structure){
        //TODO sent frequently data to GUI.
        // TODO Transform matrix x into the displacements in the beam object

        //stores the global simulation time
        double t = 0;
        double delta_t;

        //this solver provides the numerical algorith  for calculating the displacement
        TimeIntegrationSolver solver = new Euler();

        //initial condition for the velocity
        DenseMatrix64F xDot = new DenseMatrix64F(structure.getNumDOF(),1);
        xDot.zero();

        //xDotDot must be calculated by the external load forces and the differnetial equation

        //THIS IS JUST A WORKAROUND/MINIMAL EXAMPLE
        DenseMatrix64F xDotDot = new DenseMatrix64F(structure.getNumDOF(),1);
        xDotDot.zero();
        //fill withs 1s
        for(int i = 0; i< structure.getNumDOF(); i++){
            xDotDot.set(i,i, 1);
        }


        //only for fixed stepsize
        delta_t = 0.001;
        for (int i = 0; i < 100000; i++) {
            //calculate new position
            solver.nextStep(structure.getDisplacementVector(), xDot, xDotDot,t, delta_t);
            kernel1.updateStructure(structure.getDisplacementVector());
            t += delta_t;
        }

    }

}
