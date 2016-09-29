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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import de.ferienakademie.smartquake.LiveSimulation;
import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.ReplaySimulation;
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
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class SimulationActivity extends AppCompatActivity implements Simulation.SimulationProgressListener,
        SaveEarthquakeFragment.SaveEarthquakeListener, AccelerationProviderObserver, CanvasView.NodePositionChoiceListener, Simulation.StructureUpdateListener, CanvasView.StructureProvider {

    // TODO: break this up
    private static final int REQUEST_EARTHQUAKE_DATA = 0;
    private static Structure structure;
    private SensorManager mSensorManager; // manager to subscribe for sensor events
    private AccelerationProvider mCurrentAccelerationProvider = new EmptyAccelerationProvider();
    private FloatingActionButton simFab;
    private CanvasView canvasView;
    private TimeIntegration timeIntegration;
    private SpatialDiscretization spatialDiscretization;
    private Simulation simulation;
    private CoordinatorLayout layout;
    private Snackbar slowSnackbar;
    private ProgressBar replaySeekBar;
    private TextView replayrunningLabel;
    private double replayProgress;
    private SimulationMode mode = SimulationMode.READY;
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

    @Override
    public Structure getStructure() {
        return structure;
    }

    // ...
    public static Structure getMainViewStructure() {
        return structure;
    }

    private void runReplay(String fileName) {

        Log.d("REPLAYING", fileName);

        try {
            switch (fileName) {
                case "Sinusodial.earthquake":
                    mode = SimulationMode.LIVE;
                    SinCosExcitation sinCosExcitation = new SinCosExcitation();
                    sinCosExcitation.setFrequency(PreferenceReader.getExcitationFrequency());
                    startSimulation(sinCosExcitation);
                    break;
                case "Sensors.earthquake":
                    mode = SimulationMode.REPLAY;
                    onStartButtonClicked();
                    return;
                default:
                    mode = SimulationMode.REPLAY;
                    FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();
                    fileAccelerationProvider.load(openFileInput(fileName));
                    if (!fileAccelerationProvider.isEmpty()) {
                        replaySeekBar.setVisibility(View.VISIBLE);
                        startSimulation(fileAccelerationProvider);
                    } else {
                        Snackbar.make(layout, "No past acceleration data", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mode = SimulationMode.REPLAY;
        replayrunningLabel.setVisibility(View.VISIBLE);
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
        if (id == android.R.id.home) {
            onStopButtonClicked();
            onBackPressed();
            return true;
        } else if (id == R.id.sim_replay_button && (simulation == null || !simulation.isRunning()) && mode != SimulationMode.REPLAY) {
            runReplay("Last.earthquake");
            if (simulation != null) toggleStartStopAvailability();
            return true;
        } else if (id == R.id.sim_load_earthquake_data_button) {
            ActionMenuItemView loadEqDataButton = (ActionMenuItemView) findViewById(id);
            if (loadEqDataButton != null) loadEqDataButton.setEnabled(false);
            ActionMenuItemView replay = (ActionMenuItemView) findViewById(R.id.sim_replay_button);
            if (replay != null) replay.setEnabled(false);
            Intent chooseEqIntent = new Intent(this, ChooseEarthQuakeDataActivity.class);
            chooseEqIntent.putExtra("id", structureId);
            chooseEqIntent.putExtra("name", structureName);
            startActivityForResult(chooseEqIntent, REQUEST_EARTHQUAKE_DATA);
        } else if (id == R.id.save_simulation) {
            if (simulation.isRunning()) {
                simulation.stop();
            }
            new SaveEarthquakeFragment().show(getFragmentManager(), "saveEarthquake");
        } else if (id == R.id.sim_reset_button) {
            if (mode != SimulationMode.LIVE) {
                mode = SimulationMode.LIVE;
            }

            if (simulation != null && simulation.isRunning()) {
                onStopButtonClicked();
            }
            tvSensorDataX.setText("");
            tvSensorDataY.setText("");
            createStructure(structureId, structureName);
            onStructureUpdate(structure);
            return true;
        } else if (id == R.id.sim_replay_displacement) {
            if (simulation != null && simulation.isRunning()) {
                onStopButtonClicked();
            }
            replayDisplacement();
        } else if (id == R.id.sim_view_graphs_button) {
            Intent i = new Intent(this, GraphViewActivity.class);
            i.putExtra("initialNodeId", canvasView.getSelectedNodeId());
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("id", structureId);
        outState.putString("name", structureName);
    }

    private void createStructure(int structureId, String structureName) {
        structure = StructureFactory.getStructure(this, structureId, structureName);

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
        canvasView.setNodePositionChoiceListener(this);
        canvasView.setStructureProvider(this);
        canvasView.setSelectedNodeId(1);
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
                    onStructureUpdate(structure);
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

    private void setStopButton() {
        simFab.setImageResource(R.drawable.ic_pause_white_24dp);
        simFab.setOnClickListener(stopSimulationListener);
    }

    private void setStartButton() {
        simFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        simFab.setOnClickListener(startSimulationListener);
    }

    private void onStartButtonClicked() {
        // TODO: currently resets the model somehow
        mode = SimulationMode.LIVE;
        startSimulation(new SensorAccelerationProvider(mSensorManager));
    }

    void startSimulation(AccelerationProvider accelerationProvider) {
        if (simulation != null)
        {
            simulation.stop();
        }

        if (mCurrentAccelerationProvider != null) {
            mCurrentAccelerationProvider.removeObserver(this);
            mCurrentAccelerationProvider.setInactive();
        }

        mCurrentAccelerationProvider = accelerationProvider;
        mCurrentAccelerationProvider.addObserver(this);


        // reset structure if this is a resimulation
        structure.resetHistoryOfNodes();
        structure.resetBeams();


        spatialDiscretization = new SpatialDiscretization(structure);
        timeIntegration = new TimeIntegration(spatialDiscretization, accelerationProvider);
        simulation = new LiveSimulation(spatialDiscretization, timeIntegration, canvasView);
        simulation.setSimulationProgressListener(this);
        simulation.setStructureUpdateListener(this);
        simulation.start();

        toggleStartStopAvailability();
    }

    private void onStopButtonClicked() {
        Log.d("STOPSIM STATE", mode.name());

        mode = SimulationMode.READY;

        replaySeekBar.setVisibility(View.GONE);
        replayrunningLabel.setVisibility(View.GONE);

        if (simulation == null) return;
        simulation.stop();

        mCurrentAccelerationProvider.removeObserver(this);
        mCurrentAccelerationProvider.setInactive();
        try {
            mCurrentAccelerationProvider.saveFileIfDataPresent(this, "Last.earthquake");
        } catch (IOException e) {
            Log.e("ACCEL WRITE", "error writing", e);
        }

        mCurrentAccelerationProvider = new EmptyAccelerationProvider();

        ActionMenuItemView loadEQDataButton = (ActionMenuItemView) findViewById(R.id.sim_load_earthquake_data_button);
        if (loadEQDataButton != null) loadEQDataButton.setEnabled(true);

        ActionMenuItemView replay = (ActionMenuItemView) findViewById(R.id.sim_replay_button);
        if (replay != null) replay.setEnabled(true);

        if (slowSnackbar != null && slowSnackbar.isShown()) slowSnackbar.dismiss();

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
            Log.e("ACCEL WRITE", "file not found", e);
        } catch (IOException e) {
            Log.e("ACCEL WRITE", "error writing", e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                Log.e("ACCEL WRITE", "error closing", e);
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
            if (data.getExtras().containsKey("id"))
                structureId = data.getExtras().getInt("id");
            if (data.getExtras().containsKey("name"))
                structureName = data.getExtras().getString("name");
        }
    }

    public void onNewAccelerationValue(AccelData data) {
        long timestamp = System.currentTimeMillis();
        if (timestamp - lastDebugSensorDataTimestamp > 100) {
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
                }
            });

        }
    }

    /**
     * replays previous simulation without calculating again
     */
    private void replayDisplacement() {

        if (mode == SimulationMode.REPLAY || mode == SimulationMode.LIVE) {
            return;
        }

        if (simulation != null) {
            mode = SimulationMode.REPLAY;
            replayrunningLabel.setVisibility(View.VISIBLE);
            replaySeekBar.setVisibility(View.VISIBLE);
        }

        setStopButton();

        //This tells us how many time steps were calculated
        simulation = new ReplaySimulation(structure, this, canvasView);

        simulation.start();
        simulation.setSimulationProgressListener(this);
        simulation.setStructureUpdateListener(this);
    }

    @Override
    public void onNodePositionChosen(double internalX, double internalY, double scale) {
        List<Node> nodes = structure.getNodes();
        double maxDist = 50;
        double closestDist = maxDist;
        double nodeFingerDistManhattan;
        Node thisNode;

        for (int i = 0; i < nodes.size(); ++i) {
            thisNode = nodes.get(i);
            nodeFingerDistManhattan = Math.abs(thisNode.getCurrentX() - internalX)
                    + Math.abs(thisNode.getCurrentY() - internalY);
            if (nodeFingerDistManhattan < closestDist) {
                canvasView.setSelectedNodeId(i);
                closestDist = nodeFingerDistManhattan;
            }
        }

        onStructureUpdate(structure);
    }

    @Override
    public void onStructureUpdate(Structure newStructure) {
        if (newStructure != structure) {
            structure = newStructure;
        }
        DrawHelper.drawStructure(structure, canvasView, null);
    }

    // TODO: should this be part of Simulation too?
    private enum SimulationMode {
        READY,
        LIVE,
        REPLAY
    }
}
