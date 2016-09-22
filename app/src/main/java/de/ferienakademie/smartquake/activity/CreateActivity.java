package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Created by yuriy on 22/09/16.
 */
public class CreateActivity extends Activity {

    private static final int DELTA = 80;
    private static boolean adding = false;
    private Node node1 = null;
    private Node node2 = null;

    private CanvasView canvasView;
    private Structure structure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (CanvasView) findViewById(R.id.shape);
        DrawHelper.clearCanvas(canvasView);
        structure = new Structure();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() == 2) {

            if (event.getAction() != MotionEvent.ACTION_MOVE && event.getAction() != MotionEvent.ACTION_POINTER_UP
                    && event.getAction() != MotionEvent.ACTION_UP) {
                adding = true;

                node1 = new Node(event.getX(0), event.getY(0) - 220);
                node2 = new Node(event.getX(1), event.getY(1) - 220);

                structure.addNode(node1);
                structure.addNode(node2);
                Beam beam = new Beam(node1, node2);
                structure.addBeam(beam);

                node1.addBeam(beam);
                node2.addBeam(beam);
                adding = false;
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE && !adding) {

                List<Node> nodes = structure.getNodes();

                node1 = nodes.get(nodes.size() - 2);
                node2 = nodes.get(nodes.size() - 1);
                node1.setCurrX(event.getX(0));
                node1.setCurrY(event.getY(0) - 220);

                node2.setCurrX(event.getX(1));
                node2.setCurrY(event.getY(1) - 220);

                Beam beam = new Beam(node1, node2);
                node1.addBeam(beam);
                node2.addBeam(beam);

                structure.getBeams().set(structure.getBeams().size() - 1, beam);

                boolean attach1 = false, attach2 = false;
                double minDist1 = DELTA, minDist2 = DELTA;
                Node node1Attach = null, node2Attach = null;

                for (int i = 0; i < nodes.size() - 2; i++) {

                    Node node = nodes.get(i);

                    if (distNodes(node1, node) <= minDist1) {
                        minDist1 = distNodes(node1, node);
                        attach1 = true;
                        node1Attach = node;
                    }

                    if (distNodes(node2, node) <= minDist2) {
                        minDist2 = distNodes(node2, node);
                        attach2 = true;
                        node2Attach = node;
                    }

                }

                if (attach1) {
                    node1.setCurrX(node1Attach.getCurrX());
                    node1.setCurrY(node1Attach.getCurrY());
                }

                if (attach2) {
                    node2.setCurrX(node2Attach.getCurrX());
                    node2.setCurrY(node2Attach.getCurrY());
                }

            }

            if (event.getAction() == MotionEvent.ACTION_POINTER_UP ||
                    event.getAction() == MotionEvent.ACTION_UP) {
                List<Node> nodes = structure.getNodes();
                for (int i = 0; i < nodes.size() - 2; i++) {
                    if (nodes.get(i).equals(node1)) nodes.remove(node1);
                    if (nodes.get(i).equals(node2)) nodes.remove(node2);
                }
            }

            DrawHelper.clearCanvas(canvasView);

            DrawHelper.drawStructure(structure, canvasView);

        }

        return super.onTouchEvent(event);
    }

    private static double distNodes(Node node1, Node node2) {
        return Math.abs(node1.getCurrX() - node2.getCurrX()) + Math.abs(node1.getCurrY() - node2.getCurrY());
    }
}
