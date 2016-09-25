package de.ferienakademie.smartquake.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.IOException;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.EmptyAccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;
import de.ferienakademie.smartquake.excitation.SensorAccelerationProvider;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class SimulationActivity extends AppCompatActivity implements Simulation.SimulationProgressListener {

    private Sensor mAccelerometer; //sensor object
    private SensorManager mSensorManager; // manager to subscribe for sensor events
    private AccelerationProvider mCurrentAccelerationProvider = new EmptyAccelerationProvider();

    private FloatingActionButton simFab;
    private CanvasView canvasView;
    private TimeIntegration timeIntegration;
    private Structure structure;
    private SpatialDiscretization spatialDiscretization;
    private Simulation simulation;
    private CoordinatorLayout layout;
    private Snackbar slowSnackbar;
    private SimulationState state = SimulationState.STOPPED;

    private int structureId;
    private String structureName;

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
        menu.findItem(R.id.reset_button).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.replay_button).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reset_button) {
            // reset implicitly ends replaying
            if (state == SimulationState.REPLAY_RUNNING) {
                state = SimulationState.RUNNING;
            }

            if (state == SimulationState.RUNNING) {
                onStopButtonClicked();
            }
            createStructure(structureId, structureName);
            DrawHelper.drawStructure(structure, canvasView);
            return true;
        }

        if (id == R.id.create_button) {
            if (simulation != null) simulation.stop();
            startActivity(new Intent(this, CreateActivity.class));
            return true;
        }

        if (id == R.id.replay_button && state == SimulationState.STOPPED) {
            Snackbar.make(layout, "Simulation started", Snackbar.LENGTH_SHORT).show();
            FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();

            try {
                fileAccelerationProvider.load(openFileInput("saveAcc.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            state = SimulationState.REPLAY_RUNNING;

            startSimulation(fileAccelerationProvider);
            simFab.setOnClickListener(stopSimulationListener);
            simFab.setImageResource(R.drawable.ic_pause_white_24dp);
        }


        return super.onOptionsItemSelected(item);
    }

    private void createStructure(int structureId, String structureName) {
        if (structureId == 0) {
            structure = StructureFactory.cantileverBeam();
        } else if (structureId == 1) {
            structure = StructureFactory.getSimpleHouse();
        } else if (structureId == 2) {
            structure = StructureFactory.getSimpleEiffelTower();
        } else {
            structure = StructureFactory.getStructure(this, structureName);
        }

        for (Beam beam : structure.getBeams()) {
            beam.computeAll(true);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        simFab = (FloatingActionButton) findViewById(R.id.simFab);
        simFab.setOnClickListener(startSimulationListener);
        layout = (CoordinatorLayout) findViewById(R.id.simLayout);

        canvasView = (CanvasView) findViewById(R.id.simCanvasView);
        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        structureId = getIntent().getExtras().getInt("id");
        structureName = getIntent().getExtras().getString("name");

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    canvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    createStructure(structureId, structureName);
                    DrawHelper.drawStructure(structure, canvasView);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentAccelerationProvider.setActive();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentAccelerationProvider.setInactive();
    }

    private void onStartButtonClicked() {
        startSimulation(new SensorAccelerationProvider(mSensorManager, mAccelerometer));
        if (state == SimulationState.REPLAY_STOPPED) {
            state = SimulationState.REPLAY_RUNNING;
        } else if (state == SimulationState.STOPPED) {
            state = SimulationState.RUNNING;
        } else {
            throw new IllegalStateException("Simulation already running (" + state.name() + ") before Start button press");
        }
        simFab.setOnClickListener(stopSimulationListener);
        simFab.setImageResource(R.drawable.ic_pause_white_24dp);
    }

    private void onStopButtonClicked() {
        simulation.stop();
        if (state == SimulationState.RUNNING) {
            state = SimulationState.STOPPED;
        } else if (state == SimulationState.REPLAY_RUNNING) {
            state = SimulationState.REPLAY_STOPPED;
        } else {
            throw new IllegalStateException("Simulation already stopped (" + state.name() + ") before Stop button press");
        }

        Snackbar.make(layout, "Simulation stopped", Snackbar.LENGTH_SHORT).show();

        mCurrentAccelerationProvider.setInactive();
        try {
            mCurrentAccelerationProvider.saveFile(openFileOutput("saveAcc.txt", MODE_PRIVATE));
        } catch (IOException e) {
            e.printStackTrace();
        }


        simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        simFab.setOnClickListener(startSimulationListener);
    }

    void startSimulation(AccelerationProvider accelerationProvider) {
        if (mCurrentAccelerationProvider != null)
        {
            mCurrentAccelerationProvider.setInactive();
        }

        mCurrentAccelerationProvider = accelerationProvider;

        Snackbar.make(layout, "Simulation started", Snackbar.LENGTH_SHORT).show();


        spatialDiscretization = new SpatialDiscretization(structure);
        timeIntegration = new TimeIntegration(spatialDiscretization, accelerationProvider);
        simulation = new Simulation(spatialDiscretization, timeIntegration, canvasView);

        accelerationProvider.setActive();
        accelerationProvider.initTime(30_000_000);
        simulation.start();
        simulation.setListener(this);

    }

    @Override
    public void onSimulationFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                simFab.setOnClickListener(startSimulationListener);
                simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                Snackbar.make(layout, "Simulation stopped", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSimulationSpeedChanged(Simulation.SpeedState newSpeedState) {

        String msg = "";

        switch (newSpeedState) {
            case SLOW:
                msg = "Simulation speed slow";
                slowSnackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
                slowSnackbar.show();
                break;
            case NORMAL:
                msg = "Simulation speed normal";
                if (slowSnackbar != null) slowSnackbar.dismiss();
                break;
        }

        Log.d("SimSpeed", msg);

    }

    // TODO: shouldn't this be part of Simulation?
    private enum SimulationState {
        RUNNING,
        STOPPED,
        REPLAY_RUNNING,
        REPLAY_STOPPED
    }
}
