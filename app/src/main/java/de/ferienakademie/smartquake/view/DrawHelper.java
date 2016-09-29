package de.ferienakademie.smartquake.view;

        import android.view.View;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Vector;

        import de.ferienakademie.smartquake.model.Beam;
        import de.ferienakademie.smartquake.model.Node;
        import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by yuriy on 22/09/16.
 */
public class DrawHelper {

    public static List<Node> snapNodes = new Vector<>();
    public static List<Beam> snapBeams = new Vector<>();
    public static double[] boundingBox = new double[4];

    public static synchronized void drawStructure(Structure structure, View view1) {
        snapShot(structure.getNodes(), structure.getBeams());
        boundingBox = structure.getBoundingBox();
        if (view1 instanceof CanvasView) {
            CanvasView view = (CanvasView)view1;
            view.isBeingDrawn = true;
        }
        if (view1 instanceof DrawCanvasView) {
            DrawCanvasView view = (DrawCanvasView)view1;
            view.isBeingDrawn = true;
        }

        view1.postInvalidate();
    }

    public synchronized static void clearCanvas(View view1) {
        snapBeams.clear();
        snapNodes.clear();
        if (view1 instanceof CanvasView) {
            CanvasView view = (CanvasView)view1;
            view.isBeingDrawn = true;
        }
        if (view1 instanceof DrawCanvasView) {
            DrawCanvasView view = (DrawCanvasView)view1;
            view.isBeingDrawn = true;
        }
        view1.postInvalidate();
    }

    private synchronized static void snapShot(List<Node> nodes, List<Beam> beams) {
        snapBeams.clear();
        snapNodes.clear();
        snapBeams.addAll(beams);
        snapNodes.addAll(nodes);
    }

}
