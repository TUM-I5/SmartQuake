package de.ferienakademie.smartquake.activity;

import android.content.Context;
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
import android.widget.Button;

import java.io.IOException;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.EmptyAccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;
import de.ferienakademie.smartquake.excitation.SensorAccelerationProvider;
import de.ferienakademie.smartquake.excitation.SinCosExcitation;
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

    private SimulationMode mode = SimulationMode.LIVE;

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sim_reset_button) {
            if (mode != SimulationMode.LIVE) {
                mode = SimulationMode.LIVE;
            }

            if (simulation.isRunning()) {
                onStopButtonClicked();
            }
            createStructure(structureId, structureName);
            DrawHelper.drawStructure(structure, canvasView);
            return true;
        }

        if (id == R.id.sim_replay_button && !simulation.isRunning()) {
            Snackbar.make(layout, "Simulation started", Snackbar.LENGTH_SHORT).show();
            FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();

            try {
                fileAccelerationProvider.load(openFileInput("Last.earthquake"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mode = SimulationMode.REPLAY;

            startSimulation(fileAccelerationProvider);
            simFab.setOnClickListener(stopSimulationListener);
            simFab.setImageResource(R.drawable.ic_pause_white_24dp);
            return true;
        } else if (id == R.id.sim_load_earthquake_data_button) {
            // load EQ data
        }




        return super.onOptionsItemSelected(item);
    }

    private void createStructure(int structureId, String structureName) {
        if (structureId == 0) {
            structure = StructureFactory.cantileverBeam();
        } else if (structureId == 1) {
            structure = StructureFactory.getSimpleHouse();
        } else {
            structure = StructureFactory.getStructure(this, structureName);
        }

        for (Beam beam : structure.getBeams()) {
            beam.computeAll(structure.isLumped());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

    private void toggleStartStopAvailability() {
        if (simulation.isRunning()) {
            // started
            simFab.setImageResource(R.drawable.ic_pause_white_24dp);
            simFab.setOnClickListener(stopSimulationListener);
        } else {
            // stopped
            simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            simFab.setOnClickListener(startSimulationListener);
        }
    }

    private void onStartButtonClicked()
    {
        String msgString = "Simulation started";
        if (mode == SimulationMode.LIVE)
        {
            msgString += ". Start shaking!";
        }
        Snackbar.make(layout, msgString, Snackbar.LENGTH_SHORT).show();

        //startSimulation(new SensorAccelerationProvider(mSensorManager, mAccelerometer));
        startSimulation(new SinCosExcitation());
    }

    void startSimulation(AccelerationProvider accelerationProvider) {
        if (mCurrentAccelerationProvider != null)
        {
            mCurrentAccelerationProvider.setInactive();
        }

        mCurrentAccelerationProvider = accelerationProvider;

        String msgString = "Simulation started";
        if (mode == SimulationMode.LIVE) {
            msgString += ". Start shaking!";
        }
        Snackbar.make(layout, msgString, Snackbar.LENGTH_SHORT).show();

        spatialDiscretization = new SpatialDiscretization(structure);
        timeIntegration = new TimeIntegration(spatialDiscretization, accelerationProvider);
        simulation = new Simulation(spatialDiscretization, timeIntegration, canvasView);

        accelerationProvider.setActive();
        accelerationProvider.initTime(30_000_000);
        simulation.start();
        simulation.setListener(this);

        toggleStartStopAvailability();
    }

    private void onStopButtonClicked() {
        simulation.stop();
        Snackbar.make(layout, "Simulation stopped", Snackbar.LENGTH_SHORT).show();

        mCurrentAccelerationProvider.setInactive();
        try {
            mCurrentAccelerationProvider.saveFile(openFileOutput("Last.earthquake", MODE_PRIVATE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentAccelerationProvider = new EmptyAccelerationProvider();

        toggleStartStopAvailability();
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
    public void onSimulationStateChanged(Simulation.SimulationState newSpeedState) {

        String msg = "";

        switch (newSpeedState) {
            case RUNNING_SLOW:
                msg = "Simulation speed slow";
                slowSnackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
                slowSnackbar.show();
                break;
            case RUNNING_NORMAL:
                msg = "Simulation speed normal";
                if (slowSnackbar != null) slowSnackbar.dismiss();
                break;
        }

        Log.d("SimSpeed", msg);

    }

    // TODO: should this be part of Simulation too?
    private enum SimulationMode {
        LIVE,
        REPLAY,
        FILE_REPLAY
    }


}
