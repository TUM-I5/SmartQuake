package de.ferienakademie.smartquake.kernel1;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * Created by alex on 22.09.16.
 */
public class Kernel1 {

    Structure structure;
    CanvasView view;
    AccelerationProvider accelerationProvider;

    public Kernel1(Structure structure, CanvasView view, AccelerationProvider accelerationProvider) {
        this.structure = structure;
        this.view = view;
        this.accelerationProvider = accelerationProvider;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public CanvasView getView() {
        return view;
    }

    public void setView(CanvasView view) {
        this.view = view;
    }

    public AccelerationProvider getAccelerationProvider() {
        return accelerationProvider;
    }

    public void setAccelerationProvider(AccelerationProvider accelerationProvider) {
        this.accelerationProvider = accelerationProvider;
    }

    public void updateStructure(DenseMatrix64F displacementVector) {
        //TODO: update nodes and beams from displacementVector
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            node.setX(displacementVector.get(3*i, 0));
            node.setY(displacementVector.get(3*i+1, 0));
        }
        view.drawStructure(structure);

    }
}
