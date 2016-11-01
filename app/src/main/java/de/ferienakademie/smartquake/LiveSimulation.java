// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.ferienakademie.smartquake;

import android.util.Log;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;


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

