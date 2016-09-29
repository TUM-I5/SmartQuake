package de.ferienakademie.smartquake;

import android.util.Log;

import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Created by David Schneller on 29.09.2016.
 */
public class LiveSimulation extends Simulation {
    private int slowStateCount = 0;

    SpatialDiscretization spatialDiscretization;
    TimeIntegration kernel2;
    TimeIntegration.SimulationStep currentStep;

    public LiveSimulation(SpatialDiscretization spatialDiscretization, TimeIntegration kernel2, CanvasView view) {
        super(view);
        this.spatialDiscretization = spatialDiscretization;
        this.kernel2 = kernel2;
    }

    @Override
    protected void startup() {
        kernel2.prepareSimulation();
    }

    @Override
    protected void updateTick() {
        currentStep = kernel2.performSimulationStep();
    }

    @Override
    protected void drawTick() {
        if (currentStep.isRunning()) {
            Log.e("Simulation", "Kernel2 can not catch up the gui");

            slowStateCount++;

            // If the last speed state was normal and now we're slow, notify the listener
            if (simulationProgressListener != null && state == SimulationState.RUNNING_NORMAL && slowStateCount > 5) {
                state = SimulationState.RUNNING_SLOW;
                simulationProgressListener.onSimulationStateChanged(state);
            }
            currentStep.stop();
        }
    }

    @Override
    protected void shutdown() {

    }

    @Override
    protected Structure getStructure() {
        return spatialDiscretization.getStructure();
    }
}

