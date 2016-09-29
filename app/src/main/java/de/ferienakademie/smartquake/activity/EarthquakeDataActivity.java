package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ferienakademie.smartquake.R;

/**
 * Created by root on 26.09.16.
 */
public class EarthquakeDataActivity extends AppCompatActivity {

    private int mPosition = ListView.INVALID_POSITION;

    private List<String> values = null;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_data);

        values = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.list_view_eq_data);
        registerForContextMenu(lv);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_eq_data, R.id.list_item_eq_data_text, values);
        setUpValues();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //onItemSelected(position);
                Intent intent = new Intent(EarthquakeDataActivity.this, AccelerationDataActivity.class);
                intent.putExtra("eqDataFile", values.get(position));
                //setResult(Activity.RESULT_OK, intent);
                //finish();
                startActivity(intent);
                mPosition = position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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

    public void onItemSelected(Integer dataSourceId) {
        Intent sel = new Intent();
        sel.putExtra("eqDataFile", values.get(dataSourceId));
        setResult(Activity.RESULT_OK, sel);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_quakedata, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch(item.getItemId()) {
            case R.id.delete_quakedata:
                delete_action(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void delete_action(int position){
        String name_of_file  = values.get(position) + ".earthquake";

        if (position > 1){
            File file = new File(getFilesDir().getAbsoluteFile() + "/" + name_of_file);
            boolean  deleted = false;
            if(file.exists()) {
                deleted = file.delete();
                setUpValues();
            }
            if(!deleted) {
                Log.e("Unable to delete file: " + file.getAbsolutePath(), "IOException");
            }
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "You are not allowed to delete this data", Toast.LENGTH_SHORT).show();
        }

    } */

    private void setUpValues() {
        values.clear();
        values.add("Sensors");
        values.add("Sinusodial");
        String[] fileNames = getFilesDir().list();
        Pattern pattern = Pattern.compile("[_A-Za-z0-9-]+\\.earthquake");
        Matcher matcher;

        for (String str : fileNames) {
            matcher = pattern.matcher(str);
            if (matcher.matches()) values.add(str.substring(0, str.length() - 11));
        }

        adapter.notifyDataSetChanged();
    }
}
