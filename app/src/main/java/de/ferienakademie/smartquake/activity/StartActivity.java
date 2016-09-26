package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ferienakademie.smartquake.R;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private int mPosition = ListView.INVALID_POSITION;

    private List<String> values = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "You decided to draw the new model on your own", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(StartActivity.this, CreateActivity.class));
            }
        });

        values = new ArrayList<>();
        values.add("Simple Beam");
        values.add("Simple House");
        values.add("Crane");
        values.add("Long Frame");

        String[] structures = getFilesDir().list();

        Pattern pattern = Pattern.compile("[_A-Za-z0-9-]+\\.structure");
        Matcher matcher;

        for (String str : structures) {
            matcher = pattern.matcher(str);
            if (matcher.matches()) values.add(str.substring(0, str.length() - 10));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_start_activity, R.id.list_item_date_textview, values);

        PredefinedAdapter mPredefinedAdapter = new PredefinedAdapter(this, null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView mListView = (ListView) findViewById(R.id.listview_predefined);
        //mListView.setAdapter(mPredefinedAdapter);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position
                /**Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    onItemSelected(position);
                }*/
                //String  itemValue    = (String) mListView.getItemAtPosition(position);
                onItemSelected(position);
                mPosition = position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            //TODO What happens when you want to play recorded quake data

        } else if (id == R.id.nav_manage) {
           //TODO What happens when you want tools
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onItemSelected(Integer id_of_predefined_model) {
        Intent intent = new Intent(this, SimulationActivity.class);
        intent.putExtra("id", id_of_predefined_model);
        intent.putExtra("name", values.get(id_of_predefined_model) + ".structure");
        startActivity(intent);
    }
}
