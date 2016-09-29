package de.ferienakademie.smartquake.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.excitation.StructureIO;
import de.ferienakademie.smartquake.fragment.NodeFragment;
import de.ferienakademie.smartquake.fragment.SaveDialogFragment;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Material;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawCanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;


public class CreateActivity extends AppCompatActivity implements SaveDialogFragment.SaveDialogListener,
        NodeFragment.NodeParametersListener {
    private static double DELTA = 100;
    private static boolean adding = false;
    private Node node1 = null;
    private Node node2 = null;
    private Node chosenNode = null;

    private double[] loadedBoundingBox;

    // stuff to detect gestures, specifically long press
    private GestureDetectorCompat mGestureDetector;
    private LongPressListener longPressListener;

    private DrawCanvasView canvasView;
    private Structure structure;

    private ActionBar actionBar;

    private double width, height;

    private int yOffset = 0;

    private static double distNodes(Node node1, Node node2) {
        return Math.abs(node1.getCurrentX() - node2.getCurrentX()) + Math.abs(node1.getCurrentY() - node2.getCurrentY());
    }

    private static double rotateX(Node node, double cosAlfa, double sinAlfa) {
        return cosAlfa * node.getCurrentX() + sinAlfa * node.getCurrentY();
    }

    // is called, when the structure is saved
    public void onNameChosen(String s) {
        serializeStructure(s);
    }

    // is called, when nodes parameters are changed
    public void onChangeNode() {
        DrawHelper.drawStructure(structure, canvasView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        canvasView = (DrawCanvasView) findViewById(R.id.crtCanvasView);
        longPressListener = new LongPressListener();
        mGestureDetector = new GestureDetectorCompat(this, longPressListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    width = canvasView.getWidth();
                    height = canvasView.getHeight();
                    if(structure==null) { // only on first launch
                        setStructureWithIntents();
                    }
                }
            });
        }

        // toolbar offset from the up of the screen
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
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.clear_canvas:
                structure.clearAll();
                DrawHelper.drawStructure(structure, canvasView);
                break;
            case R.id.save_canvas:
                if (!structure.getNodes().isEmpty() && !structure.getBeams().isEmpty()) {
                    SaveDialogFragment save = new SaveDialogFragment();
                    if(getIntent().getExtras()!=null){
                        save.setNameSuggestion(getIntent().getExtras().getString("name"));
                    }
                    save.show(getFragmentManager(), "save");
                } else {
                    Toast.makeText(CreateActivity.this, "Cannot save empty structure!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void serializeStructure(String name) {
        List<Node> nodes = structure.getNodes();
        List<Beam> allBeams = structure.getBeams();



        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            List<Beam> beams = node.getBeams();

            if (nodes.get(i).getBeams().size() == 0) {
                nodes.remove(i);
                break;
            }

            for (Beam beam : beams) {
                if (beam.getStartNode() != node && beam.getEndNode() != node) nodes.remove(i);
            }
        }

        for (Node node : nodes) {
            transformToMeters(node);
        }

        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (nodes.get(i).getBeams().isEmpty()) {
                nodes.remove(i);
            }
        }

        for (int i = 0; i < allBeams.size(); i++) {
            if (allBeams.get(i).getStartNode().equals(allBeams.get(i).getEndNode()))
                allBeams.remove(i--);
        }

        double[] boundingBox=new double[4];
        for (Node node:structure.getNodes()) {
                if (node.getInitialX() < boundingBox[0]) {
                        boundingBox[0] = node.getInitialX();
                }
                if (node.getInitialX() > boundingBox[1]) {
                    boundingBox[1] = node.getInitialX();
                }
                if (node.getInitialY() < boundingBox[2]) {
                        boundingBox[2] = node.getInitialY();
                }
                if (node.getInitialY() > boundingBox[3]) {
                    boundingBox[3] = node.getInitialY();
                }
            }
        structure.setBoundingBox(boundingBox);



        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(name + ".structure", Context.MODE_PRIVATE);
            StructureIO.writeStructure(fileOutputStream, structure);
            fileOutputStream.close();
            Toast.makeText(this, "Structure saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (FileNotFoundException e) {
            Log.e("CreateActivity.class", "File not found");
        } catch (IOException e) {
            Log.e("CreateActivity.class", "IOException");
        }
    }

    // transform pixels to meters, standard modelsize for drawing is 8x8
    public void transformToMeters(Node node) {
        double x = node.getCurrentX();
        double y = node.getCurrentY();

        double[] modelSize;
        if(loadedBoundingBox!=null){
            modelSize = new double[]{loadedBoundingBox[1]-loadedBoundingBox[0], loadedBoundingBox[3]-loadedBoundingBox[2]};
        }else{
            modelSize = new double[]{8.0, 8.0};
        }

        double displayScaling = Math.min(width / modelSize[0], height / modelSize[1]);

        double xOffset = 0.5 * (width - modelSize[0] * displayScaling);
        double yOffset = height - modelSize[1] * displayScaling;

        x = (x - xOffset) / (displayScaling);
        y = (y - yOffset) / (displayScaling);

        double temp = (height - yOffset) / (displayScaling);
        double deltaTemp = (DELTA - yOffset) / (displayScaling);

        if (y >= temp - deltaTemp / 2) y = temp;

        node.setInitialX(x);
        node.setInitialY(y);
    }


    // transform meters to pixels, standard modelsize for drawing is 8x8
    public void transformtoPixels(Node node, double modelScaling, double[] negativeMinCorrections, double[] screenCenteringOffsets) {
        double x= node.getInitialX();
        double y = node.getInitialY();

        x=((x + negativeMinCorrections[0]) * modelScaling + screenCenteringOffsets[0]);
        y=((y + negativeMinCorrections[1]) * modelScaling + screenCenteringOffsets[1]);
        node.setInitialX(x);
        node.setInitialY(y);
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
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && !adding) {

                List<Node> nodes = structure.getNodes();

                node1 = nodes.get(nodes.size() - 2);
                node2 = nodes.get(nodes.size() - 1);
                node1.setInitialX(event.getX(0));
                node1.setInitialY(event.getY(0) - yOffset);

                node2.setInitialX(event.getX(1));
                node2.setInitialY(event.getY(1) - yOffset);

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

                popupGround(currBeam.getStartNode());
                popupGround(currBeam.getEndNode());
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
                    chosenNode.setInitialX(x);
                    chosenNode.setInitialY(y);
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
                    for (int k = 0; k < beamList.size(); k++) {
                        Beam beam = beamList.get(k);
                        if (beam.getStartNode().equals(changeToThisNode) && changeToThisNode != beam.getStartNode()) {
                            Node startNode = beam.getStartNode();
                            if (!removed) {
                                for (int i = 0; i < nodes.size(); i++) {
                                    if (startNode == nodes.get(i)) {
                                        nodes.remove(i);
                                        removed = true;
                                        if (beam.getStartNode().equals(beam.getEndNode())) {
                                            boolean delete = true;
                                            for (Beam connectedBeam : beam.getStartNode().getBeams()) {
                                                if (!connectedBeam.getStartNode().equals(connectedBeam.getEndNode()))
                                                    delete = false;
                                            }
                                            for (Beam connectedBeam : beam.getEndNode().getBeams()) {
                                                if (!connectedBeam.getStartNode().equals(connectedBeam.getEndNode()))
                                                    delete = false;
                                            }
                                            if (delete) {
                                                nodes.remove(beam.getStartNode());
                                                nodes.remove(beam.getEndNode());
                                                beamList.remove(beam);
                                            }
                                        }
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
                                        if (beam.getStartNode().equals(beam.getEndNode())) { //TODO wtf? if(true)?, maybe fixed, double check pls
                                            boolean delete = true;
                                            for (Beam connectedBeam : beam.getStartNode().getBeams()) {
                                                if (!connectedBeam.getStartNode().equals(connectedBeam.getEndNode()))
                                                    delete = false;
                                            }
                                            for (Beam connectedBeam : beam.getEndNode().getBeams()) {
                                                if (!connectedBeam.getStartNode().equals(connectedBeam.getEndNode()))
                                                    delete = false;
                                            }
                                            if (delete) {
                                                nodes.remove(beam.getStartNode());
                                                nodes.remove(beam.getEndNode());
                                                beamList.remove(beam);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            beam.setEndNode(changeToThisNode);
                            changeToThisNode.addBeam(beam);
                        }
                    }
                }

                popupGround(chosenNode);

                chosenNode = null;
            }

        }

        DrawHelper.clearCanvas(canvasView);
        DrawHelper.drawStructure(structure, canvasView);

        return super.onTouchEvent(event);
    }

    // looks if while moving any nodes should be connected to the other one
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
            node1.setInitialX(node1Attach.getCurrentX());
            node1.setInitialY(node1Attach.getCurrentY());
        }

        if (attach2) {
            node2.setInitialX(node2Attach.getCurrentX());
            node2.setInitialY(node2Attach.getCurrentY());
        }

        if (node1 != null && node1.getCurrentY() >= height - DELTA / 2) {
            node1.setInitialY(height);

        }

        if (node2 != null && node2.getCurrentY() >= height - DELTA / 2) {
            node2.setInitialY(height);
        }

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

            double cosAlfa = (x1 * x2 + y1 * y2) / (Math.sqrt(y1 * y1 + x1 * x1) * Math.sqrt(y2 * y2 + x2 * x2));
            double sinAlfa = Math.sqrt(1 - cosAlfa * cosAlfa);

            double dist = sinAlfa * Math.sqrt(y1 * y1 + x1 * x1);

            if (dist <= minDist) {
                sinAlfa = Math.abs(y2) / (Math.sqrt(y2 * y2 + x2 * x2));
                cosAlfa = Math.sqrt(1 - sinAlfa * sinAlfa);

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

    public boolean setHinge(Node finger) {
        List<Node> nodes = structure.getNodes();
        double mindist = DELTA;
        Node hingeNode = null;
        for (Node node : nodes) {
            if (distNodes(node, finger) <= mindist) {
                mindist = distNodes(node, finger);
                hingeNode = node;
            }
        }
        if (hingeNode != null) {
            nodePopup(hingeNode);
            return true;
        }
        return false;
    }

    private void nodePopup(Node node) {
        NodeFragment nodeFragment = new NodeFragment();
        nodeFragment.setNode(node);
        nodeFragment.setListener(this);
        nodeFragment.show(getFragmentManager(), "parameters");
    }

    private void popupGround(Node node) {
        if (node.getInitialY() >= height - DELTA / 2) {
            nodePopup(node);
        }
        boolean connected = false;
        for (boolean flag : node.getConstraints()) {
            if (flag) connected = true;
        }
        if (connected && node.getInitialY() <= height - DELTA / 2) {
            nodePopup(node);
        }
    }

    private void setStructureWithIntents(){
        if (getIntent().getExtras() != null) {
            int structureId = getIntent().getExtras().getInt("id");
            String  structureName = getIntent().getExtras().getString("name");
            if (structureId == 0) {
                structure = StructureFactory.cantileverBeam();
            } else if (structureId == 1) {
                structure = StructureFactory.getSimpleHouse();
            } else if (structureId == 2) {
                structure = StructureFactory.getCraneBottom();
            } else if (structureId == 3) {
                structure = StructureFactory.getBetterEiffelTower();
            } else if (structureId == 4) {
                structure = StructureFactory.getEmpireState();
            } else if (structureId == 5) {
                structure = StructureFactory.getGoldenGate();
            } else if (structureId == 6) {
                structure = StructureFactory.getWeirdBridge();
            } else if (structureId == 7) {
                structure = StructureFactory.getHousingBlock();
            } else if (structureId == 8) {
                structure = StructureFactory.getTrumpTower();
            } else if (structureId == 9) {
                structure = StructureFactory.getTVtower();
            } else if (structureId == 10) {
                structure = StructureFactory.getTaipeh();
            } else if (structureId == 11) {
                structure = StructureFactory.getHouseWithMassDamper();
            } else if (structureId == 12) {
                structure = StructureFactory.getOneWTC();
            } else if (structureId == 13) {
                structure = StructureFactory.getBurjKhalifa();
            } else if (structureId == 14) {
                structure = StructureFactory.getTunedMassExample1();
            }else if (structureId == 15) {
                structure = StructureFactory.getTunedMassExample2();
            } else if (structureId == 16) {
                structure = StructureFactory.getSimpleElephant();
            }   else if (structureId == 17) {
                structure = StructureFactory.getEierlaufen();
            } else if (structureId == 18) {
                structure = StructureFactory.getDemoTMD();
            }
            else {
                structure = StructureFactory.getStructure(this, structureName);
            }

            for (Beam beam : structure.getBeams()) {
                beam.computeAll(structure.isLumped());
            }
            loadedBoundingBox=new double[4];
            System.arraycopy(structure.getBoundingBox(),0,loadedBoundingBox,0,4);
            convertStructurefromMetertoPixels();
            DrawHelper.clearCanvas(canvasView);
            DrawHelper.drawStructure(structure, canvasView);
        }else{
            DrawHelper.clearCanvas(canvasView);
            structure = new Structure();
        }

    }

    private void convertStructurefromMetertoPixels(){


        double modelScaling;
        double[] boundingBox = structure.getBoundingBox();

        double modelXSize = boundingBox[1] - boundingBox[0];
        double modelYSize = boundingBox[3] - boundingBox[2];
        // special case for single beam
        if (modelXSize == 0){
            modelXSize = 8;
        }
        if (modelYSize == 0) {
            modelYSize = 8;
        }

        double widthFitScaling = (1 - 2 * CanvasView.SIDE_MARGIN_SCREEN_FRACTION) * width / (modelXSize / 0.75);
        double heightFitScaling = (1 - CanvasView.TOP_MARGIN_SCREEN_FRACTION) * height / (modelYSize / 0.875);

        if (widthFitScaling < heightFitScaling) {
            modelScaling = widthFitScaling;
            boundingBox[0]/=0.75;
            boundingBox[1]/=0.75;
        } else {
            modelScaling = heightFitScaling;
            boundingBox[2]/=0.875;
            boundingBox[3]/=0.875;
        }


        double[] negativeMinCorrections=new double[2];
        double[] screenCenteringOffsets=new double[2];
        screenCenteringOffsets[0] = 0.5 * (width - modelXSize * modelScaling);
        screenCenteringOffsets[1] = height - modelYSize * modelScaling;

        if (boundingBox[0] < 0) {
            negativeMinCorrections[0] = -boundingBox[0];
        } else {
            negativeMinCorrections[0] = 0;
        }

        if (boundingBox[2] < 0) {
            negativeMinCorrections[1] = -boundingBox[1];
        } else {
            negativeMinCorrections[1] = 0;
        }
        structure.setBoundingBox(boundingBox);
        loadedBoundingBox=boundingBox;

        for(Node node:structure.getNodes()){
            transformtoPixels(node, modelScaling, negativeMinCorrections, screenCenteringOffsets);
        }
    }

    public class LongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            Node n = new Node(e.getX(), e.getY() - yOffset);

            if (!setHinge(n)) deleteBeam(n.getCurrentX(), n.getCurrentY());

            DrawHelper.drawStructure(structure, canvasView);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

    }

}
