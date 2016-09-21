package de.ferienakademie.smartquake.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * Created by yuriy on 18/09/16.
 */
public class MainActivity extends Activity {

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
                }
            });
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int i = 1;
                            while (i < 100) {
                                structure.emptyBeams();
                                addNewNodes(structure, i);
                                addNewBeams(structure, i);

                                i++;

                                structure.postInvalidate();

                                try {
                                    Thread.sleep(50);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

            }
        });
    }


    public void addNewBeams(final CanvasView canvasView, final int step) {

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();

        final double width = canvasView.getWidth();
        final double height = canvasView.getHeight();
        final double middle = canvasView.getWidth() * 0.25f;


        for (int i = 0; i < 100; i++) {
            canvasView.addBeam(new Beam(step * 10, i * height / 100, step * 10, (i + 1) * height / 100));
            canvasView.addBeam(new Beam(i*width/100, step * 10, (i + 1) * width/100, step * 10));
        }

    }

    public void addNewNodes(CanvasView canvasView, int step) {

    }

}