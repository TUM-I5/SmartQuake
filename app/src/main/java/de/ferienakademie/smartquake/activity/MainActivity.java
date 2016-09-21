package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewTreeObserver;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.excitation.ExcitationManager;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    Sensor mAccelerometer; //sensor object
    SensorManager mSensorManager; // manager to subscribe for sensor events
    ExcitationManager mExcitationManager; // custom accelerometer listener

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        final CanvasView canvasView = (CanvasView) findViewById(R.id.shape);

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    canvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    double width = canvasView.getWidth();
                    double height = canvasView.getHeight();
                    double middle = canvasView.getWidth() * 0.25f;

                    Structure structure = new Structure();

                    Node n1 = new Node(middle, height);
                    Node n2 = new Node(width - middle, height);
                    Node n3 = new Node(width - middle, height - middle);
                    Node n4 = new Node(middle, height - middle);
                    Node n5 = new Node(2 * middle, height - 2 * middle);

                    Beam b1 = new Beam(n1, n2);
                    Beam b2 = new Beam(n2, n3);
                    Beam b3 = new Beam(n3, n4);
                    Beam b4 = new Beam(n4, n1);
                    Beam b5 = new Beam(n4, n5);
                    Beam b6 = new Beam(n5, n3);

                    structure.addNodes(n1, n2, n3, n4, n5);
                    structure.addBeams(b1, b2, b3, b4, b5, b6);

                    canvasView.drawStructure(structure);
                }
            });
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        mSensorManager.registerListener(mExcitationManager, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI); //subscribe for sensor events
    }

    @Override
    public void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(mExcitationManager);// do not receive updates when paused
    }

}
