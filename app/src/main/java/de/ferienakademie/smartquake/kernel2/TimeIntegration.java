package de.ferienakademie.smartquake.kernel2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    Kernel1 kernel1;

    SimulationProgressListener listener;

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
                for(int j=0; j<kernel1.getNumDOF(); j+=3){
                    xDot.add(j,0, 1);
                }
                //xDotDot must be calculated by the external load forces and the differnetial equation

                //THIS IS JUST A WORKAROUND/MINIMAL EXAMPLE
                DenseMatrix64F xDotDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
                xDotDot.zero();

                //only for fixed stepsize
                delta_t = 0.0001;
                for (int i = 0; i < 1000; i++) {
                    //calculate new position
                    solver.nextStep(kernel1.getDisplacementVector(), xDot, xDotDot,t, delta_t);
                    while (kernel1.getView().isBeingDrawn) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException ex) {
                            Log.e("TimeIntegration", ex.getMessage());
                        }
                    }
                    for(int j=0; j<kernel1.getNumDOF(); j+=3){
                        xDotDot.add(j,0, -10*kernel1.getDisplacementVector().get(j, 0));
                    }
                    kernel1.updateStructure(kernel1.getDisplacementVector());
                    t += delta_t;
                }
                if (listener != null) {
                    listener.onFinish();
                }

            }
        }).start();
    }

    public void setListener(SimulationProgressListener listener) {
        this.listener = listener;
    }

    public interface SimulationProgressListener {

        /**
         * Is called after simulation finishes. Is called from background thread.
         */
        void onFinish();
    }

}
