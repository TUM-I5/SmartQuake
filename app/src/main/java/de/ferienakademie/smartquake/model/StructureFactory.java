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
