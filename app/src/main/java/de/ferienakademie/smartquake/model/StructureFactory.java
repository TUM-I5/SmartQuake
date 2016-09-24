package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.ferienakademie.smartquake.kernel1.Testing;

public class StructureFactory {
    public static Structure getSimpleHouse() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;



        Structure cantilever = new Structure();
        Structure rotcantilever = new Structure();
        Structure structure = new Structure();

        Material testMaterial = new Material();

        //Kernel1 stuff

        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();


        DOFnode1.add(0); //constraint
        DOFnode1.add(1);//constraint
        DOFnode1.add(2);//constraint

        DOFnode2.add(3);
        DOFnode2.add(4);
        DOFnode2.add(5);

        Node n1 = new Node(0, height, DOFnode1);
        Node n2 = new Node(2, height, DOFnode2);

        //rotated cantilever node
        Node n3 = new Node(1,height - half, DOFnode2);

        Beam b1 = new Beam(n1, n2, testMaterial,true);
        Beam b2 = new Beam(n1, n3, testMaterial,true);



        cantilever.addNodes(n1, n2);
        cantilever.addBeams(b1);

        rotcantilever.addNodes(n1,n3);
        rotcantilever.addBeam(b2);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            condof.add(i);
        }

        cantilever.setConDOF(condof);
        rotcantilever.setConDOF(condof);

        Testing.cantiLeverStaticTest(cantilever);

        Testing.rotatedcantiLeverStaticTest(rotcantilever);

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
