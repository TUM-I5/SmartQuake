package de.ferienakademie.smartquake;

import android.util.Log;

import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Class that wires everything together.
 */
public class Simulation {

    SpatialDiscretization spatialDiscretization;
    TimeIntegration kernel2;
    CanvasView view;
    SimulationProgressListener listener;
    boolean isRunning;

    public Simulation(SpatialDiscretization spatialDiscretization, TimeIntegration kernel2, CanvasView view) {
        this.spatialDiscretization = spatialDiscretization;
        this.kernel2 = kernel2;
        this.view = view;
    }

    public void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                kernel2.prepareSimulation();
                TimeIntegration.SimulationStep currentStep;
                while(true) {
                    if (!isRunning) {
                        break;
                    }
                    currentStep = kernel2.performSimulationStep();
                    try {
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
        isRunning = false;
    }

    public void setListener(SimulationProgressListener listener) {
        this.listener = listener;
    }

    public interface SimulationProgressListener {

        /**
         * Is called after simulation finishes. Is called from background thread.
         */
        void onSimulationFinished();
    }

}
