package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class CreateActivity extends AppCompatActivity{

    private static final int DELTA = 80;
    private static boolean adding = false;
    private Node node1 = null;
    private Node node2 = null;
    private Node chosenNode = null;

    private CanvasView canvasView;
    private Structure structure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (CanvasView) findViewById(R.id.crtCanvasView);
        DrawHelper.clearCanvas(canvasView);
        structure = new Structure();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

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

            float x = event.getX(0);
            float y = event.getY(0) - 220;

            double mindist = DELTA;
/*
            if (event.getDownTime() >= 500) {
                chosenNode = null;
                // find the beam with the minimum distance to it
                List<Beam> beams = structure.getBeams();

                List<Beam> possibleDeleteBeams = new ArrayList<>();

                for (Beam beam : beams) {

                    Node node1 = beam.getStartNode();
                    Node node2 = beam.getEndNode();

                    double x1 = node1.getCurrX();
                    double x2 = node2.getCurrX();
                    double y1 = node1.getCurrX();
                    double y2 = node2.getCurrY();

                    double cosAlfa = (x1*x2+y1*y2)/Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));

                    //double dist = cosAlfa *

                }

            }
*/
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
}
