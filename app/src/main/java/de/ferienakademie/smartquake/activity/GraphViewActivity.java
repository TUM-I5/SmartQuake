package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

public class GraphViewActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    // should be enough...
    private static int[] graphColors = {Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.BLACK, Color.CYAN, Color.MAGENTA};

    private Structure wholeStructure;
    private Structure nodeLocalStructure;
    private Node selectedNode;
    private int selectedNodeId;

    private LineChart nodeDataChart; // can do just about anything
    private List<Pair<ILineDataSet, Boolean>> sets;
    // LineData requires strings for some reason
    private List<String> graphXPoints;

    private CanvasView nodeSnapshotView;
    private CanvasView modelSnapshotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle rcvd = getIntent().getExtras();
        wholeStructure = SimulationActivity.getMainViewStructure();
        selectedNodeId = rcvd.getInt("initialNodeId");
        selectedNode = wholeStructure.getNodes().get(selectedNodeId);

        nodeLocalStructure = new Structure();
        nodeLocalStructure.addNode(selectedNode);
        nodeLocalStructure.addBeams(selectedNode.getBeams());

        setContentView(R.layout.activity_graph_view);
        modelSnapshotView = (CanvasView) findViewById(R.id.modelSnapshotView);
        modelSnapshotView.includeRuler = false;
        modelSnapshotView.setSelectedNodeId(selectedNodeId);
        modelSnapshotView.setStructureProvider(new CanvasView.StructureProvider() {
            @Override
            public Structure getStructure() {
                return wholeStructure;
            }
        });

        nodeSnapshotView = (CanvasView) findViewById(R.id.nodeSnapshotView);
        nodeSnapshotView.includeRuler = false;
        nodeSnapshotView.setSelectedNodeId(0);
        nodeSnapshotView.setStructureProvider(new CanvasView.StructureProvider() {
            @Override
            public Structure getStructure() {
                return nodeLocalStructure;
            }
        });
        setUpChart();
    }

    private void setUpChart() {
        nodeDataChart = (LineChart) findViewById(R.id.nodeDataChart);
        nodeDataChart.setBackgroundColor(Color.WHITE);
        nodeDataChart.setDescription("Node displacements");
        nodeDataChart.setPinchZoom(false);
        nodeDataChart.setOnChartValueSelectedListener(this);

        sets = new ArrayList<>();
        graphXPoints = new ArrayList<>();
        List<Entry> xDisplacements = new ArrayList<>();
        List<Entry> yDisplacements = new ArrayList<>();
        List<List<Entry>> rotDisplacements = new ArrayList<>();

        List<List<Double>> historicDisplacements = selectedNode.getHistoryOfDisplacements();

        for (int i = 0; i < historicDisplacements.size(); ++i) {
            graphXPoints.add(Integer.toString(i));
            // Minor Java WTF. Also, Y before X in the library...
            xDisplacements.add(new Entry((float) ((double) historicDisplacements.get(i).get(0)), i));
            yDisplacements.add(new Entry((float) ((double) historicDisplacements.get(i).get(1)), i));
            for (int j = 2; j < historicDisplacements.get(0).size(); ++j) {
                if (rotDisplacements.size() <= j - 2) {
                    rotDisplacements.add(new ArrayList<Entry>());
                }
                // TODO: Scale rotational displacements?
                rotDisplacements.get(j - 2).add(new Entry((float) ((double) historicDisplacements.get(i).get(j)), i));
            }
        }

        // All displacements are selected by default.
        // TODO: Make sure there is enough room for the labels
        sets.add(new Pair<ILineDataSet, Boolean>(new LineDataSet(xDisplacements, "Along X axis"), true));
        sets.add(new Pair<ILineDataSet, Boolean>(new LineDataSet(yDisplacements, "Along Y axis"), true));
        for (int i = 0; i < rotDisplacements.size(); ++i) {
            sets.add(new Pair<ILineDataSet, Boolean>(new LineDataSet(rotDisplacements.get(i), "Beam " + Integer.toString(i + 1) + " rotation"), true));
        }

        for (int i = 0; i < sets.size(); ++i) {
            LineDataSet set = (LineDataSet) sets.get(i).first;
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            if (i < graphColors.length) {
                set.setColor(graphColors[i]);
            } else {
                // give up for now
                Random rand = new Random();
                byte[] colorBytes = new byte[3];
                rand.nextBytes(colorBytes);
                set.setColor(Color.rgb(colorBytes[2], colorBytes[1], colorBytes[0]));
            }

            set.setDrawCircles(false);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);
        }
        updateDataItemVisibility();
    }

    private void updateDataItemVisibility() {
        List<ILineDataSet> enabledSets = new ArrayList<>();
        for (Pair<ILineDataSet, Boolean> p : sets) {
            if (p.second) {
                enabledSets.add(p.first);
            }
        }
        LineData displacementData = new LineData(graphXPoints, enabledSets);
        nodeDataChart.setData(displacementData);
        nodeDataChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        Intent sel = new Intent();
        setResult(Activity.RESULT_CANCELED, sel);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_graph_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < selectedNode.getDOF().size(); ++i) {
            if (menu.findItem(i) == null) {
                menu.add(R.id.dofCheckboxGroup, i, i + 2, "View beam " + Integer.toString(i + 1) + " rotation");
                MenuItem newItem = menu.findItem(i);
                newItem.setCheckable(true);
                newItem.setChecked(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.xDofCheckbox:
                // why is this necessary again?
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                // Pair is handy, but immutable
                sets.set(0, new Pair<>(sets.get(0).first, !sets.get(0).second));
                break;
            case R.id.yDofCheckbox:
                if (item.isChecked()) {
                    item.setChecked(false);

                } else {
                    item.setChecked(true);
                }
                sets.set(1, new Pair<>(sets.get(1).first, !sets.get(1).second));
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        if (0 <= id && id < selectedNode.getDOF().size()) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            sets.set(id + 2, new Pair<>(sets.get(id + 2).first, !sets.get(id + 2).second));
        }

        updateDataItemVisibility();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        for (Node n: wholeStructure.getNodes()) {
            n.recallDisplacementOfStep(entry.getXIndex());
        }
        modelSnapshotView.invalidate();
        nodeSnapshotView.invalidate();
    }

    @Override
    public void onNothingSelected() {

    }
}