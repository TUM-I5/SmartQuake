package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    private ListView dofCheckboxList;
    private CanvasView nodeView;
    private CanvasView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        nodeDataChart = (LineChart) findViewById(R.id.nodeDataChart);
        nodeDataChart.setBackgroundColor(Color.WHITE);
        nodeDataChart.setDescription("Dummy displacement");
        nodeDataChart.setPinchZoom(false);

        /*List<String> dofs = new ArrayList<>();
        dofs.add("Some DOF");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_checkbox, R.id.dofCheckBox, dofs);
        dofCheckboxList = (ListView) findViewById(R.id.dofChoiceList);
        dofCheckboxList.setAdapter(adapter);
        dofCheckboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });*/

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
        sets.add(new LineDataSet(line1, "values 1"));
        sets.add(new LineDataSet(line2, "values 2"));

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_start_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}