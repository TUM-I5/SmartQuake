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


        Material testMaterial = new Material();

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
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        //Kernel1 stuff


        Node n1 = new Node(0, height);
        Node n2 = new Node(width, height);
        Node n3 = new Node(width, height - half);
        Node n4 = new Node(0, height - half);
        Node n5 = new Node(half, height - 2 * half);

        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n4, testMaterial);
        Beam b4 = new Beam(n4, n1, testMaterial);
        Beam b5 = new Beam(n4, n5, testMaterial);
        Beam b6 = new Beam(n5, n3, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5);
        structure.addBeams( b2, b3, b4, b5, b6);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);
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

    public static Structure getWeirdBridge() {
        double width = 8;
        double height = 8;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        Node g1 = new Node(width/4, height);
        Node g2 = new Node(width/2, height);
        Node g3 = new Node(3*width/4, height);
        Node s1 = new Node(0, height/2);
        Node s2 = new Node(width/4, height/2);
        Node s3 = new Node(width/2, height/2);
        Node s4 = new Node(3*width/4, height/2);
        Node s5 = new Node(width, height/2);
        Node t1 = new Node(width/4, 0);
        Node t2 = new Node(width/2, 0);
        Node t3 = new Node(3*width/4, 0);

        Beam c1 = new Beam(g1, t1, testMaterial);
        Beam c2 = new Beam(g2, t2, testMaterial);
        Beam c3 = new Beam(g3, t3, testMaterial);
        Beam sb1 = new Beam(s1, s2, testMaterial);
        Beam sb2 = new Beam(s2, s3, testMaterial);
        Beam sb3 = new Beam(s3, s4, testMaterial);
        Beam sb4 = new Beam(s4, s5, testMaterial);
        Beam h1 = new Beam(t1, s2, testMaterial);
        Beam h2 = new Beam(t2, s3, testMaterial);
        Beam h3 = new Beam(t3, s4, testMaterial);

        structure.addNodes(g1,g2,g3,s1,s2,s3,s4,s5,t1,t2,t3);
        structure.addBeams(c1,c2,c3,sb1,sb2,sb3,sb4,h1,h2,h3);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        g1.setConstraint(con);
        g2.setConstraint(con);
        g3.setConstraint(con);
        t1.setHinge(true);
        t2.setHinge(true);
        t3.setHinge(true);
        //TODO: Make s2/s3/s4 hinges between h1 and the group sb1,sb2/between h2 and the group sb2,sb3/between h3 and the group sb3/sb4
        //In the current implementation of hinges, each beam makes up one group
        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getHousingBlock() {
        double width = 10;
        double height = 10;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        Node n1 = new Node(0, height);
        Node n2 = new Node(width/4, height);
        Node n3 = new Node(width/2, height);
        Node n4 = new Node(3*width/4, height);
        Node n5 = new Node(width, height);
        Node n6 = new Node(0, height);
        Node n7 = new Node(width/4, 3*height/4);
        Node n8 = new Node(width/2, 3*height/4);
        Node n9 = new Node(3*width/4, 3*height/4);
        Node n10 = new Node(width, 3*height/4);
        Node n11 = new Node(0, height/2);
        Node n12 = new Node(width/4, height/2);
        Node n13 = new Node(width/2, height/2);
        Node n14 = new Node(3*width/4, height/2);
        Node n15 = new Node(width, height/2);
        Node n16 = new Node(width/4, height/4);
        Node n17 = new Node(width/2, height/4);
        Node n18 = new Node(3*width/4, height/4);
        Node n19 = new Node(width/2, 0);

        Beam b1 = new Beam(n1, n6, testMaterial);
        Beam b2 = new Beam(n2, n7, testMaterial);
        Beam b3 = new Beam(n3, n8, testMaterial);
        Beam b4 = new Beam(n4, n9, testMaterial);
        Beam b5 = new Beam(n5, n10, testMaterial);
        Beam b6 = new Beam(n6, n7, testMaterial);
        Beam b7 = new Beam(n7, n8, testMaterial);
        Beam b8 = new Beam(n8, n9, testMaterial);
        Beam b9 = new Beam(n9, n10, testMaterial);
        Beam b10 = new Beam(n6, n11, testMaterial);
        Beam b11 = new Beam(n7, n12, testMaterial);
        Beam b12 = new Beam(n8, n13, testMaterial);
        Beam b13 = new Beam(n9, n14, testMaterial);
        Beam b14 = new Beam(n10, n15, testMaterial);
        Beam b15 = new Beam(n11, n16, testMaterial);
        Beam b16 = new Beam(n12, n16, testMaterial);
        Beam b17 = new Beam(n13, n17, testMaterial);
        Beam b18 = new Beam(n14, n18, testMaterial);
        Beam b19 = new Beam(n15, n18, testMaterial);
        Beam b20 = new Beam(n16, n19, testMaterial);
        Beam b21 = new Beam(n17, n19, testMaterial);
        Beam b22 = new Beam(n18, n19, testMaterial);


        structure.addNodes(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19);
        structure.addBeams(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        n5.setConstraint(con);
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
