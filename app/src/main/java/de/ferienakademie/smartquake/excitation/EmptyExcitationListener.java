package de.ferienakademie.smartquake.excitation;

/**
 * Created by simon on 23.09.16.
 */

/**
 * THis class is used as a dummy to avoid NullPointer checks
 */
public class EmptyExcitationListener implements  ExcitationListener{
    private static EmptyExcitationListener ourInstance = new EmptyExcitationListener();

    public static EmptyExcitationListener getInstance() {
        return ourInstance;
    }

    private EmptyExcitationListener() {
    }

    @Override
    public void excited(AccelData reading) {
        //does nothing
    }
}
