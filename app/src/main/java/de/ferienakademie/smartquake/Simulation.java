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
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;


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
