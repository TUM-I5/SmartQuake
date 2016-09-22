package de.ferienakademie.smartquake;

import android.util.Log;

import de.ferienakademie.smartquake.kernel1.Kernel1;
import de.ferienakademie.smartquake.kernel2.TimeIntegration;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Class that wires everything together.
 */
public class Simulation {

    Kernel1 kernel1;
    TimeIntegration kernel2;
    CanvasView view;
    SimulationProgressListener listener;
    boolean isRunning;

    public Simulation(Kernel1 kernel1, TimeIntegration kernel2, CanvasView view) {
        this.kernel1 = kernel1;
        this.kernel2 = kernel2;
        this.view = view;
    }

    public void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                kernel2.prepareSimulation();
                for (int i = 0; i < 400; i++) {
                    if (!isRunning) {
                        break;
                    }
                    kernel2.performSimulationStep();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ex) {
                        Log.e("Simulation", ex.getMessage());
                        continue;
                    }
                    while(view.isBeingDrawn) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            Log.e("Simulation", ex.getMessage());
                        }
                    }
                    if (kernel2.isRunning()) {
                        Log.e("Simulation", "Kernel2 can not catch up the gui");
                    }
                    DrawHelper.drawStructure(kernel1.getStructure(), view);
                }
                if (listener != null) {
                    listener.onFinish();
                }
                kernel2.stop();
            }

        }).start();
    }

    /**
     * Stop simulation. This will also kill all background threads.
     */
    public void stop() {
        isRunning = false;
    }

    public void setListener(SimulationProgressListener listener) {
        this.listener = listener;
    }

    public interface SimulationProgressListener {

        /**
         * Is called after simulation finishes. Is called from background thread.
         */
        void onFinish();
    }

}
