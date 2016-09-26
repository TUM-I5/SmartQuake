package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 26.09.16.
 */
public abstract class GravityProvider {
    public abstract void getGravity(AccelData data);

    public abstract void init(double timestep);

    public abstract void setActive();

    public abstract void setInactive();

    public abstract void setBaseTime(long baseTime);
}
