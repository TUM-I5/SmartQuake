package de.ferienakademie.smartquake;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.view.ViewTreeObserver;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.ferienakademie.smartquake.activity.MainActivity;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void test_canvas_speed() {
        Activity activity = mActivityRule.getActivity();

        final CanvasView canvasView = (CanvasView) activity.findViewById(R.id.shape);

        int i = 1;

        while (i < 100) {

            addNewNodes(canvasView, i);
            addNewBeams(canvasView, i);

            i++;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    canvasView.drawStructure();
                }
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

    }

    public void addNewBeams(final CanvasView canvasView, final int step) {

        ViewTreeObserver viewTreeObserver = canvasView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    canvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    double width = canvasView.getWidth();
                    double height = canvasView.getHeight();
                    double middle = canvasView.getWidth() * 0.25f;

                    //drawing 100 vertical bars
                    for (int i = 0; i < 100; i++) {
                        canvasView.addBeam(new Beam(step*10, i*height/100, step*10, (i+1)*height/100));
                    }
                }
            });
        }

    }

    public void addNewNodes(CanvasView canvasView, int step) {

    }


}