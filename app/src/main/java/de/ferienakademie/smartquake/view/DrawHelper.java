package de.ferienakademie.smartquake.view;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

public class DrawHelper {

    public static List<Node> getSnapNodes() {
        return snapNodes;
    }

    public static List<Beam> getSnapBeams() {
        return snapBeams;
    }

    public static double[] getBoundingBox() {
        return boundingBox;
    }

    // TODO: change accessibility modifiers?
    private static List<Node> snapNodes = new ArrayList<>();
    private static List<Beam> snapBeams = new ArrayList<>();
    private static double[] boundingBox = new double[2];

    public static void drawStructure(Structure structure, CanvasView view) {
        snapNodes = structure.getNodes();
        snapBeams = structure.getBeams();
        boundingBox = structure.getModelSize();
        view.isBeingDrawn = true;
        view.postInvalidate();
    }

    public static void clearCanvas(CanvasView view) {
        snapBeams.clear();
        snapNodes.clear();
        view.postInvalidate();
    }

}
