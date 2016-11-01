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

package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.List;


public class BeamFactory {
    public static List<Beam> createTriangleShapedBeam(Node tri0, Node tri1, Node tri2) {
        List<Beam> beams = new ArrayList<>();
        beams.add(new Beam(tri0, tri1));
        beams.add(new Beam(tri1, tri2));
        beams.add(new Beam(tri2, tri0));

        return beams;
    }
}
