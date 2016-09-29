package de.ferienakademie.smartquake.kernel2;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.managers.PreferenceReader;

/**
 * This is the main class for the simulation.
 * The time integration is here managed.
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    //object to obtain matrices
    SpatialDiscretization spatialDiscretization;
    //object to get sensor data
    AccelerationProvider accelerationProvider;

    // total computed time between every time step. This variable prevents computing more than GUI wants
    double t;
    // time step
    double delta_t;
    //globale time since startSimulation
    double globalTime;

    // matrices of velocity
    DenseMatrix64F xDot;

    // provides the numerical algorithm for calculating the displacement
    TimeIntegrationSolver solver;

    // manages the multi threading
    ExecutorService executorService;


    /**
     *
     * @param spatialDiscretization
     *        object to get matrices
     * @param accelerationProvider
     *        object to get accelerations from Sensors
     */
    public TimeIntegration(SpatialDiscretization spatialDiscretization, AccelerationProvider accelerationProvider) {
        this.spatialDiscretization = spatialDiscretization;
        this.accelerationProvider = accelerationProvider;
    }


    /**
     * This method is called from the Simulation class to prepare everything for simulation
     */
    public void prepareSimulation(){

        //initial condition for the velocity.
        xDot = new DenseMatrix64F(spatialDiscretization.getNumberOfDOF(),1);
        //This is just temporarily. In future this should choosen in the right way
        xDot.zero();

        //fixed step size
        delta_t = 0.015;

        //give the class the time step
        //USE timestep between FRAMES


        //if modal analysis is activated we can diagonalize the matrices
        if(PreferenceReader.useModalAnalysis()) {
            spatialDiscretization.calculateModalAnalysisMatrices();
        }

        //stores the numerical scheme
        //TODO: why do we instantiate xDot here if it is used only in the solver class?
        solver = new Newmark(spatialDiscretization, accelerationProvider, xDot,delta_t);
        //solver = new Euler(spatialDiscretization, accelerationProvider, xDot);
        //solver = new Static(spatialDiscretization, accelerationProvider, xDot,delta_t);


        accelerationProvider.setActive();
        accelerationProvider.initTime(2*delta_t*1e9);
        //for the parallel thread
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

        //variable which can stop the simulation
        //it will be set to false if the calculation of the displacements takes longer than than
        boolean isRunning;
        double loadVectorScaling = PreferenceReader.getLoadVectorScaling();
        DenseMatrix64F loadVector;

        public SimulationStep execute() {
            isRunning = true;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //reset time
                    t = 0;

                    double[] currExcitation = accelerationProvider.getAcceleration(globalTime);

                   // for(int i=0; i<currExcitation.length; i++){
                   //     currExcitation[i] =0;
                   // }
                    if(PreferenceReader.useModalAnalysis()) {
                        //update loadVector
                        spatialDiscretization.updateLoadVectorModalAnalysis(currExcitation);
                        //get the loadVector for the whole calculation
                        loadVector = spatialDiscretization.getRedLoadVectorModalAnalysis().copy();
                    }
                    else {
                        //update loadVector
                        spatialDiscretization.updateLoadVector(currExcitation);
                        //get the loadVector for the whole calculation
                        loadVector = spatialDiscretization.getLoadVector().copy();
                    }

                    CommonOps.scale(loadVectorScaling, loadVector);
                    solver.setFLoad(loadVector);
                    Log.i("Newmark Load:", ""+loadVector.toString() );

                    // TODO MALTE DEBUG
                    //solver.nextStep(t, delta_t);

                    long firstTime = System.nanoTime();
                    //this loop performs the calculation
                    //it calculates one frame then it stops
                    while(t < 0.03-0.000001 && isRunning) {
                        //calculate new displacement
                        solver.nextStep(t, delta_t);
                        //add ground movement for recording
                        solver.setGroundDisplacement(delta_t, currExcitation);
                        t += delta_t;

                    }

                    //for the sensor team the global time since begining
                    globalTime += (double)3e-2;

                    //for checking the calculation time
                    long secondTime = System.nanoTime();
                  //  Log.e("Timestamp",""+(secondTime-firstTime));

                    if(PreferenceReader.useModalAnalysis()){
                        //update the displacement in the node variables using modal analysis
                        spatialDiscretization.superimposeModalAnalysisSolutions(solver.getX(), solver.getGroundDisplacement());
                    }
                    else {
                        //update the displacement in the node variables
                        Log.i("Newmark Disp:", ""+solver.getX().toString());
                        spatialDiscretization.updateDisplacementsOfStructure(solver.getX(), solver.getGroundDisplacement());
                    }
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
