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
