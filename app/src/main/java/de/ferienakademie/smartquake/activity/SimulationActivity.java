package de.ferienakademie.smartquake.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.excitation.Recorder;
import de.ferienakademie.smartquake.excitation.SensorExcitation;
import de.ferienakademie.smartquake.kernel1.Kernel1;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class SimulationActivity extends AppCompatActivity implements Simulation.SimulationProgressListener {

    private Sensor mAccelerometer; //sensor object
    private SensorManager mSensorManager; // manager to subscribe for sensor events
    private SensorExcitation mExcitationManager; // custom accelerometer listener

    private FloatingActionButton simFab;
    private CanvasView canvasView;
    private TimeIntegration timeIntegration;
    private Structure structure;
    private Kernel1 kernel1;
    private Simulation simulation;

    private Recorder recorder;

    // Click listeners
    private View.OnClickListener startSimulationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onStartButtonClicked();
        }
    };

    private View.OnClickListener stopSimulationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onStopButtonClicked();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.simulation_activity_actions, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createStructure() {
        structure = StructureFactory.getSimpleHouse();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mExcitationManager = new SensorExcitation();
        recorder = new Recorder();
        mExcitationManager.registerLstnr(recorder);

        simFab = (FloatingActionButton) findViewById(R.id.simFab);
        simFab.setOnClickListener(startSimulationListener);

        canvasView = (CanvasView) findViewById(R.id.simCanvasView);
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
        mSensorManager.unregisterListener(mExcitationManager); // do not receive updates when paused
    }

    private void onStartButtonClicked() {
        startSimulation();

        simFab.setOnClickListener(stopSimulationListener);
        simFab.setImageResource(R.drawable.ic_pause_white_24dp);
    }

    private void onStopButtonClicked() {
        simulation.stop();
        Toast.makeText(SimulationActivity.this, "Simulation stopped", Toast.LENGTH_SHORT).show();

        simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        simFab.setOnClickListener(startSimulationListener);
    }

    void startSimulation() {
        Toast.makeText(SimulationActivity.this, "Simulation started", Toast.LENGTH_SHORT).show();

        recorder.initRecord();

        kernel1 = new Kernel1(structure, mExcitationManager);
        timeIntegration = new TimeIntegration(kernel1);
        simulation = new Simulation(kernel1, timeIntegration, canvasView);
        simulation.setListener(SimulationActivity.this);
        simulation.start();
    }

    @Override
    public void onSimulationFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                simFab.setOnClickListener(startSimulationListener);
                simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                Toast.makeText(SimulationActivity.this, "Simulation stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
