package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StructureFactory {
    public static Structure getSimpleHouse() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        List<Double> unode1 = new LinkedList<>();
        unode1.add(0.0);
        unode1.add(0.0);
        unode1.add(0.0);

        Structure structure = new Structure();
        Material testMaterial = new Material();

        //Kernel1 stuff
        //TODO Alex: redesign with ID Matrix
        //TODO Paul: setConstraint(true) for fixed nodes
        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();
        List<Integer> DOFnode5 = new LinkedList<>();

        DOFnode1.add(0); //constraint
        DOFnode1.add(1);//constraint
        DOFnode1.add(2);//constraint

        DOFnode2.add(3);//constraint
        DOFnode2.add(4);//constraint
        DOFnode2.add(5);//constraint

        DOFnode3.add(6);
        DOFnode3.add(7);
        DOFnode3.add(8);

        DOFnode4.add(9);
        DOFnode4.add(10);
        DOFnode4.add(11);
        DOFnode5.add(12);
        DOFnode5.add(13);
        DOFnode5.add(14);

        Node n1 = new Node(0, height);
        Node n2 = new Node(width, height);
        Node n3 = new Node(width, height - half);
        Node n4 = new Node(0, height - half);
        Node n5 = new Node(half, height - 2 * half);

        Beam b1 = new Beam(n1, n2, testMaterial,true);
        Beam b2 = new Beam(n2, n3, testMaterial,true);
        Beam b3 = new Beam(n3, n4, testMaterial,true);
        Beam b4 = new Beam(n4, n1, testMaterial,true);
        Beam b5 = new Beam(n4, n5, testMaterial,true);
        Beam b6 = new Beam(n5, n3, testMaterial,true);

        structure.addNodes(n1, n2, n3, n4, n5);
        structure.addBeams(b1, b2, b3, b4, b5, b6);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            condof.add(i);
        }

        structure.setConDOF(condof);

        return structure;
    }

    public static Structure getSimpleEiffelTower() {
        double width = 8;
        double height = 16;

        double half = width * 0.5;
        double quarter = width * 0.25;
        double eighth = width * 0.125;
        double sixteenth = width * 1d/16;

        Structure structure = new Structure();

        Node tri00 = new Node(half, height);
        Node tri01 = new Node(eighth, height);
        Node tri02 = new Node((half + eighth)/2, height/2);
        structure.addBeams(BeamFactory.createTriangleShapedBeam(tri00, tri01, tri02));

        Node tri11 = new Node(width - eighth, height);
        Node tri12 = new Node(width - (half + eighth)/2, height/2);
        structure.addBeams(BeamFactory.createTriangleShapedBeam(tri00, tri11, tri12));

        Node tri22 = new Node(half, height/4);

        structure.addBeams(BeamFactory.createTriangleShapedBeam(tri02, tri12, tri22));

        structure.addNodes(tri00, tri01, tri11, tri12, tri02, tri22);
        return structure;
    }
}
