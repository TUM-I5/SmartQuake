package de.ferienakademie.smartquake.excitation;

/**
 * Created by Maximilian Berger on 9/26/16.
 */
public interface AccelerationProviderObserver {
    void onNewAccelerationValue(AccelData data);

    void onNewReplayPercent(double percent);
}
