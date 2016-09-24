package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    SpatialDiscretization spatialDiscretization;
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
    * @param spatialDiscretization
    *          object to obtain all matrices, displacements, external forces
    *
    **/
    public TimeIntegration(SpatialDiscretization spatialDiscretization, AccelerationProvider accelerationProvider) {
        this.spatialDiscretization = spatialDiscretization;
        this.accelerationProvider = accelerationProvider;
    }


    /**
     * This method is called from the Simulation class to prepare everything for simulation
     */
    public void prepareSimulation(){

        //initial condition for the velocity.
        xDot = new DenseMatrix64F(spatialDiscretization.getNumberofDOF(),1);
        //This is just temporarily. In future this should choosen in the right way
        xDot.zero();

        //stores the numerical scheme
        //solver = new Newmark(kernel1, xDot,delta_t);
        solver = new Euler(spatialDiscretization, accelerationProvider, xDot);

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
                    spatialDiscretization.updateLoadVector(accelerationProvider.getAcceleration());


                    //Log.d("load vector", ""+spatialDiscretization.getLoadVector().toString());
                    //Log.d("xDotDot", solver.getXDotDot().toString());
                    //Log.d("xDotDot inside TimInt", solver.getXDotDot().toString());
                    //get the loadVector for the whole calculation
                    solver.setFLoad(spatialDiscretization.getLoadVector());

                   // long firstTime = System.nanoTime();
                    while(t < 0.03+0.000001 && isRunning) {
                        //calculate new displacement
                        solver.nextStep(t, delta_t);
                        t += delta_t;

                    }
                    //for the sensor team
                    globalTime += 0.03;

                    //Log.d("Inside Time Itegration", solver.getFLoad().toString());
                    //for recording
                    //long secondTime = System.nanoTime();
                    //Log.e("Timestamp",""+(secondTime-firstTime));

                    //update the displacement in the node variables
                    spatialDiscretization.updateStructure(spatialDiscretization.getDisplacementVector());

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
