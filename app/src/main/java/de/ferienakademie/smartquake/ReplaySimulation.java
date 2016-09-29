package de.ferienakademie.smartquake;

import de.ferienakademie.smartquake.activity.SimulationActivity;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

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
