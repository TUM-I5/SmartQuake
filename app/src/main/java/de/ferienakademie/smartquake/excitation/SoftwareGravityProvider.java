package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 26.09.16.
 */

/**
 * uses only software emulation to get the current gravity-values
 */
public class SoftwareGravityProvider extends GravityProvider{
    private double lastXGravity = 0;
    private double lastYGravity = 9.81;
    private final double alpha = 0.8;

    /**
     * fills in gravity properties with acceleration passed through low-frequency filter
     * @param data datastrucure with acceleretation along X,Y axis already provided
     */
    @Override
    public void getGravity(AccelData data) {
        lastXGravity = alpha * lastXGravity + (1 - alpha) * data.xAcceleration;
        lastYGravity = alpha * lastYGravity + (1 - alpha) * data.yAcceleration;

        data.xGravity = lastXGravity;
        data.yGravity = lastYGravity;
        data.xAcceleration = data.xAcceleration - lastXGravity;
        data.yAcceleration = data.yAcceleration - lastYGravity;
    }

    @Override
    public void init(double timestep) {

    }

    @Override
    public void setActive() {
        //noop
    }

    @Override
    public void setInactive() {
        //noop
    }
}
