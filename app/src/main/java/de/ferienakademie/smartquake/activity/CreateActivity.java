package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
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
public class CreateActivity extends AppCompatActivity {

    private static final int DELTA = 80;
    private static boolean adding = false;
    private Node node1 = null;
    private Node node2 = null;
    private Node chosenNode = null;

    private GestureDetectorCompat mGestureDetector;
    private LongPressListener longPressListener;

    private CanvasView canvasView;
    private Structure structure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (CanvasView) findViewById(R.id.crtCanvasView);
        DrawHelper.clearCanvas(canvasView);
        structure = new Structure();
        longPressListener = new LongPressListener();
        mGestureDetector = new GestureDetectorCompat(this, longPressListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);

        if (event.getPointerCount() == 2) {
            if (event.getAction() == 261) {
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
                node1.clearBeams();
                node2.clearBeams();
                node1.addBeam(beam);
                node2.addBeam(beam);

                structure.getBeams().set(structure.getBeams().size() - 1, beam);

                magneticConnect();

            }

            if (event.getAction() == MotionEvent.ACTION_POINTER_UP
                    || event.getAction() == 262) {
                List<Node> nodes = structure.getNodes();
                List<Beam> beams = structure.getBeams();
                Node connectedOneNode = null;
                Node connectedTwoNode = null;
                Beam currBeam = structure.getBeams().get(structure.getBeams().size() - 1);
                for (int i = 0; i < nodes.size() - 2; i++) {
                    if (nodes.get(i).equals(node1)) {
                        nodes.get(i).addBeam(currBeam);
                        connectedOneNode = nodes.get(i);
                        nodes.remove(nodes.size() - 2);
                    }
                    if (nodes.get(i).equals(node2)) {
                        connectedTwoNode = nodes.get(i);
                        nodes.get(i).addBeam(currBeam);
                        nodes.remove(nodes.size() - 1);
                    }
                }
                for (Beam beam : beams) {
                    if (beam.getStartNode().equals(connectedOneNode))
                        beam.setStartNode(connectedOneNode);
                    if (beam.getEndNode().equals(connectedOneNode))
                        beam.setEndNode(connectedOneNode);

                    if (beam.getStartNode().equals(connectedTwoNode))
                        beam.setStartNode(connectedTwoNode);
                    if (beam.getEndNode().equals(connectedTwoNode))
                        beam.setEndNode(connectedTwoNode);
                }

            }
        }

        if (event.getPointerCount() == 1) {

            List<Node> nodes = structure.getNodes();

            double x = event.getX(0);
            double y = event.getY(0) - 220;

            double mindist = DELTA;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                for (Node node : nodes) {
                    if (distNodes(node, new Node(x, y)) <= mindist) {
                        mindist = distNodes(node, new Node(x, y));
                        chosenNode = node;
                    }
                }
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (chosenNode != null) {
                    chosenNode.setCurrX(x);
                    chosenNode.setCurrY(y);
                }
                magneticConnect();
            }

