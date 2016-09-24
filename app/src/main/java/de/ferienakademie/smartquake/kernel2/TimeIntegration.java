<<<<<<< HEAD
=======
package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    Kernel1 kernel1;
    AccelerationProvider accelerationProvider;

    // total computed time between every time step. This variable prevents computing more than GUI
    double t;
    // time step
    double delta_t;

    double globalTime;

    // matrices of velocity
    DenseMatrix64F xDot;

    // provides the numerical algorithm for calculating the displacement
    TimeIntegrationSolver solver;

    // manages the multi threading
    ExecutorService executorService;



    /**
    * @param kernel1
    *          object to obtain all matrices, displacements, external forces
    *
    **/
    public TimeIntegration(Kernel1 kernel1, AccelerationProvider accelerationProvider) {
        this.kernel1 = kernel1;
        this.accelerationProvider = accelerationProvider;
    }


    /**
     * This method is called from the Simulation class to prepare everything for simulation
     */
    public void prepareSimulation(){
        // initial condition for the velocity.
        xDot = new DenseMatrix64F(kernel1.getNumDOF(),1);

        // TODO: This is just temporarily. Should be chosen correctly
        xDot.zero();

        // stores the numerical scheme
        solver = new Newmark(kernel1, accelerationProvider, xDot,delta_t);
        //solver = new Euler(kernel1, accelerationProvider, xDot);

        // fixed step size for implicit schemes
        delta_t = 0.001;

        executorService = Executors.newSingleThreadExecutor();
    }

    public SimulationStep performSimulationStep() {
        return new SimulationStep().execute();
    }

    /**
     * Class that represents single simulation step of {@link TimeIntegration}.
     * If simulation step can not be performed during a single frame, it will be stopped.
     */
    public class SimulationStep {

        boolean isRunning;

        public SimulationStep execute() {
            isRunning = true;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //reset time
                    t = 0;

                    //calculates time step

                    //update loadVector
                    kernel1.updateLoadVector(accelerationProvider.getAcceleration());
                    //get the loadVector for the whole calculation
                    solver.setFLoad(kernel1.getLoadVector());

                    long firstTime = System.nanoTime();
                    while(t < 0.03+0.000001 && isRunning) {
                        //calculate new displacement
                        solver.nextStep(t, delta_t);
                        t += delta_t;

                    }
                    //for the sensor team
                    globalTime += 0.03;

                    //for recording
                    long secondTime = System.nanoTime();
                    Log.e("Timestamp",""+(secondTime-firstTime));

                    //update the displacement in the node variables
                    kernel1.updateStructure(kernel1.getDisplacementVector());

                    isRunning = false;
                }
            });
            return this;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void stop() {
            isRunning = false;
        }

    }
}
>>>>>>> 2330e3f1192372b559432921e13e4272ff014c18
