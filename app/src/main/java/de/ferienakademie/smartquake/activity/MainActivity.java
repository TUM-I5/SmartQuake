package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.excitation.ExcitationManager;
import de.ferienakademie.smartquake.kernel1.Kernel1;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Material;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class MainActivity extends Activity implements Simulation.SimulationProgressListener{

    Sensor mAccelerometer; //sensor object
    SensorManager mSensorManager; // manager to subscribe for sensor events
    ExcitationManager mExcitationManager; // custom accelerometer listener

    Button startButton, stopButton;
    CanvasView canvasView;
    TimeIntegration timeIntegration;
    Structure structure;
    Kernel1 kernel1;
    Simulation simulation;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_activity_actions, menu);
        menu.findItem(R.id.create_button).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.load_replay_button).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.save_replay_button).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.load_replay_button) {
            createStructure();
            DrawHelper.drawStructure(structure, canvasView);
            return true;
        }

        if (id == R.id.create_button) {
            if (simulation != null) simulation.stop();
            startActivity(new Intent(this, CreateActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void createStructure() {
        double width = canvasView.getWidth();
        double height = canvasView.getHeight();
        double middle = canvasView.getWidth() * 0.25f;

        structure = new Structure();
        //TODO Alex: redesign with ID Matrix
        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();
        List<Integer> DOFnode5 = new LinkedList<>();

        List<Double> unode1 = new LinkedList<>();

        DOFnode1.add(0); //constraint
        DOFnode1.add(1);//constraint
        DOFnode1.add(2);//constraint

        DOFnode2.add(3);//constraint
        DOFnode2.add(4);//constraint
        DOFnode2.add(5);//constraint

        DOFnode3.add(6);
        DOFnode3.add(7);
        DOFnode3.add(8);

        DOFnode4.add(9);
        DOFnode4.add(10);
        DOFnode4.add(11);

        DOFnode5.add(12);
        DOFnode5.add(13);
        DOFnode5.add(14);



        unode1.add(0.0);
        unode1.add(0.0);
        unode1.add(0.0);

        Node n1 = new Node(middle, height,DOFnode1,unode1);
        Node n2 = new Node(width - middle, height,DOFnode2,unode1);
        Node n3 = new Node(width - middle, height - middle,DOFnode3,unode1);
        Node n4 = new Node(middle, height - middle,DOFnode4,unode1);
        Node n5 = new Node(2 * middle, height - 2 * middle,DOFnode5,unode1);

        Beam b1 = new Beam(n2, n3);
        Beam b2 = new Beam(n3, n4);
        Beam b3 = new Beam(n4, n1);
        Beam b4 = new Beam(n4, n5);
        Beam b5 = new Beam(n5, n3);


        List<Integer> condof= new ArrayList<>( );
        List<Integer> dof= new ArrayList<>( );
        List<Integer[]> IDMatrix = new ArrayList<Integer[]>();

        IDMatrix.add(b1.getDofs()); //get right notation

        condof.addAll(n1.getDOF()); //ground connected Nodes
        condof.addAll(n2.getDOF());



        Material test = new Material("testmat");
        kernel1.setMaterial(test);
        kernel1.setConDOF(condof);

        structure.addNodes(n1, n2, n3, n4, n5);

        for (int i = 0; i < structure.getNodes().size(); i++) {
            kernel1.addDof(structure.getNodes().get(i).getDOF());
        }
        structure.addBeams(b1, b2, b3, b4, b5);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mExcitationManager = new ExcitationManager();
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                startSimulation();
                Toast.makeText(MainActivity.this, "Simulation started", Toast.LENGTH_SHORT).show();
             }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulation.stop();
                Toast.makeText(MainActivity.this, "Simulation stopped", Toast.LENGTH_SHORT).show();
            }
        });

        canvasView = (CanvasView) findViewById(R.id.shape);
        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    canvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    createStructure();

                    DrawHelper.drawStructure(structure, canvasView);
                }
            });
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(mExcitationManager, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI); //subscribe for sensor events
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mExcitationManager);// do not receive updates when paused
    }

    void startSimulation() {
        kernel1 = new Kernel1(structure, mExcitationManager);
        timeIntegration = new TimeIntegration(kernel1);
        simulation = new Simulation(kernel1, timeIntegration, canvasView);
        simulation.setListener(MainActivity.this);
        simulation.start();
    }

    @Override
    public void onFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Simulation stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
