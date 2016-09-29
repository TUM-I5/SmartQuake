package de.ferienakademie.smartquake.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maximilian Berger on 9/29/16.
 */
public class FileMatching {
    private static Pattern earthquakeFileNamePattern = Pattern.compile("[ _A-Za-z0-9-]+\\.earthquake");

    public static boolean matchesEarthQuakeFileName(String filename) {
        Matcher matcher = earthquakeFileNamePattern.matcher(filename);
        return matcher.matches();
    }

    public static boolean matchesStructureFileName(String filename) {
        Matcher matcher = earthquakeFileNamePattern.matcher(filename);
        return matcher.matches();
    }
}
