package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.model.StructureFactory;
import de.ferienakademie.smartquake.view.CanvasView;

public class GraphViewActivity extends AppCompatActivity {
    // should be enough...
    private static int[] graphColors = {Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.BLACK, Color.CYAN, Color.MAGENTA};

    private Structure structure;
    private Node selectedNode;

    private LineChart nodeDataChart;
    private List<ILineDataSet> sets = new ArrayList<>();
    // TODO: scroll graph view and actually use this
    private int numShownDataPoints = 20;


    private CanvasView nodeView;
    private CanvasView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle rcvd = getIntent().getExtras();
        structure = SimulationActivity.getStructure();
        int selectedNodeId = rcvd.getInt("initialNodeId");
        selectedNode = structure.getNodes().get(selectedNodeId);

        setContentView(R.layout.activity_graph_view);
        setUpChart();
    }

    private void setUpChart() {
        nodeDataChart = (LineChart) findViewById(R.id.nodeDataChart);
        nodeDataChart.setBackgroundColor(Color.WHITE);
        nodeDataChart.setDescription("Node displacements");
        nodeDataChart.setPinchZoom(false);

        List<String> graphXPoints = new ArrayList<>();
        List<Entry> xDisplacements = new ArrayList<>();
        List<Entry> yDisplacements = new ArrayList<>();
        List<List<Entry>> rotDisplacements = new ArrayList<>();

        List<List<Double>> historicDisplacements = selectedNode.getHistoryOfDisplacements();

        for (int i = 0; i < historicDisplacements.size(); ++i) {
            graphXPoints.add(Integer.toString(i));
            xDisplacements.add(new Entry((float) ((double) historicDisplacements.get(i).get(0)), i));
            yDisplacements.add(new Entry((float) ((double) historicDisplacements.get(i).get(1)), i));
            for (int j = 2; j < historicDisplacements.get(0).size(); ++j) {
                if (rotDisplacements.size() <= j - 2) {
                    rotDisplacements.add(new ArrayList<Entry>());
                }
                rotDisplacements.get(j - 2).add(new Entry((float) ((double) historicDisplacements.get(i).get(j)), i));
            }
        }

        // all displacements are selected by default
        sets.add(new LineDataSet(xDisplacements, "Along X axis"));
        sets.add(new LineDataSet(yDisplacements, "Along Y axis"));
        for (int i = 0; i < rotDisplacements.size(); ++i) {
            sets.add(new LineDataSet(rotDisplacements.get(i), "Beam " + Integer.toString(i) + " rotation"));
        }

        for (int i = 0; i < sets.size(); ++i) {
            LineDataSet set = (LineDataSet) sets.get(i);
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
        LineData displacementData = new LineData(graphXPoints, sets);
        nodeDataChart.setData(displacementData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_graph_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < selectedNode.getDOF().size() - 2; ++i) {
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
            case R.id.yDofCheckbox:
                // why is this necessary again?
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            default:
                break;
        }

        if (0 <= id && id < selectedNode.getDOF().size() - 2) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }
}