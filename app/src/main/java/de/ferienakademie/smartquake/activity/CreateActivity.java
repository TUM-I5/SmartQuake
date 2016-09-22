package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.os.Bundle;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Created by yuriy on 22/09/16.
 */
public class CreateActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        CanvasView canvasView = (CanvasView) findViewById(R.id.shape);
        DrawHelper.clearCanvas(canvasView);
    }

}
