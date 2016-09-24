package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.jar.Manifest;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.excitation.StructureIO;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.DrawCanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Created by yuriy on 22/09/16.
 */
public class CreateActivity extends AppCompatActivity {
    private static double DELTA = 90;
    private static boolean adding = false;
    private Node node1 = null;
    private Node node2 = null;
    private Node chosenNode = null;


    private GestureDetectorCompat mGestureDetector;
    private LongPressListener longPressListener;

    private DrawCanvasView canvasView;
    private Structure structure;

    private ActionBar actionBar;

    private double width, height;

    private int yOffset = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (DrawCanvasView) findViewById(R.id.crtCanvasView);
        DrawHelper.clearCanvas(canvasView);
        structure = new Structure();
        longPressListener = new LongPressListener();
        mGestureDetector = new GestureDetectorCompat(this, longPressListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    width = canvasView.getWidth();
                    height = canvasView.getHeight();
                }
            });
        }

        yOffset = actionBar.getHeight() + 40;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings: //TODO setteings activity
                /***
                 *Add here code for setting activity
                 * startActivity(new Intent(this, SettingsActivity.class));
                 return true;
                 */
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.clear_canvas:
                structure.clearAll();
                DrawHelper.drawStructure(structure, canvasView);
                return true;
            case R.id.save_canvas:
                serialize();
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public void serialize() {
        List<Node> nodes = structure.getNodes();

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getBeams().size() == 0) nodes.remove(i);
        }

        for (Node node : nodes) {
            transformToMeters(node);
        }

//        File file = new File(getFilesDir(), "structure.json");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput("structure.json", Context.MODE_PRIVATE);
            Log.w("FILE", fileOutputStream.toString());
            StructureIO.writeStructure(fileOutputStream, structure);
            fileOutputStream.close();
            Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("CreateActivity.class", "File not found");
        }
    }

    public void transformToMeters(Node node) {
        double x = node.getCurrentX();
        double y = node.getCurrentY();

        double[] modelSize = DrawHelper.boundingBox;

        double displayScaling = Math.min(0.75 * width / modelSize[0], 0.75 * height / modelSize[1]);

        double xOffset = 0.5 * (width - modelSize[0] * displayScaling);
        double yOffset = height - modelSize[1] * displayScaling;

        x = (x - xOffset) / (displayScaling);
        y = (y - yOffset) / (displayScaling);

        node.setCurrentX(x);
        node.setCurrentY(y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);

        if (event.getPointerCount() == 2) {
            if (event.getAction() == 261) {
                adding = true;

                // in pixels
                node1 = new Node(event.getX(0), (event.getY(0) - yOffset));
                node2 = new Node(event.getX(1), (event.getY(1) - yOffset));

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
                node1.setCurrentX(event.getX(0));
                node1.setCurrentY(event.getY(0) - yOffset);

                node2.setCurrentX(event.getX(1));
                node2.setCurrentY(event.getY(1) - yOffset);

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
                        for (int j = i + 1; j < nodes.size(); j++) {
                            if (nodes.get(j).equals(node1)) nodes.remove(j);
                        }
                    }
                    if (nodes.get(i).equals(node2)) {
                        connectedTwoNode = nodes.get(i);
                        nodes.get(i).addBeam(currBeam);
                        for (int j = i + 1; j < nodes.size(); j++) {
                            if (nodes.get(j).equals(node2)) nodes.remove(j);
                        }
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
            double y = event.getY(0) - yOffset;

            Node tempNode = new Node(x, y);

            x = tempNode.getCurrentX();
            y = tempNode.getCurrentY();

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
                    chosenNode.setCurrentX(x);
                    chosenNode.setCurrentY(y);
                    node1 = chosenNode;
                    node2 = null;
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

            if (node1 != null && node != node1 && distNodes(node1, node) <= minDist1) {
                minDist1 = distNodes(node1, node);
                attach1 = true;
                node1Attach = node;
            }

            if (node2 != null && node != node2 && distNodes(node2, node) <= minDist2) {
                minDist2 = distNodes(node2, node);
                attach2 = true;
                node2Attach = node;
            }

        }

        if (attach1) {
            node1.setCurrentX(node1Attach.getCurrentX());
            node1.setCurrentY(node1Attach.getCurrentY());
        }

        if (attach2) {
            node2.setCurrentX(node2Attach.getCurrentX());
            node2.setCurrentY(node2Attach.getCurrentY());
        }

        if (node1 != null && node1.getCurrentY() >= height - DELTA / 2) {
            node1.setCurrentY(height);
        }

        if (node2 != null && node2.getCurrentY() >= height - DELTA / 2) {
            node2.setCurrentY(height);
        }

    }

    private static double distNodes(Node node1, Node node2) {
        return Math.abs(node1.getCurrentX() - node2.getCurrentX()) + Math.abs(node1.getCurrentY() - node2.getCurrentY());
    }


    public void deleteBeam(double x, double y) {

        List<Beam> beams = structure.getBeams();

        Beam deleteBeam = null;

        double minDist = DELTA;

        for (Beam beam : beams) {

            Node node1 = beam.getStartNode();
            Node node2 = beam.getEndNode();

            double x1 = node1.getCurrentX();
            double x2 = node2.getCurrentX();
            double y1 = node1.getCurrentY();
            double y2 = node2.getCurrentY();

            x2 = x2 - x1;
            x1 = x - x1;
            y2 = y2 - y1;
            y1 = y - y1;

            double cosAlfa = (x1*x2+y1*y2)/(Math.sqrt(y1*y1+x1*x1)*Math.sqrt(y2*y2+x2*x2));
            double sinAlfa = Math.sqrt(1 - cosAlfa*cosAlfa);

            double dist = sinAlfa * Math.sqrt(y1*y1+x1*x1);

            if (dist <= minDist) {

                sinAlfa = Math.abs(y2)/(Math.sqrt(y2*y2+x2*x2));
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
        return cosAlfa*node.getCurrentX() + sinAlfa*node.getCurrentY();
    }

    public class LongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            Node n = new Node(e.getX(), e.getY() - yOffset);

            deleteBeam(n.getCurrentX(), n.getCurrentY());
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

    }

}
