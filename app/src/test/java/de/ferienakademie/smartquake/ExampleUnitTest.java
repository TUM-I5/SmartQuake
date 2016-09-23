package de.ferienakademie.smartquake;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sub_isCorrect() throws Exception {
        assertEquals(4, 8 - 4);
    }

    @Test(expected = NullPointerException.class)
    public void expectNullPointer() throws Exception {
        String s = null;
        int length = s.length();
    }
}