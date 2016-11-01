// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.ferienakademie.smartquake.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Maximilian Berger on 9/29/16.
 */
public class FileMatching {
    private static Pattern earthquakeFileNamePattern = Pattern.compile("[ _A-Za-z0-9.-]+\\.earthquake");
    private static Pattern structureFileNamePattern = Pattern.compile("[ _A-Za-z0-9.-]+\\.structure");

    public static boolean matchesEarthQuakeFileName(String filename) {
        Matcher matcher = earthquakeFileNamePattern.matcher(filename);
        return matcher.matches();
    }

    public static boolean matchesStructureFileName(String filename) {
        Matcher matcher = structureFileNamePattern.matcher(filename);
        return matcher.matches();
    }
}
