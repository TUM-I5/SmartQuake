package de.ferienakademie.smartquake.model;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.ferienakademie.smartquake.excitation.StructureIO;
import de.ferienakademie.smartquake.kernel1.SpatialDiscretization;

public class StructureFactory {
    public static Structure cantileverBeam() {
        List<Integer> dofNode1 = new LinkedList<>();
        List<Integer> dofNode2 = new LinkedList<>();


        Node bottom = new Node(4, 8);
        Node up = new Node(4, 0);

        List<Integer> condof = new ArrayList<>();

        bottom.setSingleConstraint(0,true);
        bottom.setSingleConstraint(1,true);
        bottom.setSingleConstraint(2,true);


        Material testMaterial = Material.STEEL;

        Beam b = new Beam(bottom, up, testMaterial);

        Structure structure =  new Structure(Arrays.asList(bottom, up), Arrays.asList(b), condof);
        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getSimpleHouse() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        //Kernel1 stuff


        Node n1 = new Node(0, height);
        Node n2 = new Node(width, height);
        Node n3 = new Node(width, height - half);
        Node n4 = new Node(0, height - half);
        Node n5 = new Node(half, height - 2 * half);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n4, testMaterial);
        Beam b4 = new Beam(n4, n1, testMaterial);
        Beam b5 = new Beam(n4, n5, testMaterial);
        Beam b6 = new Beam(n5, n3, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5);
        structure.addBeams(b1, b2, b3, b4, b5, b6);


        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        //TODO: what does this do? Didn't you mean n1.setHinge(true); ?
        n1.isHinge();

        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }


    public static Structure getCraneBottom() {

        double width = 8;
        double height = 24;

        boolean lumped = true;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        //Kernel1 stuff


        Node n1 = new Node(0, height);
        Node n2 = new Node(width, height);
        Node n3 = new Node(0, height - width);
        Node n4 = new Node(width, height - width);
        Node n5 = new Node(0, height - 2 * width);
        Node n6 = new Node(width, height - 2 * width);
        Node n7 = new Node(0, height - 3 * width);
        Node n8 = new Node(width, height - 3 * width);

        Beam b1 = new Beam(n1, n3, testMaterial);
        Beam b2 = new Beam(n3, n5, testMaterial);
        Beam b3 = new Beam(n5, n7, testMaterial);
        Beam b4 = new Beam(n7, n8, testMaterial);
        Beam b5 = new Beam(n8, n6, testMaterial);
        Beam b6 = new Beam(n6, n4, testMaterial);
        Beam b7 = new Beam(n4, n2, testMaterial);
        Beam b8 = new Beam(n2, n3, testMaterial);
        Beam b9 = new Beam(n4, n5, testMaterial);
        Beam b10 = new Beam(n6, n7, testMaterial);
        Beam b11 = new Beam(n3, n4, testMaterial);
        Beam b12 = new Beam(n5, n6, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        //TODO: what does this do? Didn't you mean n1.setHinge(true); ?
        n1.isHinge();

        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;

    }

    public static Structure getBetterEiffelTower() {
        double a = 1;
        double width = 6 * a;
        double height = 16 * a;

        boolean lumped = true;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        //Kernel1 stuff


        Node n1 = new Node(0, height);
        Node n2 = new Node(a, height);
        Node n3 = new Node(width - a, height);
        Node n4 = new Node(width, height);
        Node n5 = new Node(a, height - 2 * a);
        Node n6 = new Node(2 * a, height - 2 * a);
        Node n7 = new Node(width - 2 * a, height - 2 * a);
        Node n8 = new Node(width - a, height - 2 * a);
        Node n9 = new Node(a + a/2., height - 3 * a);
        Node n10 = new Node(2 * a + a/2., height - 3 * a);
        Node n11 = new Node(width - 2 * a - a/2., height - 3 * a);
        Node n12 = new Node(width - a - a/2., height - 3 * a);
        Node n13 = new Node(2 * a, height - 6 * a);
        Node n14 = new Node(2 * a + 2 * a/3., height - 6 * a);
        Node n15 = new Node(2 * a + 2 * a/3. + 2 * a/3., height - 6 * a);
        Node n16 = new Node(2 * a + 2 * a/3. + 2 * a/3. + 2 * a/3., height - 6 * a);
        Node n17 = new Node(2 * a + a/2., height - 13 * a);
        Node n18 = new Node(2 * a + a/2. + a/3., height - 13 * a);
        Node n19 = new Node(2 * a + a/2. + a/3. + a/3., height - 13 * a);
        Node n20 = new Node(2 * a + a/2. + a, height - 13 * a);
        Node n21 = new Node(2 * a + a/2. - a/3., height - 13 * a - a/3.);
        Node n22 = new Node(2 * a + a/2. + a + a/3., height - 13 * a - a/3.);
        Node n23 = new Node(3 * a, height - 15 * a);
        Node n24 = new Node(3 * a, 0);


        Beam b1 = new Beam(n1, n5, testMaterial);
        Beam b2 = new Beam(n2, n6, testMaterial);
        Beam b3 = new Beam(n3, n7, testMaterial);
        Beam b4 = new Beam(n4, n8, testMaterial);
        Beam b5 = new Beam(n5, n6, testMaterial);
        Beam b6 = new Beam(n6, n7, testMaterial);
        Beam b7 = new Beam(n7, n8, testMaterial);
        Beam b8 = new Beam(n5, n9, testMaterial);
        Beam b9 = new Beam(n6, n10, testMaterial);
        Beam b10 = new Beam(n7, n11, testMaterial);
        Beam b11 = new Beam(n8, n12, testMaterial);
        Beam b12 = new Beam(n9, n10, testMaterial);
        Beam b13 = new Beam(n10, n11, testMaterial);
        Beam b14 = new Beam(n11, n12, testMaterial);
        Beam b15 = new Beam(n9, n13, testMaterial);
        Beam b16 = new Beam(n10, n14, testMaterial);
        Beam b17 = new Beam(n11, n15, testMaterial);
        Beam b18 = new Beam(n12, n16, testMaterial);
        Beam b19 = new Beam(n13, n14, testMaterial);
        Beam b20 = new Beam(n14, n15, testMaterial);
        Beam b21 = new Beam(n15, n16, testMaterial);
        Beam b22 = new Beam(n13, n17, testMaterial);
        Beam b23 = new Beam(n14, n18, testMaterial);
        Beam b24 = new Beam(n15, n19, testMaterial);
        Beam b25 = new Beam(n16, n20, testMaterial);
        Beam b26 = new Beam(n17, n18, testMaterial);
        Beam b27 = new Beam(n18, n19, testMaterial);
        Beam b28 = new Beam(n19, n20, testMaterial);
        Beam b29 = new Beam(n17, n21, testMaterial);
        Beam b30 = new Beam(n20, n22, testMaterial);
        Beam b31 = new Beam(n21, n23, testMaterial);
        Beam b32 = new Beam(n22, n23, testMaterial);
        Beam b33 = new Beam(n23, n24, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33);


        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        //TODO: what does this do? Didn't you mean n1.setHinge(true); ?
        n1.isHinge();

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }
    public static Structure getEmpireState() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;


        Node n1 = new Node(0.000000, 18.000000);
        Node n2 = new Node(1.000000, 18.000000);
        Node n3 = new Node(7.000000, 18.000000);
        Node n4 = new Node(8.000000, 18.000000);
        Node n5 = new Node(0.000000, 17.000000);
        Node n6 = new Node(1.000000, 17.000000);
        Node n7 = new Node(7.000000, 17.000000);
        Node n8 = new Node(8.000000, 17.000000);
        Node n9 = new Node(1.000000, 14.000000);
        Node n10 = new Node(2.000000, 14.000000);
        Node n11 = new Node(6.000000, 14.000000);
        Node n12 = new Node(7.000000, 14.000000);
        Node n13 = new Node(2.000000, 5.000000);
        Node n14 = new Node(3.000000, 5.000000);
        Node n15 = new Node(5.000000, 5.000000);
        Node n16 = new Node(6.000000, 5.000000);
        Node n17 = new Node(3.000000, 3.000000);
        Node n18 = new Node(3.500000, 3.000000);
        Node n19 = new Node(4.500000, 3.000000);
        Node n20 = new Node(5.000000, 3.000000);
        Node n21 = new Node(3.500000, 2.000000);
        Node n22 = new Node(4.000000, 2.000000);
        Node n23 = new Node(4.500000, 2.000000);
        Node n24 = new Node(4.000000, 0.000000);


        Beam b1 = new Beam(n1, n5, testMaterial);
        Beam b2 = new Beam(n2, n6, testMaterial);
        Beam b3 = new Beam(n3, n7, testMaterial);
        Beam b4 = new Beam(n4, n8, testMaterial);
        Beam b5 = new Beam(n5, n6, testMaterial);
        Beam b6 = new Beam(n7, n8, testMaterial);
        Beam b7 = new Beam(n6, n9, testMaterial);
        Beam b8 = new Beam(n7, n12, testMaterial);
        Beam b9 = new Beam(n9, n10, testMaterial);
        Beam b10 = new Beam(n10, n11, testMaterial);
        Beam b11 = new Beam(n11, n12, testMaterial);
        Beam b12 = new Beam(n10, n13, testMaterial);
        Beam b13 = new Beam(n11, n16, testMaterial);
        Beam b14 = new Beam(n13, n14, testMaterial);
        Beam b15 = new Beam(n14, n15, testMaterial);
        Beam b16 = new Beam(n15, n16, testMaterial);
        Beam b17 = new Beam(n14, n17, testMaterial);
        Beam b18 = new Beam(n15, n20, testMaterial);
        Beam b19 = new Beam(n17, n18, testMaterial);
        Beam b20 = new Beam(n18, n19, testMaterial);
        Beam b21 = new Beam(n19, n20, testMaterial);
        Beam b22 = new Beam(n18, n21, testMaterial);
        Beam b23 = new Beam(n19, n23, testMaterial);
        Beam b24 = new Beam(n21, n22, testMaterial);
        Beam b25 = new Beam(n22, n23, testMaterial);
        Beam b26 = new Beam(n22, n24, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        //TODO: what does this do? Didn't you mean n1.setHinge(true); ?
        n1.isHinge();

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        enumerateDOFs(structure);
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
        enumerateDOFs(structure);
        return structure;
    }


    public static Structure getStructure(Context context, String structureName) {

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = context.openFileInput(structureName);

            Structure structure = StructureIO.readStructure(fileInputStream);
            enumerateDOFs(structure);
            return structure;

        } catch (FileNotFoundException e) {
            //TODO: handle exception in the calling function and make appropriate actions.
            Log.e(StructureFactory.class.toString(), "FileNotFound");
        } catch (IOException e) {
            Log.e(StructureFactory.class.toString(), "IOException");
        }

        return new Structure();
    }


    public static void enumerateDOFs(Structure structure){
        int numberofDOF=0;
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);

            List<Integer>  dofs = new ArrayList<>();

            // dof for x direction
            dofs.add(numberofDOF);
            if (node.getConstraint(0)){
                structure.addSingleConDOF(numberofDOF);
            }
            numberofDOF++;

            // dof for y direction
            dofs.add(numberofDOF);
            if (node.getConstraint(1)){
                structure.addSingleConDOF(numberofDOF);
            }
            numberofDOF++;

            if (node.isHinge()){
                List<Beam> beams = node.getBeams();
                for (int j = 0; j < beams.size(); j++) {
                    Beam beam = beams.get(i);

                    // dof for rotation of this beam
                    dofs.add(numberofDOF);

                    if(beam.getStartNode()==node){
                        beam.setSingleDof(0,dofs.get(0));
                        beam.setSingleDof(1,dofs.get(1));
                        beam.setSingleDof(2,numberofDOF);
                    }
                    else {
                        beam.setSingleDof(3,dofs.get(0));
                        beam.setSingleDof(4,dofs.get(1));
                        beam.setSingleDof(5, numberofDOF);
                    }
                    numberofDOF++;

                }
            }
            // rigid connection
            else {
                List<Beam> beams = node.getBeams();

                // dof for rotation of all beams
                dofs.add(numberofDOF);

                if (node.getConstraint(2)){
                    structure.addSingleConDOF(numberofDOF);
                }

                for (int j = 0; j < beams.size(); j++) {
                    Beam beam = beams.get(j);

                    if(beam.getStartNode()==node){
                        beam.setSingleDof(0,dofs.get(0));
                        beam.setSingleDof(1,dofs.get(1));
                        beam.setSingleDof(2,numberofDOF);
                    }
                    else {
                        beam.setSingleDof(3,dofs.get(0));
                        beam.setSingleDof(4,dofs.get(1));
                        beam.setSingleDof(5, numberofDOF);
                    }

                }
                numberofDOF++;

            }

            // dofs am Knoten setzen
            node.setDOF(dofs);
            structure.setNumberOfDOF(numberofDOF);
        }

    }

}
