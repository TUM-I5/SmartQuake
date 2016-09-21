package de.ferienakademie.smartquake.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewTreeObserver;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * Created by yuriy on 18/09/16.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final CanvasView structure = (CanvasView) findViewById(R.id.shape);

        ViewTreeObserver viewTreeObserver = structure.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    structure.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    double width = structure.getWidth();
                    double height = structure.getHeight();
                    double middle = structure.getWidth() * 0.25f;

                    structure.emptyJoints();

                    structure.addJoint(new Beam(middle, height, width - middle, height));
                    structure.addJoint(new Beam(width - middle, height, width - middle, height - middle));
                    structure.addJoint(new Beam(width - middle, height - middle, middle, height - middle));
                    structure.addJoint(new Beam(middle, height - middle, middle, height));
                    structure.addJoint(new Beam(middle, height - middle, 2*middle, height - 2*middle));
                    structure.addJoint(new Beam(2*middle, height - 2*middle, width - middle, height - middle));

                    structure.forceRedraw();
                }
            });
        }
    }

}
