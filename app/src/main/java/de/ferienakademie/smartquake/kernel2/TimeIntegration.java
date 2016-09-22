package de.ferienakademie.smartquake.kernel2;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.kernel1.Kernel1;

/**
 * Created by Felix Wechsler on 21/09/16.
 */
public class TimeIntegration {

    Kernel1 kernel1;
    boolean isRunning;

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
        xDot.add(3,0,20);
        xDot.add(1,0,40);
        xDot.add(6,0,-20);
        xDot.add(10,0,-10);
        xDot.add(4,0,-10);
        xDot.add(7,0,-80);
        xDot.add(12,0,150);
        xDot.add(13,0,100);
        xDot.zero();
        //xDotDot must be calculated by the external load forces and the differnetial equation

        //THIS IS JUST A WORKAROUND/MINIMAL EXAMPLE
        xDotDot = new DenseMatrix64F(kernel1.getNumDOF(),1);
        xDotDot.zero();

        //only for fixed stepsize
        delta_t = 0.0000001;
    }

    public void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {


                prepareSimulation();
                while(isRunning) {
                    //calculate new position
                    solver.nextStep(kernel1.getDisplacementVector(), xDot, xDotDot,t, delta_t);

                    acceleration=kernel1.getAccelerationProvider().getAcceleration();
                    for(int j=0; j<kernel1.getNumDOF(); j+=3){
                        xDotDot.add(j,0, 1*acceleration[0]-5*xDot.get(j,0)-10*kernel1.getDisplacementVector().get(j, 0));
                        xDotDot.add(j+1,0, 1*acceleration[1]-5*xDot.get(j+1,0)-10*kernel1.getDisplacementVector().get(j+1, 0));
                        //xDotDot.add(j,0,1*acceleration[0]-0.1*xDot.get(j+1,0));
                    }
                    kernel1.updateStructure(kernel1.getDisplacementVector());
                    t += delta_t;
                }

            }
        }).start();
    }

    public void stop() {
        isRunning = false;
    }

}
