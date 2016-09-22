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

    private CanvasView canvasView;
    private Structure structure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (CanvasView) findViewById(R.id.shape);
        DrawHelper.clearCanvas(canvasView);
        structure = new Structure();

        canvasView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                Toast.makeText(CreateActivity.this, "" + event.getX() + " " + event.getY(), Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.w("LOLOLOL", event.toString());
        Log.w("POINTER_COUNT", ""+event.getPointerCount());

        if (event.getPointerCount() == 2) {

            if (event.getAction() != MotionEvent.ACTION_MOVE && event.getAction() != MotionEvent.ACTION_POINTER_UP
                    && event.getAction() != MotionEvent.ACTION_UP) {
                adding = true;
                Node node1 = new Node(event.getX(0), event.getY(0) - 220);
                Node node2 = new Node(event.getX(1), event.getY(1) - 220);

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

                Node node1 = nodes.get(nodes.size() - 2);
                Node node2 = nodes.get(nodes.size() - 1);
                node1.setX(event.getX(0));
                node1.setY(event.getY(0) - 220);

                node2.setX(event.getX(1));
                node2.setY(event.getY(1) - 220);

                Beam beam = new Beam(node1, node2);
                node1.addBeam(beam);
                node2.addBeam(beam);

                structure.getBeams().set(structure.getBeams().size() - 1, beam);

                for (int i = 0; i < nodes.size() - 2; i++) {

                    Node node = nodes.get(i);

                    if (nearNodes(node1, node)) {
                        node1.setX(node.getX());
                        node1.setY(node.getY());
                    }

                    if (nearNodes(node2, node)) {
                        node2.setX(node.getX());
                        node2.setY(node.getY());
                    }

                }
            }

            DrawHelper.clearCanvas(canvasView);

            DrawHelper.drawStructure(structure, canvasView);

        }

        return super.onTouchEvent(event);
    }

    private static boolean nearNodes(Node node1, Node node2) {
        return (Math.abs(node1.getX() - node2.getX()) <= DELTA && Math.abs(node1.getY() - node2.getY()) <= DELTA);
    }
}
