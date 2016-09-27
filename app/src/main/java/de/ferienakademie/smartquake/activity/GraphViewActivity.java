package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.view.CanvasView;

public class GraphViewActivity extends AppCompatActivity {
    private LineChart nodeDataChart;
    private int numRotationalDofs = 1;
    private CanvasView nodeView;
    private CanvasView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        nodeDataChart = (LineChart) findViewById(R.id.nodeDataChart);
        nodeDataChart.setBackgroundColor(Color.WHITE);
        nodeDataChart.setDescription("Node displacements");
        nodeDataChart.setPinchZoom(false);


        List<String> xList = new ArrayList<>();
        List<Entry> line1 = new ArrayList<>();
        List<Entry> line2 = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            // y before x?
            xList.add(Integer.toString(i));
            line1.add(new Entry((float) Math.sin(i), i));
            line2.add(new Entry((float) Math.cos(i), i));
        }

        List<ILineDataSet> sets = new ArrayList<>();
        sets.add(new LineDataSet(line1, "X axis (dummy)"));
        sets.add(new LineDataSet(line2, "Y axis (dummy)"));

        for (ILineDataSet iSet : sets) {
            LineDataSet set = (LineDataSet) iSet;

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(Color.rgb(255, 241, 46));
            set.setDrawCircles(false);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);
        }

        LineData dummyData = new LineData(xList, sets);
        nodeDataChart.setData(dummyData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_graph_view, menu);
        //onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < numRotationalDofs; ++i) {
            if (menu.findItem(i) == null) {
                menu.add(R.id.dofCheckboxGroup, i, i + 2, "View rotation " + Integer.toString(i));
                MenuItem newItem = menu.findItem(i);
                newItem.setCheckable(true);
                newItem.setChecked(false);
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

        if (0 <= id  && id < numRotationalDofs) {
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }
}