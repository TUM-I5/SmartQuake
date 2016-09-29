package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.util.FileMatching;

/**
 * Created by root on 26.09.16.
 */
public class EarthquakeDataStartActivity extends AppCompatActivity {

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
                if(!values.get(position).equals("Sensors") && !values.get(position).equals("Sinusodial") && !values.get(position).equals("Last")){
                Intent intent = new Intent(EarthquakeDataStartActivity.this, AccelerationDataActivity.class);
                intent.putExtra("eqDataFile", values.get(position));
                //setResult(Activity.RESULT_OK, intent);
                //finish();
                startActivity(intent);}
                else{
                    Toast.makeText(getApplicationContext(),
                            "Sorry, no acceleration data", Toast.LENGTH_SHORT).show();
                }
                mPosition = position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setUpValues() {
        values.clear();
        values.add("Sensors");
        values.add("Sinusodial");
        String[] fileNames = getFilesDir().list();

        for (String str : fileNames) {
            if (FileMatching.matchesEarthQuakeFileName(str)) values.add(str.substring(0, str.length() - 11));
        }

        adapter.notifyDataSetChanged();
    }
}
