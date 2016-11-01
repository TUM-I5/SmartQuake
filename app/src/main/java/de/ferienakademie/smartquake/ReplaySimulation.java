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

import de.ferienakademie.smartquake.activity.SimulationActivity;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;


/**
 * Created by David Schneller on 29.09.2016.
 */
public class ReplaySimulation extends Simulation {
    Structure structure;
    int i;
    int number_timeSteps;
    SimulationActivity activity;

    public ReplaySimulation(Structure structure, SimulationActivity activity, CanvasView view)
    {
        super(view);
        this.structure = structure;
        this.activity = activity;
    }

    @Override
    protected void startup() {
        number_timeSteps = structure.getNodes().get(0).getLengthOfHistory();

        for (Beam beam : structure.getBeams())
        {
            beam.resetBeam();
        }
        i = 0;
    }

    @Override
    protected void updateTick() {

        if (i >= number_timeSteps)
        {
            stop();
            return;
        }

        //loop over all nodes to update positions
        structure.recallDisplacementOfStep(i);

        ++i;
    }

    @Override
    protected void drawTick() {
        //draw frame, later
//        DrawHelper.drawStructure(structure, view);

        double percentage = ((double)i/number_timeSteps)*100;

        activity.onNewReplayPercent(percentage);
    }

    @Override
    protected void shutdown() {
        activity.onNewReplayPercent(100);
    }

    @Override
    protected Structure getStructure() {
        return structure;
    }
}
