package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.R;

public class ChooseDataActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int mPosition = ListView.INVALID_POSITION;

    private List<String> values = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_data);

        values = new ArrayList<>();
        values.add("Phone sensors");
        values.add("Last");

        ListView lv = (ListView) findViewById(R.id.list_view_eq_data);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_eq_data, R.id.list_item_eq_data_text, values));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //String  itemValue    = (String) mListView.getItemAtPosition(position);
                onItemSelected(position);
                mPosition = position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            //TODO What happens when you want to play recorded quake data

        } else if (id == R.id.nav_manage) {
            //TODO What happens when you want tools
        }

        return true;
    }

    public void onItemSelected(Integer dataSourceId) {
        Intent sel = new Intent();
        if (dataSourceId != 0) {
            sel.putExtra("eqDataFile", values.get(dataSourceId));
        }
        setResult(Activity.RESULT_OK, sel);
        finish();
    }
}