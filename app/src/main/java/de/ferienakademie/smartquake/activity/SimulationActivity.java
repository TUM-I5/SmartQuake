package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.Simulation;
import de.ferienakademie.smartquake.excitation.AccelData;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.excitation.AccelerationProviderObserver;
import de.ferienakademie.smartquake.excitation.EmptyAccelerationProvider;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;
import de.ferienakademie.smartquake.excitation.SensorAccelerationProvider;
import de.ferienakademie.smartquake.excitation.SinCosExcitation;
import de.ferienakademie.smartquake.fragment.SaveEarthquakeFragment;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.managers.PreferenceReader;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class SimulationActivity extends AppCompatActivity implements Simulation.SimulationProgressListener,
        SaveEarthquakeFragment.SaveEarthquakeListener, AccelerationProviderObserver {

    // TODO: global enum for this?
    private static final int REQUEST_EARTHQUAKE_DATA = 0;
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
    private ProgressBar replaySeekBar;
    private TextView replayrunningLabel;
    private double replayProgress;
    private SimulationMode mode = SimulationMode.LIVE;
    //Sensor Debug Views
    private TextView tvSensorDataX;
    private TextView tvSensorDataY;
    private LinearLayout layoutSensorDebug;

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
    private long lastDebugSensorDataTimestamp;

    private void runReplay(String fileName) {
        FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();

        try {
            switch (fileName) {
                case "Sinusodial.earthquake":
                    SinCosExcitation sinCosExcitation = new SinCosExcitation();
                    sinCosExcitation.setFrequency(PreferenceReader.getExcitationFrequency());
                    startSimulation(sinCosExcitation);
                    break;
                case "Sensors.earthquake":
                    onStartButtonClicked();
                    break;
                default:
                    fileAccelerationProvider.load(openFileInput(fileName));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileAccelerationProvider.isEmpty()) {
            //Snackbar.make(layout, "Empty acceleration file", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),
                    "Empty acceleration file", Toast.LENGTH_SHORT).show();
        } else {
            mode = SimulationMode.REPLAY;
            //Snackbar.make(layout, "Simulation started", Snackbar.LENGTH_SHORT).show();
            replaySeekBar.setVisibility(View.VISIBLE);
            replayrunningLabel.setVisibility(View.VISIBLE);
            startSimulation(fileAccelerationProvider);
            toggleStartStopAvailability();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.simulation_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sim_replay_button && (simulation == null || !simulation.isRunning()) && mode != SimulationMode.REPLAY) {
            runReplay("Last.earthquake");
            if (simulation != null) toggleStartStopAvailability();
            return true;
        } else if (id == R.id.sim_load_earthquake_data_button) {
            ActionMenuItemView loadEqDataButton = (ActionMenuItemView)findViewById(id);
            if (loadEqDataButton != null) loadEqDataButton.setEnabled(false);
            ActionMenuItemView replay = (ActionMenuItemView)findViewById(R.id.sim_replay_button);
            if (replay != null) replay.setEnabled(false);
            startActivityForResult(new Intent(this, ChooseDataActivity.class), REQUEST_EARTHQUAKE_DATA);
        } else if (id == R.id.save_simulation) {
            if (simulation.isRunning()) {
                simulation.stop();
            }
            new SaveEarthquakeFragment().show(getFragmentManager(), "saveEarthquake");
        } else if (id == R.id.sim_reset_button){
            if (mode != SimulationMode.LIVE) {
                mode = SimulationMode.LIVE;
            }

            if (simulation.isRunning()) {
                onStopButtonClicked();
            }
            tvSensorDataX.setText("");
            tvSensorDataY.setText("");
            createStructure(structureId, structureName);
            DrawHelper.drawStructure(structure, canvasView);
            return true;

        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("id", structureId);
        outState.putString("name", structureName);
    }

    private void createStructure(int structureId, String structureName) {
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
        replaySeekBar = (ProgressBar) findViewById(R.id.replaySeekBar);
        replaySeekBar.setVisibility(View.GONE);

        replayrunningLabel = (TextView) findViewById(R.id.replaytext);
        replayrunningLabel.setVisibility(View.GONE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        simFab = (FloatingActionButton) findViewById(R.id.simFab);
        simFab.setOnClickListener(startSimulationListener);
        layout = (CoordinatorLayout) findViewById(R.id.simLayout);

        canvasView = (CanvasView) findViewById(R.id.simCanvasView);
        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        if (savedInstanceState != null) {
            structureId = savedInstanceState.getInt("id");
            structureName = savedInstanceState.getString("name");
        }

        if (getIntent().getExtras() != null) {
            structureId = getIntent().getExtras().getInt("id");
            structureName = getIntent().getExtras().getString("name");
        }
        findSensorDataDebugViews();

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

    private void findSensorDataDebugViews() {
        layoutSensorDebug = (LinearLayout) findViewById(R.id.sim_layout_sensor_debug);
        tvSensorDataX = (TextView) findViewById(R.id.sim_tv_sensor_x);
        tvSensorDataY = (TextView) findViewById(R.id.sim_tv_sensor_y);

        int visibility = PreferenceReader.showRawSensorData() ? View.VISIBLE : View.GONE;
        layoutSensorDebug.setVisibility(visibility);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onStopButtonClicked();
    }

    private void toggleStartStopAvailability() {
        if (simulation == null || simulation.isRunning()) {
            // started
            simFab.setImageResource(R.drawable.ic_pause_white_24dp);
            simFab.setOnClickListener(stopSimulationListener);
        } else {
            // stopped
            simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            simFab.setOnClickListener(startSimulationListener);
        }
    }

    private void onStartButtonClicked() {
        startSimulation(new SensorAccelerationProvider(mSensorManager));
    }

    void startSimulation(AccelerationProvider accelerationProvider) {
        if (mCurrentAccelerationProvider != null){
            mCurrentAccelerationProvider.removeObserver(this);
            mCurrentAccelerationProvider.setInactive();
        }

        mCurrentAccelerationProvider = accelerationProvider;
        mCurrentAccelerationProvider.addObserver(this);

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
        if (mode == SimulationMode.REPLAY) {
            replaySeekBar.setVisibility(View.GONE);
            replayrunningLabel.setVisibility(View.GONE);
        }
        if (simulation == null) return;
        simulation.stop();
        Snackbar.make(layout, "Simulation stopped", Snackbar.LENGTH_SHORT).show();

        mCurrentAccelerationProvider.removeObserver(this);
        mCurrentAccelerationProvider.setInactive();
        if (mode == SimulationMode.LIVE) {
            try {
                mCurrentAccelerationProvider.saveFile(openFileOutput("Last.earthquake", MODE_PRIVATE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCurrentAccelerationProvider = new EmptyAccelerationProvider();

        ActionMenuItemView loadEQDataButton = (ActionMenuItemView) findViewById(R.id.sim_load_earthquake_data_button);
        if (loadEQDataButton != null) loadEQDataButton.setEnabled(true);
        ActionMenuItemView replay = (ActionMenuItemView) findViewById(R.id.sim_replay_button);
        if (replay != null) replay.setEnabled(true);

        toggleStartStopAvailability();
    }

    @Override
    public void onSimulationFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                simFab.setOnClickListener(startSimulationListener);
                simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                slowSnackbar = null;
            }
        });
    }

    @Override
    public void onSimulationStateChanged(Simulation.SimulationState newSpeedState) {

        String msg;

        if (newSpeedState == Simulation.SimulationState.RUNNING_SLOW && slowSnackbar == null) {
            msg = "Simulation speed might be slow...";
            slowSnackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE);
            slowSnackbar.setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    slowSnackbar.dismiss();
                }
            });
            slowSnackbar.show();
        }

        Log.v("SimSpeed", newSpeedState.name());

    }


    public void onNameChosen(String name) {
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileOutputStream = openFileOutput(name + ".earthquake", Context.MODE_PRIVATE);
            fileInputStream = openFileInput("Last.earthquake");
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_EARTHQUAKE_DATA) {
            if (data.getExtras().containsKey("eqDataFile")) {
                String fileName = data.getExtras().getString("eqDataFile") + ".earthquake";
                runReplay(fileName);
            }
        }

    }

    public void onNewAccelerationValue(AccelData data) {
        long timestamp = System.currentTimeMillis();
        if (timestamp - lastDebugSensorDataTimestamp  > 100) {
            lastDebugSensorDataTimestamp = System.currentTimeMillis();
            final String xData = String.format("x: %.2f %.2f", data.xAcceleration, data.xGravity);
            final String yData = String.format("y: %.2f %.2f", data.yAcceleration, data.yGravity);
            tvSensorDataX.post(new Runnable() {
                @Override
                public void run() {
                    tvSensorDataX.setText(xData);
                    tvSensorDataY.setText(yData);
                }
            });
        }
    }

    @Override
    public void onNewReplayPercent(double percent) {
        replayProgress = percent;
        replaySeekBar.setProgress((int) Math.round(percent));
        if (percent >= 100) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onStopButtonClicked();
                    mode = SimulationMode.LIVE;
                }
            });

        }
    }

    // TODO: should this be part of Simulation too?
    private enum SimulationMode {
        LIVE,
        REPLAY
    }

}
