package de.ferienakademie.smartquake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.ferienakademie.smartquake.excitation.ExcitationManager;

public class MainActivity extends AppCompatActivity{

    Sensor mAccelerometer; //sensor object
    SensorManager mSensorManager; // manager to subscribe for sensor events
    ExcitationManager mExcitationManager; // custom accelerometer listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
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
