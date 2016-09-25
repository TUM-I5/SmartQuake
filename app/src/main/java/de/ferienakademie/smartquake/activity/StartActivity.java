package de.ferienakademie.smartquake.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import de.ferienakademie.smartquake.R;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private int mPosition = ListView.INVALID_POSITION;
    String[] values = new String[] { "Simple House",
            "Eiffel Tower", "Empire State", "Statue of Liberty", "Coloseum", "Sample 6", "Sample 7", "Sample 8", "Sample 9", "Sample 10", "Sample 11"
    };

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
            /***
             *Add here code for setting activity
             * startActivity(new Intent(this, SettingsActivity.class));
             return true;
             */
            //TODO setteings activity

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            //TODO What happens when you want to load

        } else if (id == R.id.nav_slideshow) {
            //TODO What happens when you want to play recorded quake data

        } else if (id == R.id.nav_manage) {
           //TODO What happens when you want tools
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *This should be changed!!!
     * */
    public void onItemSelected(Integer id_of_predefined_model) {
        if(id_of_predefined_model==0){
        Intent intent = new Intent(this, SimulationActivity.class);
        intent.putExtra("test", id_of_predefined_model);
        startActivity(intent);}
        else{
            // Show Alert if you pressed anything but simple house model
            Toast.makeText(getApplicationContext(),
                    "We are sorry but this model is still not defined" , Toast.LENGTH_SHORT)
                    .show();
        }
    }
}