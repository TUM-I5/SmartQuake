// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.activity.earthquakeselect.EarthQuakeAssetListEntry;
import de.ferienakademie.smartquake.activity.earthquakeselect.EarthQuakeListEntry;
import de.ferienakademie.smartquake.activity.earthquakeselect.EarthQuakeFileListEntry;
import de.ferienakademie.smartquake.activity.earthquakeselect.EarthQuakeSensorListEntry;
import de.ferienakademie.smartquake.activity.earthquakeselect.EarthQuakeSinusoidalListEntry;
import de.ferienakademie.smartquake.util.FileMatching;


public class ChooseEarthQuakeDataActivity extends AppCompatActivity {

    private List<EarthQuakeListEntry> values;

    private ArrayAdapter<EarthQuakeListEntry> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_data);

        values = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.list_view_eq_data);
        registerForContextMenu( lv);
        adapter = new ArrayAdapter<>(this, R.layout.list_item_eq_data, R.id.list_item_eq_data_text, values);
        setUpValues();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onItemSelected(position);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent sel = new Intent();
        setResult(Activity.RESULT_CANCELED, sel);
        finish();
    }


    public void onItemSelected(Integer dataSourceId) {
        Intent sel = new Intent();
        sel.putExtra("eqDataEntry", values.get(dataSourceId));
        setResult(Activity.RESULT_OK, sel);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    }

    private void setUpValues() {
        values.clear();
        values.add(new EarthQuakeSensorListEntry());
        values.add(new EarthQuakeSinusoidalListEntry());

        String[] fileNames = getFilesDir().list();

        for (String fileName : fileNames) {
            if (FileMatching.matchesEarthQuakeFileName(fileName)) {
                values.add(new EarthQuakeFileListEntry(fileName));
            }
        }

        try {
            String[] assetFiles = getAssets().list("");
            for (String assetFilename : assetFiles) {
                if (FileMatching.matchesEarthQuakeFileName(assetFilename)) {
                    values.add(new EarthQuakeAssetListEntry(assetFilename));
                }
            }
        } catch (IOException e) {
            Log.e("CHOOSE EQ ASSETS", "could not load assets", e);
        }

        adapter.notifyDataSetChanged();
    }
}