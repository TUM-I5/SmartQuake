package de.ferienakademie.smartquake;

import android.util.Log;

import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Class that wires everything together.
 * TODO on first run an event is thrown for slow simulation.
 */
public class Simulation {

    private int slowStateCount = 0;

    public enum SimulationState {
        RUNNING_NORMAL,
        RUNNING_SLOW,
        STOPPED
    }

    private SimulationState state;

    SpatialDiscretization spatialDiscretization;
    TimeIntegration kernel2;
    CanvasView view;
    SimulationProgressListener listener;
    public boolean isRunning() {
        return state != SimulationState.STOPPED;
    }



    public Simulation(SpatialDiscretization spatialDiscretization, TimeIntegration kernel2, CanvasView view) {
        this.spatialDiscretization = spatialDiscretization;
        this.kernel2 = kernel2;
        this.view = view;
        state = SimulationState.STOPPED;
    }

    public void start() {
        state = SimulationState.RUNNING_NORMAL;
        new Thread(new Runnable() {
            @Override
            public void run() {
                kernel2.prepareSimulation();
                TimeIntegration.SimulationStep currentStep;
                while(true) {
                    if (!isRunning()) {
                        break;
                    }
                    currentStep = kernel2.performSimulationStep();
                    try {
                        //TODO: think about dynamic frame rate
                        Thread.sleep(30);
                    } catch (InterruptedException ex) {
                        Log.e("Simulation", ex.getMessage());
                        continue;
                    }
                    while(view.isBeingDrawn) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ex) {
                            Log.e("Simulation", ex.getMessage());
                        }
                    }
                    if (currentStep.isRunning()) {
                        Log.e("Simulation", "Kernel2 can not catch up the gui");

                        slowStateCount++;

                        // If the last speed state was normal and now we're slow, notify the listener
                        if (listener != null && state == SimulationState.RUNNING_NORMAL && slowStateCount > 5) {
                            state = SimulationState.RUNNING_SLOW;
                            listener.onSimulationStateChanged(state);
                        }
                        currentStep.stop();
                    }
                    DrawHelper.drawStructure(spatialDiscretization.getStructure(), view);
                }
                if (listener != null) {
                    listener.onSimulationFinished();
                }
            }

        }).start();
    }

    /**
     * Stop simulation. This will also kill all background threads.
     */
    public void stop() {
        state = SimulationState.STOPPED;
    }

    public void setListener(SimulationProgressListener listener) {
        this.listener = listener;
    }

    public interface SimulationProgressListener {

        /**
         * Is called after simulation finishes. Is called from background thread.
         */
        void onSimulationFinished();

        /**
         * Is called when the simulation speed has changed
         */
        void onSimulationStateChanged(SimulationState newSpeedState);
    }

}
