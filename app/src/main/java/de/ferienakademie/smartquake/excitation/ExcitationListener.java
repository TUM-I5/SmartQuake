package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 22.09.16.
 */
public interface ExcitationListener {
    /**
     * Gets called when Excitation data is read
     *
     * @param reading the Excitation data
     */
    void excited(AccelData reading);
}