            if (event.getAction() == MotionEvent.ACTION_UP && chosenNode != null) {

                List<Beam> beamList = chosenNode.getBeams();

                Node changeToThisNode = null;

                boolean removed = false;

                for (Node node : nodes) {
                    if (node.equals(chosenNode) && node != chosenNode) {
                        changeToThisNode = node;
                        break;
                    }
                }

                if (changeToThisNode != null) {
                    for (Beam beam : beamList) {
                        if (beam.getStartNode().equals(changeToThisNode) && changeToThisNode != beam.getStartNode()) {
                            Node startNode = beam.getStartNode();
                            if (!removed) {
                                for (int i = 0; i < nodes.size(); i++) {
                                    if (startNode == nodes.get(i)) {
                                        nodes.remove(i);
                                        removed = true;
                                        break;
                                    }
                                }
                            }
                            beam.setStartNode(changeToThisNode);
                            changeToThisNode.addBeam(beam);
                        }
                        if (beam.getEndNode().equals(changeToThisNode) && changeToThisNode != beam.getEndNode()) {
                            Node endNode = beam.getEndNode();
                            if (!removed) {
                                for (int i = 0; i < nodes.size(); i++) {
                                    if (endNode == nodes.get(i)) {
                                        nodes.remove(i);
                                        removed = true;
                                        break;
                                    }
                                }
                            }
                            beam.setEndNode(changeToThisNode);
                            changeToThisNode.addBeam(beam);
                        }
                    }
                }

                chosenNode = null;
            }

        }

        DrawHelper.clearCanvas(canvasView);
        DrawHelper.drawStructure(structure, canvasView);

        return super.onTouchEvent(event);
    }

    private void magneticConnect() {
        List<Node> nodes = structure.getNodes();

        boolean attach1 = false, attach2 = false;
        double minDist1 = DELTA, minDist2 = DELTA;
        Node node1Attach = null, node2Attach = null;

        for (int i = 0; i < nodes.size(); i++) {

            Node node = nodes.get(i);

            if (node != node1 && distNodes(node1, node) <= minDist1) {
                minDist1 = distNodes(node1, node);
                attach1 = true;
                node1Attach = node;
            }

            if (node != node2 && distNodes(node2, node) <= minDist2) {
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

    private static double distNodes(Node node1, Node node2) {
        return Math.abs(node1.getCurrX() - node2.getCurrX()) + Math.abs(node1.getCurrY() - node2.getCurrY());
    }


    public void deleteBeam(double x, double y) {

        List<Beam> beams = structure.getBeams();

        Beam deleteBeam = null;

        double minDist = DELTA;

        for (Beam beam : beams) {

            Node node1 = beam.getStartNode();
            Node node2 = beam.getEndNode();

            double x1 = node1.getCurrX();
            double x2 = node2.getCurrX();
            double y1 = node1.getCurrY();
            double y2 = node2.getCurrY();

            x2 = x2 - x1;
            x1 = x - x1;
            y2 = y2 - y1;
            y1 = y - y1;

            double cosAlfa = (x1*x2+y1*y2)/(Math.sqrt(y1*y1+x1*x1)*Math.sqrt(y2*y2+x2*x2));
            double sinAlfa = Math.sqrt(1 - cosAlfa*cosAlfa);

            double dist = sinAlfa * Math.sqrt(y1*y1+x1*x1);

            if (dist <= minDist) {

                sinAlfa = y2/(Math.sqrt(y2*y2+x2*x2));
                cosAlfa = Math.sqrt(1 - sinAlfa*sinAlfa);

                x1 = rotateX(node1, cosAlfa, sinAlfa);
                x2 = rotateX(node2, cosAlfa, sinAlfa);

                x = rotateX(new Node(x, y), cosAlfa, sinAlfa);

                if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)) {
                    minDist = dist;
                    deleteBeam = beam;
                }
            }
        }

        for (int i = 0; i < beams.size(); i++) {
            if (beams.get(i) == deleteBeam) {
                beams.remove(i);
                Node startNode = deleteBeam.getStartNode();
                Node endNode = deleteBeam.getEndNode();
                // delete reference of the deleted beam on start and end nodes

                startNode.getBeams().remove(deleteBeam);
                endNode.getBeams().remove(deleteBeam);

                if (startNode.getBeams().isEmpty())
                    structure.getNodes().remove(startNode);

                if (endNode.getBeams().isEmpty())
                    structure.getNodes().remove(endNode);

                break;
            }
        }

    }

    private static double rotateX(Node node, double cosAlfa, double sinAlfa) {
        return cosAlfa*node.getCurrX() + sinAlfa*node.getCurrY();
    }

    public class LongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Log.w("LONG PRESS", "TRUE");
            deleteBeam(e.getX(), e.getY() - 220);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

    }

}
