package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    Kernel1 kernel1;

    //total computed time
    double t;
    //time_step
    double delta_t;

    //JUST FOR TESTING DIRECTLY SENSORS INPUT
    double[] acceleration;

    //matrices of velocity
    DenseMatrix64F xDot;
    //matrices of acceleration
    DenseMatrix64F xDotDot;

    //this solver provides the numerical algorithm  for calculating the displacement
    TimeIntegrationSolver solver;


    ExecutorService executorService;

    /**
    * @param kernel1
    *          object to obtain all matrices, displacements, external forces
    *
    **/
    public TimeIntegration(Kernel1 kernel1) {
        this.kernel1 = kernel1;
    }


    public void prepareSimulation(){
        //stores the global simulation time
        t = 0;

        solver = new Euler();

        //initial condition for the velocity
        xDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
        xDot.zero();
        for(int j=0; j<kernel1.getNumDOF(); j+=3){
            xDot.add(j,0, 10);
        }
        //xDot.add(3,0,20);
        //xDot.add(1,0,40);
        //xDot.add(6,0,-20);
        //xDot.add(10,0,-10);
        //xDot.add(4,0,-10);
        //xDot.add(7,0,-80);
        //xDot.add(12,0,150);
        //xDot.add(13,0,100);
        xDot.zero();
        //xDotDot must be calculated by the external load forces and the differnetial equation

        //THIS IS JUST A WORKAROUND/MINIMAL EXAMPLE
        xDotDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
        xDotDot.zero();

        //only for fixed stepsize
        delta_t = 0.001;

        executorService = Executors.newSingleThreadExecutor();
    }

    public SimulationStep performSimulationStep() {
        return new SimulationStep().execute();
    }

    /**
     * Class that represent single simulation step of {@link TimeIntegration}.
     * If simulation step can not be performed during a single frame, it will be stopped.
     */
    public class SimulationStep {

        boolean isRunning;

        public SimulationStep execute() {
            isRunning = true;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    t = 0;
                    //long firstTime = System.nanoTime();
                    while(t < 0.021 && isRunning) {
                        //calculate new position
                        solver.nextStep(kernel1.getDisplacementVector(), xDot, xDotDot,t, delta_t);
                        acceleration=kernel1.getAccelerationProvider().getAcceleration();
                        for(int j=6; j<kernel1.getNumDOF(); j+=3){
                            xDotDot.set(j,0, 2000*acceleration[0]-5*xDot.get(j,0)-100*kernel1.getDisplacementVector().get(j, 0));
                            xDotDot.set(j+1,0, 2000*acceleration[1]-5*xDot.get(j+1,0)-100*kernel1.getDisplacementVector().get(j+1, 0));
                        }
                        //temporarily fix the ground
                        kernel1.updateStructure(kernel1.getDisplacementVector());
                        t += delta_t;
                    }
                    //long secondTime = System.nanoTime();
                    //Log.e("TImestamp",""+(secondTime-firstTime));
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
