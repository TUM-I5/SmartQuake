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
                            boolean backwards = false;

                            while (true) {
                                structure.emptyBeams();
                                addNewNodes(structure, i);
                                addNewBeams(structure, i);

                                if (!backwards) i++;
                                else if (backwards) i--;

                                if (i >= 1000) backwards = true;
                                if (i <= 0) backwards = false;

                                while (structure.isBeingDrawn)
                                    try {
                                        Thread.sleep(0);
                                    } catch (InterruptedException e) { e.printStackTrace(); }

                                structure.drawStructure();
/*
                                try {
                                    Thread.sleep(4);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
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


        for (int i = 0; i < 200; i++) {
            canvasView.addBeam(new Beam(step, (2*i+1) * height / 400, step, (2*i + 2) * height / 400));
            canvasView.addBeam(new Beam((2*i+1)*width/400, step, (2*i + 2) * width/400, step));

            canvasView.addBeam(new Beam(width - step, 2*i * height / 400, width - step, (2*i + 1) * height / 400));
            canvasView.addBeam(new Beam(2*i*width/400, height - step, (2*i + 1) * width/400, height - step));
        }

    }

    public void addNewNodes(CanvasView canvasView, int step) {

    }

}