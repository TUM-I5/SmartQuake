package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    Kernel1 kernel1;

    public TimeIntegration(Kernel1 kernel1) {
        this.kernel1 = kernel1;
    }
    /*
    * @param k1
    *          object of type structure to obtain all matrices, displacements, external forces
    *
    **/
    public void startSimulation() {
        //TODO sent frequently data to GUI.

        new Thread(new Runnable() {
            @Override
            public void run() {
                //stores the global simulation time
                double t = 0;
                double delta_t;

                //this solver provides the numerical algorith  for calculating the displacement
                TimeIntegrationSolver solver = new Euler();

                //initial condition for the velocity
                DenseMatrix64F xDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
                xDot.zero();

                //xDotDot must be calculated by the external load forces and the differnetial equation

                //THIS IS JUST A WORKAROUND/MINIMAL EXAMPLE
                DenseMatrix64F xDotDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
                xDotDot.zero();

                //only for fixed stepsize
                delta_t = 0.001;
                for (int i = 0; i < 100000; i++) {
                    //calculate new position
                    solver.nextStep(kernel1.getDisplacementVector(), xDot, xDotDot,t, delta_t);
                    while (kernel1.getView().isBeingDrawn) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException ex) {
                            Log.e("TimeIntegration", ex.getMessage());
                        }
                    }
                    kernel1.updateStructure(kernel1.getDisplacementVector());
                    t += delta_t;
                    for(int j=0; i<kernel1.getNumDOF(); j+=2){
                        xDotDot.add(j,0, 3*Math.sin(t/40*delta_t));
                    }
                }

            }
        }).start();

    }

}
