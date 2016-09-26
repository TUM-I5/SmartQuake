package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 26.09.16.
 */

/**
 * uses only softwareemulation to get the current gravity-values
 */
public class SoftwareGravityProvider extends GravityProvider{
    private double lastXGravity = 0;
    private double lastYGravity = 9.81;
    private final double alpha = 0.8;

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
    public void init() {

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
