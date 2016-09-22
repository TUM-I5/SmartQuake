package de.ferienakademie.smartquake.view;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by yuriy on 22/09/16.
 */
public class DrawHelper {

    public static List<Node> snapNodes = new ArrayList<>();
    public static List<Beam> snapBeams = new ArrayList<>();

    public static void drawStructure(Structure structure, CanvasView view) {
        snapShot(structure.getNodes(), structure.getBeams());
        view.isBeingDrawn = true;
        view.postInvalidate();
    }

    private static void snapShot(List<Node> nodes, List<Beam> beams) {
        snapBeams.clear();
        snapNodes.clear();
        snapBeams.addAll(beams);
        snapNodes.addAll(nodes);
    }

}
