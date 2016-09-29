package de.ferienakademie.smartquake;

import android.util.Log;

import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Class that wires everything together.
 * TODO on first run an event is thrown for slow simulation.
 */
public abstract class Simulation {



    public enum SimulationState {
        RUNNING_NORMAL,
        RUNNING_SLOW,
        STOPPED
    }

    protected SimulationState state;


    CanvasView view;
    SimulationProgressListener simulationProgressListener;
    StructureUpdateListener structureUpdateListener;

    public boolean isRunning() {
        return state != SimulationState.STOPPED;
    }



    public Simulation(CanvasView view) {
        this.view = view;
        state = SimulationState.STOPPED;
    }

    protected  abstract  void startup();
    protected  abstract  void updateTick();
    protected  abstract  void drawTick();
    protected  abstract  void shutdown();
    protected abstract Structure getStructure();

    public void start() {
        state = SimulationState.RUNNING_NORMAL;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startup();
                while(true) {
                    if (!isRunning()) {
                        break;
                    }
                    updateTick();
                    if (!isRunning()) {
                        break;
                    }
                    try {
                        //TODO: think about dynamic frame rate
                        Thread.sleep(30);
                    } catch (InterruptedException ex) {
                        Log.e("Simulation", ex.getMessage());
                        continue;
                    }

                    while(view.isBeingDrawn) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Log.e("Simulation", ex.getMessage());
                        }
                    }
                    drawTick();
                    if (structureUpdateListener != null) {
                        structureUpdateListener.onStructureUpdate(getStructure());
                    }
                }
                shutdown();
                if (simulationProgressListener != null) {
                    simulationProgressListener.onSimulationFinished();
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

    public void setSimulationProgressListener(SimulationProgressListener simulationProgressListener) {
        this.simulationProgressListener = simulationProgressListener;
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

    public void setStructureUpdateListener(StructureUpdateListener structureUpdateListener) {
        this.structureUpdateListener = structureUpdateListener;
    }

    public interface StructureUpdateListener {
        void onStructureUpdate(Structure s);
    }


}
