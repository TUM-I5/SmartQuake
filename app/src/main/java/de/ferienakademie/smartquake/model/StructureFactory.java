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

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import de.ferienakademie.smartquake.excitation.StructureIO;


public class StructureFactory {


    public static Structure cantileverBeam() {


        Node bottom = new Node(4, 8,0.0);
        Node up = new Node(4, 3,0.0);
       // Material zeroDensity = Material.STEEL7;

        List<Integer> condof = new ArrayList<>();

        bottom.setSingleConstraint(0,true);
        bottom.setSingleConstraint(1,true);
        bottom.setSingleConstraint(2,true);



        Material testMaterial = Material.STEEL2;

        Beam b = new Beam(bottom, up, testMaterial);
        Structure structure =  new Structure(Arrays.asList(bottom, up), Arrays.asList(b), condof);

        enumerateDOFs(structure);
        return structure;
    }



    public static Structure getSimpleHouse() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0, height, false);
        Node n2 = new Node(width, height, false);
        Node n3 = new Node(width, height - half, false);
        Node n4 = new Node(0, height - half, false);
        Node n5 = new Node(half, height - 2 * half, false);

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

    public static Structure getPresentation_EX_w_TMD() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL8;
        Material zeroDensity = Material.STEEL7;


        Node n1 = new Node(0, height, false);
        Node n2 = new Node(width, height, false);
        Node n3 = new Node(width, height - 2*half, false);
        Node n4 = new Node(0, height - 2*half, false);
        Node n5 = new Node(width/2, height - 2*half, false);


        Node n6 = new Node(width/2, height - half+1, 6.6754785010);




        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n5, testMaterial);
        Beam b4 = new Beam(n5, n4, testMaterial);
        Beam b5 = new Beam(n4, n1, testMaterial);
        Beam b6 = new Beam(n5, n6, zeroDensity);


        structure.addNodes(n1, n2, n3, n4,n5,n6);
        structure.addBeams( b2, b3, b4,b5,b6);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getPresentation_EX_wo_TMD() {    //Where's the Node Mass in this example?
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL8;

        Node n1 = new Node(0, height, false);
        Node n2 = new Node(width, height, false);
        Node n3 = new Node(width, height - 2*half, false);
        Node n4 = new Node(0, height - 2*half, false);
        Node n5 = new Node(width/2, height - 2*half, false);

        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n5, testMaterial);
        Beam b4 = new Beam(n5, n4, testMaterial);
        Beam b5 = new Beam(n4, n1, testMaterial);

        structure.addNodes(n1, n2, n3, n4,n5);
        structure.addBeams( b2, b3, b4,b5);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);

        structure.setLumped(true);

        enumerateDOFs(structure);
        return structure;
    }

//conserved example
//         public static Structure getTunedMassExample2() {
//    double width = 8;
//    double height = 8;
//
//    double half = width * 0.5;
//
//    Structure structure = new Structure();
//    Material testMaterial = Material.STEEL5;
//
//    Node n1 = new Node(0, height, false);
//    Node n2 = new Node(width, height, false);
//    Node n3 = new Node(width, height - 2*half, false);
//    Node n4 = new Node(0, height - 2*half, false);
//    Node n5 = new Node(width/2, height - 2*half, false);
//
//    Beam b2 = new Beam(n2, n3, testMaterial);
//    Beam b3 = new Beam(n3, n5, testMaterial);
//    Beam b4 = new Beam(n5, n4, testMaterial);
//    Beam b5 = new Beam(n4, n1, testMaterial);
//
//    structure.addNodes(n1, n2, n3, n4,n5);
//    structure.addBeams( b2, b3, b4,b5);
//
//    boolean[] con = new boolean[3];
//    con[0]=true;
//    con[1]=true;
//    con[2]=true;
//
//    n1.setConstraint(con);
//    n2.setConstraint(con);
//    enumerateDOFs(structure);
//    return structure;
//}

    public static Structure getHouseWithMassDamper() {
        double width = 8;
        double height = 8;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0, height,false);
        Node n2 = new Node(width, height,false);
        Node n3 = new Node(width, height/2,false);
        Node n4 = new Node(0, height/2,false);
        Node n5 = new Node(width/2, 0,false);
        Node n6 = new Node(width/2, height*1/4,100);

        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n4, testMaterial);
        Beam b4 = new Beam(n4, n1, testMaterial);
        Beam b5 = new Beam(n4, n5, testMaterial);
        Beam b6 = new Beam(n5, n3, testMaterial);
        Beam b7 = new Beam(n5, n6, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5, n6);
        structure.addBeams( b2, b3, b4, b5, b6, b7);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;



        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getDemoTMD() {

        double f = 1; //Hz
        double M = 2364160.95165*1/(f*f); //kg, for l1=1.5m, 12=2m and STEEL

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        double w = 4; //width
        double h = 3; //height

        Node n1 = new Node(0, 0);
        Node n2 = new Node(0.5*w, 0);
        Node n3 = new Node(w, 0);
        Node n4 = new Node(0.5*w, 1.5, M);
        Node n5 = new Node(w, 4);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n2, n4, testMaterial);
        Beam b4 = new Beam(n5, n3, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5);
        structure.addBeams(b1, b2, b3, b4);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n3.setConstraint(con);
        n5.setConstraint(con);
        enumerateDOFs(structure);
        return structure;

    }

    /**
     * This structure is meant to be the first demo of an oscillating mass.
     * Choose Material STEELDEMO so that the relationship M*l=1/f^2 holds true
     * @return
     */
    public static Structure getPresOne() {

        double f = 1; //Hz
        double l1 = 3;
        double l2 = 1;
        double M1 = 1/(f*f*l1);
        double M2 = 1;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material demoMaterial = Material.STEELDEMO;

        Node n1 = new Node(0, 4);
        Node n2 = new Node(l1, 4, M1);
        //Node n3 = new Node(l2+l1, 0);

        Beam b1 = new Beam(n1, n2, demoMaterial);
        //Beam b2 = new Beam(n2, n3, demoMaterial);

        structure.addNodes(n1, n2);
        structure.addBeams(b1);

        boolean[] con1 = new boolean[3];
        con1[0]=true;
        con1[1]=true;
        con1[2]=true;
        boolean[] con2 = new boolean[3];
        con2[0]=false;
        con2[1]=true;
        con2[2]=false;
        n1.setConstraint(con1);
        n2.setConstraint(con2);
        //n3.setConstraint(con2);
        enumerateDOFs(structure);
        return structure;

    }

    /**
     * This structure is meant to be the second demo of an oscillating mass.
     * Choose Material STEELDEMO so that the relationship M*l=1/f^2 holds true.
     * Added a tuned mass damper
     * @return
     */
    public static Structure getPresTwo() {

        double f = 1; //Hz
        double l1 = 2;
        double l2 = 2;
        double M1 = 1/(f*f*l1); //for length oscillation
        double M2 = 1/(f*f*l2);

        boolean[] con1 = new boolean[3];
        con1[0]=true;
        con1[1]=true;
        con1[2]=true;
        boolean[] con2 = new boolean[3];
        con2[0]=false;
        con2[1]=true;
        con2[2]=false;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material demoMaterial = Material.STEELDEMO;

        Node n1 = new Node(0, 4);
        Node n2 = new Node(l1, 4, M1);
        Node n3 = new Node(l2+l1, 4, M2);

        Beam b1 = new Beam(n1, n2, demoMaterial);
        Beam b2 = new Beam(n2, n3, demoMaterial);

        structure.addNodes(n1, n2, n3);
        structure.addBeams(b1, b2);

        n1.setConstraint(con1);
        n2.setConstraint(con2);
        n3.setConstraint(con2);
        enumerateDOFs(structure);
        return structure;

    }

    /**
     * This structure is meant to be the fourth demo of an oscillating mass.
     * Choose Material STEELDEMO so that the relationship M2*l2=1/f^2 holds true.
     * Material STEELDEMO2 is chosen so that the relationship M1*l1^3=1/f^2 holds true.
     * @return
     */
    public static Structure getPresThree() {

        double f = 1; //Hz
        double l1 = 8;
        //double l2 = 1;
        double M1 = 1/(f*f*l1*l1*l1);
        //double M2 = 1/(f*f*l2);

        boolean[] con1 = new boolean[3];
        con1[0]=true;
        con1[1]=true;
        con1[2]=true;
        boolean[] con2 = new boolean[3];
        con2[0]=false;
        con2[1]=true;
        con2[2]=false;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        //Material demoMaterial = Material.STEELDEMO;
        Material demoMaterial2 = Material.STEELDEMO2;




        Node n1 = new Node(4, l1);
        Node n2 = new Node(4, 0, M1);
        //Node n3 = new Node(l2, 0, M2);

        Beam b1 = new Beam(n1, n2, demoMaterial2);
        //Beam b2 = new Beam(n2, n3, demoMaterial);

        structure.addNodes(n1, n2);
        structure.addBeams(b1);

        n1.setConstraint(con1);
        n2.setConstraint(con2);
        //n2.setHinge(true);
        //n3.setConstraint(con2);
        enumerateDOFs(structure);
        return structure;

    }

    /**
     * This structure is meant to be the fourth demo of an oscillating mass.
     * Choose Material STEELDEMO so that the relationship M2*l2=1/f^2 holds true.
     * Material STEELDEMO2 is chosen so that the relationship M1*l1^3=1/f^2 holds true.
     * Added a tuned mass damper
     * @return
     */
    public static Structure getPresFour() {

        double f = 1; //Hz
        double l1 = 8;
        double l2 = 4;
        double M1 = 1/(f*f*l1*l1*l1); //for bending oscillation
        double M2 = 1/(f*f*l1*l1*l1);

        boolean[] con1 = new boolean[3];
        con1[0]=true;
        con1[1]=true;
        con1[2]=true;
        boolean[] con2 = new boolean[3];
        con2[0]=false;
        con2[1]=true;
        con2[2]=false;

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material demoMaterial = Material.STEELDEMO;
        Material demoMaterial2 = Material.STEELDEMO2;




        Node n1 = new Node(4, l1);
        Node n2 = new Node(4, 0, M1);
        Node n3 = new Node(4, l2, M2);

        Beam b1 = new Beam(n1, n2, demoMaterial2);
        Beam b2 = new Beam(n2, n3, demoMaterial2);

        structure.addNodes(n1, n2, n3);
        structure.addBeams(b1, b2);

        n1.setConstraint(con1);
        //n2.setConstraint(con2);
        //n2.setHinge(true);
        n3.setConstraint(con2);
        enumerateDOFs(structure);
        return structure;

    }

    public static Structure getPresFive() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        double w = 4; //width
        double h = 3; //height

        Node n1 = new Node(0, 3*h);
        Node n2 = new Node(w, 3*h);
        Node n3 = new Node(0, 2*h);
        Node n4 = new Node(w, 2*h);
        Node n5 = new Node(0, h);
        Node n6 = new Node(w, h);
        Node n7 = new Node(0, 0);
        Node n8 = new Node(w, 0);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n3, n4, testMaterial);
        Beam b3 = new Beam(n5, n6, testMaterial);
        Beam b4 = new Beam(n7, n8, testMaterial);
        Beam b5 = new Beam(n1, n3, testMaterial);
        Beam b6 = new Beam(n2, n4, testMaterial);
        Beam b7 = new Beam(n3, n5, testMaterial);
        Beam b8 = new Beam(n4, n6, testMaterial);
        Beam b9 = new Beam(n5, n7, testMaterial);
        Beam b10 = new Beam(n6, n8, testMaterial);

        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;

    }

    public static Structure getPresSix() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;
        Material demoMaterial2 = Material.STEELDEMO2;

        double w = 4; //width
        double h = 3; //height
        double f = 0.60074819525495005; //Hz
        double l = 2;
        double M = 1/(f*f*l*l*l);

        Node n1 = new Node(0, 3*h);
        Node n2 = new Node(w, 3*h);
        Node n3 = new Node(0, 2*h);
        Node n4 = new Node(w, 2*h);
        Node n5 = new Node(0, h);
        Node n6 = new Node(w, h);
        Node n7 = new Node(0, 0);
        Node n8 = new Node(w, 0);
        Node n9 = new Node(w/2.0, 0);
        Node n10 = new Node(w/2.0, l, M);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n3, n4, testMaterial);
        Beam b3 = new Beam(n5, n6, testMaterial);
        Beam b4 = new Beam(n7, n9, testMaterial);
        Beam b5 = new Beam(n1, n3, testMaterial);
        Beam b6 = new Beam(n2, n4, testMaterial);
        Beam b7 = new Beam(n3, n5, testMaterial);
        Beam b8 = new Beam(n4, n6, testMaterial);
        Beam b9 = new Beam(n5, n7, testMaterial);
        Beam b10 = new Beam(n6, n8, testMaterial);
        Beam b11 = new Beam(n9, n8, testMaterial);
        Beam b12 = new Beam(n9, n10, demoMaterial2);

        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;

    }

    public static Structure getCraneBottom() {

        double width = 8;
        double height = 24;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;


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

        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;

    }

    public static Structure getBetterEiffelTower() {
        double a = 1;
        double width = 6 * a;
        double height = 16 * a;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;


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


        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }
    public static Structure getEmpireState() {


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

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }
    public static Structure getGoldenGate() {

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0.000000, 5.000000);
        Node n2 = new Node(2.000000, 5.000000);
        Node n3 = new Node(4.000000, 5.000000);
        Node n4 = new Node(5.000000, 5.000000);
        Node n5 = new Node(6.000000, 5.000000);
        Node n6 = new Node(7.000000, 5.000000);
        Node n7 = new Node(8.000000, 5.000000);
        Node n8 = new Node(9.000000, 5.000000);
        Node n9 = new Node(10.000000, 5.000000);
        Node n10 = new Node(12.000000, 5.000000);
        Node n11 = new Node(14.000000, 5.000000);
        Node n12 = new Node(15.000000, 5.000000);
        Node n13 = new Node(16.000000, 5.000000);
        Node n14 = new Node(17.000000, 5.000000);
        Node n15 = new Node(18.000000, 5.000000);
        Node n16 = new Node(19.000000, 5.000000);
        Node n17 = new Node(20.000000, 5.000000);
        Node n18 = new Node(22.000000, 5.000000);
        Node n19 = new Node(24.000000, 5.000000);
        Node n20 = new Node(7.000000, 8.000000);
        Node n21 = new Node(17.000000, 8.000000);
        Node n22 = new Node(4.000000, 4.000000);
        Node n23 = new Node(5.000000, 3.000000);
        Node n24 = new Node(6.000000, 2.000000);
        Node n25 = new Node(7.000000, 0.000000);
        Node n26 = new Node(8.000000, 2.000000);
        Node n27 = new Node(9.000000, 3.000000);
        Node n28 = new Node(10.000000, 4.000000);
        Node n29 = new Node(14.000000, 4.000000);
        Node n30 = new Node(15.000000, 3.000000);
        Node n31 = new Node(16.000000, 2.000000);
        Node n32 = new Node(17.000000, 0.000000);
        Node n33 = new Node(18.000000, 2.000000);
        Node n34 = new Node(19.000000, 3.000000);
        Node n35 = new Node(20.000000, 4.000000);


        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n4, testMaterial);
        Beam b4 = new Beam(n4, n5, testMaterial);
        Beam b5 = new Beam(n5, n6, testMaterial);
        Beam b6 = new Beam(n6, n7, testMaterial);
        Beam b7 = new Beam(n7, n8, testMaterial);
        Beam b8 = new Beam(n8, n9, testMaterial);
        Beam b9 = new Beam(n9, n10, testMaterial);
        Beam b10 = new Beam(n10, n11, testMaterial);
        Beam b11 = new Beam(n11, n12, testMaterial);
        Beam b12 = new Beam(n12, n13, testMaterial);
        Beam b13 = new Beam(n13, n14, testMaterial);
        Beam b14 = new Beam(n14, n15, testMaterial);
        Beam b15 = new Beam(n15, n16, testMaterial);
        Beam b16 = new Beam(n16, n17, testMaterial);
        Beam b17 = new Beam(n17, n18, testMaterial);
        Beam b18 = new Beam(n18, n19, testMaterial);
        Beam b19 = new Beam(n20, n6, testMaterial);
        Beam b20 = new Beam(n21, n14, testMaterial);
        Beam b21 = new Beam(n3, n22, testMaterial);
        Beam b22 = new Beam(n4, n23, testMaterial);
        Beam b23 = new Beam(n5, n24, testMaterial);
        Beam b24 = new Beam(n6, n25, testMaterial);
        Beam b25 = new Beam(n7, n26, testMaterial);
        Beam b26 = new Beam(n8, n27, testMaterial);
        Beam b27 = new Beam(n9, n28, testMaterial);
        Beam b28 = new Beam(n11, n29, testMaterial);
        Beam b29 = new Beam(n12, n30, testMaterial);
        Beam b30 = new Beam(n13, n31, testMaterial);
        Beam b31 = new Beam(n14, n32, testMaterial);
        Beam b32 = new Beam(n15, n33, testMaterial);
        Beam b33 = new Beam(n16, n34, testMaterial);
        Beam b34 = new Beam(n17, n35, testMaterial);
        Beam b35 = new Beam(n2, n22, testMaterial);
        Beam b36 = new Beam(n22, n23, testMaterial);
        Beam b37 = new Beam(n23, n24, testMaterial);
        Beam b38 = new Beam(n24, n25, testMaterial);
        Beam b39 = new Beam(n25, n26, testMaterial);
        Beam b40 = new Beam(n26, n27, testMaterial);
        Beam b41 = new Beam(n27, n28, testMaterial);
        Beam b42 = new Beam(n28, n10, testMaterial);
        Beam b43 = new Beam(n10, n29, testMaterial);
        Beam b44 = new Beam(n29, n30, testMaterial);
        Beam b45 = new Beam(n30, n31, testMaterial);
        Beam b46 = new Beam(n31, n32, testMaterial);
        Beam b47 = new Beam(n32, n33, testMaterial);
        Beam b48 = new Beam(n33, n34, testMaterial);
        Beam b49 = new Beam(n34, n35, testMaterial);
        Beam b50 = new Beam(n35, n18, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33, b34, b35, b36, b37, b38, b39, b40, b41, b42, b43, b44, b45, b46, b47, b48, b49, b50);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n19.setConstraint(con);
        n20.setConstraint(con);
        n21.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }


    public static Structure getTVtower() {

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(2.000000, 32.000000);
        Node n2 = new Node(5.000000, 32.000000);
        Node n3 = new Node(0.000000, 14.000000);
        Node n4 = new Node(2.500000, 14.000000);
        Node n5 = new Node(4.500000, 14.000000);
        Node n6 = new Node(7.000000, 14.000000);
        Node n7 = new Node(0.000000, 13.000000);
        Node n8 = new Node(7.000000, 13.000000);
        Node n9 = new Node(0.000000, 12.000000);
        Node n10 = new Node(2.500000, 12.000000);
        Node n11 = new Node(4.500000, 12.000000);
        Node n12 = new Node(7.000000, 12.000000);
        Node n13 = new Node(0.000000, 11.000000);
        Node n14 = new Node(2.500000, 11.000000);
        Node n15 = new Node(4.500000, 11.000000);
        Node n16 = new Node(7.000000, 11.000000);
        Node n17 = new Node(0.000000, 8.000000);
        Node n18 = new Node(3.000000, 8.000000);
        Node n19 = new Node(4.000000, 8.000000);
        Node n20 = new Node(7.000000, 8.000000);
        Node n21 = new Node(2.000000, 7.000000);
        Node n22 = new Node(3.000000, 7.000000);
        Node n23 = new Node(4.000000, 7.000000);
        Node n24 = new Node(5.000000, 7.000000);
        Node n25 = new Node(2.000000, 6.000000);
        Node n26 = new Node(3.000000, 6.000000);
        Node n27 = new Node(4.000000, 6.000000);
        Node n28 = new Node(5.000000, 6.000000);
        Node n29 = new Node(3.500000, 1.000000);


        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n3, n4, testMaterial);
        Beam b3 = new Beam(n4, n5, testMaterial);
        Beam b4 = new Beam(n5, n6, testMaterial);
        Beam b5 = new Beam(n7, n8, testMaterial);
        Beam b6 = new Beam(n9, n10, testMaterial);
        Beam b7 = new Beam(n10, n11, testMaterial);
        Beam b8 = new Beam(n11, n12, testMaterial);
        Beam b9 = new Beam(n13, n14, testMaterial);
        Beam b10 = new Beam(n14, n15, testMaterial);
        Beam b11 = new Beam(n15, n16, testMaterial);
        Beam b12 = new Beam(n17, n18, testMaterial);
        Beam b13 = new Beam(n18, n19, testMaterial);
        Beam b14 = new Beam(n19, n20, testMaterial);
        Beam b15 = new Beam(n21, n22, testMaterial);
        Beam b16 = new Beam(n22, n23, testMaterial);
        Beam b17 = new Beam(n23, n24, testMaterial);
        Beam b18 = new Beam(n25, n26, testMaterial);
        Beam b19 = new Beam(n26, n27, testMaterial);
        Beam b20 = new Beam(n27, n28, testMaterial);
        Beam b21 = new Beam(n1, n4, testMaterial);
        Beam b22 = new Beam(n2, n5, testMaterial);
        Beam b23 = new Beam(n3, n7, testMaterial);
        Beam b24 = new Beam(n6, n8, testMaterial);
        Beam b25 = new Beam(n7, n9, testMaterial);
        Beam b26 = new Beam(n8, n12, testMaterial);
        Beam b27 = new Beam(n10, n14, testMaterial);
        Beam b28 = new Beam(n11, n15, testMaterial);
        Beam b29 = new Beam(n13, n17, testMaterial);
        Beam b30 = new Beam(n16, n20, testMaterial);
        Beam b31 = new Beam(n18, n22, testMaterial);
        Beam b32 = new Beam(n19, n23, testMaterial);
        Beam b33 = new Beam(n21, n25, testMaterial);
        Beam b34 = new Beam(n22, n26, testMaterial);
        Beam b35 = new Beam(n23, n27, testMaterial);
        Beam b36 = new Beam(n24, n28, testMaterial);
        Beam b37 = new Beam(n26, n29, testMaterial);
        Beam b38 = new Beam(n27, n29, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33, b34, b35, b36, b37, b38);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getTaipeh() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0.000000, 49.000000);
        Node n2 = new Node(7.000000, 49.000000);
        Node n3 = new Node(1.500000, 39.000000);
        Node n4 = new Node(5.500000, 39.000000);
        Node n5 = new Node(1.000000, 35.500000);
        Node n6 = new Node(1.500000, 35.500000);
        Node n7 = new Node(5.500000, 35.500000);
        Node n8 = new Node(6.000000, 35.500000);
        Node n9 = new Node(1.000000, 32.000000);
        Node n10 = new Node(1.500000, 32.000000);
        Node n11 = new Node(5.500000, 32.000000);
        Node n12 = new Node(6.000000, 32.000000);
        Node n13 = new Node(1.000000, 28.500000);
        Node n14 = new Node(1.500000, 28.500000);
        Node n15 = new Node(5.500000, 28.500000);
        Node n16 = new Node(6.000000, 28.500000);
        Node n17 = new Node(1.000000, 25.000000);
        Node n18 = new Node(1.500000, 25.000000);
        Node n19 = new Node(5.500000, 25.000000);
        Node n20 = new Node(6.000000, 25.000000);
        Node n21 = new Node(1.000000, 21.500000);
        Node n22 = new Node(1.500000, 21.500000);
        Node n23 = new Node(5.500000, 21.500000);
        Node n24 = new Node(6.000000, 21.500000);
        Node n25 = new Node(1.000000, 18.000000);
        Node n26 = new Node(1.500000, 18.000000);
        Node n27 = new Node(5.500000, 18.000000);
        Node n28 = new Node(6.000000, 18.000000);
        Node n29 = new Node(1.000000, 14.500000);
        Node n30 = new Node(1.500000, 14.500000);
        Node n31 = new Node(5.500000, 14.500000);
        Node n32 = new Node(6.000000, 14.500000);
        Node n33 = new Node(1.000000, 11.000000);
        Node n34 = new Node(3.000000, 11.000000);
        Node n35 = new Node(4.000000, 11.000000);
        Node n36 = new Node(6.000000, 11.000000);
        Node n37 = new Node(3.000000, 6.000000);
        Node n38 = new Node(3.500000, 6.000000);
        Node n39 = new Node(4.000000, 6.000000);
        Node n40 = new Node(3.500000, 0.000000);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n3, n4, testMaterial);
        Beam b3 = new Beam(n5, n6, testMaterial);
        Beam b4 = new Beam(n6, n7, testMaterial);
        Beam b5 = new Beam(n7, n8, testMaterial);
        Beam b6 = new Beam(n9, n10, testMaterial);
        Beam b7 = new Beam(n10, n11, testMaterial);
        Beam b8 = new Beam(n11, n12, testMaterial);
        Beam b9 = new Beam(n13, n14, testMaterial);
        Beam b10 = new Beam(n14, n15, testMaterial);
        Beam b11 = new Beam(n15, n16, testMaterial);
        Beam b12 = new Beam(n17, n18, testMaterial);
        Beam b13 = new Beam(n18, n19, testMaterial);
        Beam b14 = new Beam(n19, n20, testMaterial);
        Beam b15 = new Beam(n21, n22, testMaterial);
        Beam b16 = new Beam(n22, n23, testMaterial);
        Beam b17 = new Beam(n23, n24, testMaterial);
        Beam b18 = new Beam(n25, n26, testMaterial);
        Beam b19 = new Beam(n26, n27, testMaterial);
        Beam b20 = new Beam(n27, n28, testMaterial);
        Beam b21 = new Beam(n29, n30, testMaterial);
        Beam b22 = new Beam(n30, n31, testMaterial);
        Beam b23 = new Beam(n31, n32, testMaterial);
        Beam b24 = new Beam(n33, n34, testMaterial);
        Beam b25 = new Beam(n34, n35, testMaterial);
        Beam b26 = new Beam(n35, n36, testMaterial);
        Beam b27 = new Beam(n37, n38, testMaterial);
        Beam b28 = new Beam(n38, n39, testMaterial);
        Beam b29 = new Beam(n1, n3, testMaterial);
        Beam b30 = new Beam(n2, n4, testMaterial);
        Beam b31 = new Beam(n3, n5, testMaterial);
        Beam b32 = new Beam(n4, n8, testMaterial);
        Beam b33 = new Beam(n6, n9, testMaterial);
        Beam b34 = new Beam(n7, n12, testMaterial);
        Beam b35 = new Beam(n10, n13, testMaterial);
        Beam b36 = new Beam(n11, n16, testMaterial);
        Beam b37 = new Beam(n14, n17, testMaterial);
        Beam b38 = new Beam(n15, n20, testMaterial);
        Beam b39 = new Beam(n18, n21, testMaterial);
        Beam b40 = new Beam(n19, n24, testMaterial);
        Beam b41 = new Beam(n22, n25, testMaterial);
        Beam b42 = new Beam(n23, n28, testMaterial);
        Beam b43 = new Beam(n26, n29, testMaterial);
        Beam b44 = new Beam(n27, n32, testMaterial);
        Beam b45 = new Beam(n30, n33, testMaterial);
        Beam b46 = new Beam(n31, n36, testMaterial);
        Beam b47 = new Beam(n34, n37, testMaterial);
        Beam b48 = new Beam(n35, n39, testMaterial);
        Beam b49 = new Beam(n38, n40, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33, b34, b35, b36, b37, b38, b39, b40, b41, b42, b43, b44, b45, b46, b47, b48, b49);

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

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node g1 = new Node(width/4, height);
        Node g2 = new Node(width/2, height);
        Node g3 = new Node(3*width/4, height);
        Node s1 = new Node(0, height/2);
        Node s2 = new Node(width/5, height/2);
        Node s3 = new Node(2*width/5, height/2);
        Node s4 = new Node(3*width/5, height/2);
        Node s5 = new Node(4*width/5, height/2);
        Node s6 = new Node(width, height/2);
        Node t1 = new Node(width/4, 0, true); //hinge
        Node t2 = new Node(width/2, 0, true); //hinge
        Node t3 = new Node(3*width/4, 0, true); //hinge

        Beam c1 = new Beam(g1, t1, testMaterial);
        Beam c2 = new Beam(g2, t2, testMaterial);
        Beam c3 = new Beam(g3, t3, testMaterial);
        Beam sb1 = new Beam(s1, s2, testMaterial);
        Beam sb2 = new Beam(s2, s3, testMaterial);
        Beam sb3 = new Beam(s3, s4, testMaterial);
        Beam sb4 = new Beam(s4, s5, testMaterial);
        Beam sb5 = new Beam(s5, s6, testMaterial);
        Beam h1 = new Beam(t1, s2, testMaterial);
        Beam h2 = new Beam(t1, s3, testMaterial);
        Beam h3 = new Beam(t2, s3, testMaterial);
        Beam h4 = new Beam(t2, s4, testMaterial);
        Beam h5 = new Beam(t3, s4, testMaterial);
        Beam h6 = new Beam(t3, s5, testMaterial);

        structure.addNodes(g1,g2,g3,s1,s2,s3,s4,s5,s6,t1,t2,t3);
        structure.addBeams(c1,c2,c3,sb1,sb2,sb3,sb4,sb5,h1,h2,h3,h4,h5,h6);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        g1.setConstraint(con);
        g2.setConstraint(con);
        g3.setConstraint(con);

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
        Material testMaterial = Material.STEEL;
        Node n1 = new Node(0, height);
        Node n2 = new Node(width/4, height);
        Node n3 = new Node(width/2, height);
        Node n4 = new Node(3*width/4, height);
        Node n5 = new Node(width, height);

        Node n6 = new Node(0, 3*height/4);
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
        Beam b23 = new Beam(n11, n12, testMaterial);
        Beam b24 = new Beam(n12, n13, testMaterial);
        Beam b25 = new Beam(n13, n14, testMaterial);
        Beam b26 = new Beam(n14, n15, testMaterial);
        Beam b27 = new Beam(n16, n17, testMaterial);
        Beam b28 = new Beam(n17, n18, testMaterial);

        structure.addNodes(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19);
        structure.addBeams(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24,b25,b26,b27,b28);

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

    public static Structure getSimpleElephant() {

        boolean lumped = true; // Make it false for consistent mass matrices!
        int move = 1;
        Structure structure = new Structure();
        //  structure.setLumped(lumped);

        Material testMaterial = Material.STEEL;

        Node n1 = new Node(1+move, 8);
        Node n2 = new Node(2+move, 8); //left foot
        Node n3 = new Node(4+move, 8);
        Node n4 = new Node(5+move, 8); //right foot

        Node n5 = new Node(1+move, 6);
        Node n6 = new Node(2+move, 6); //left foot/tummy
        Node n7 = new Node(4+move, 6);
        Node n8 = new Node(5+move, 6); //right foot/belly

        Node n9 = new Node(0+move, 4); //posterior
        Node n10 = new Node(1+move, 2);
        Node n11 = new Node(4+move, 2);

        Node n12 = new Node(5+move, 1); //ear
        Node n13 = new Node(7+move, 1.5);
        Node n14 = new Node(6.5+move, 4);
        Node n15 = new Node(5+move, 5);
        Node n16 = new Node(3.5+move, 3);

        Node n17 = new Node(6.9+move, 2); //trunk
        Node n18 = new Node(9+move, 4);
        Node n19 = new Node(10+move, 6);
        Node n20 = new Node(10+move, 8, 10);
        Node n21 = new Node(9.5+move, 8);
        Node n22 = new Node(9.5+move, 6);
        Node n23 = new Node(8.6+move, 4.5);
        Node n24 = new Node(8+move, 4);
        Node n25 = new Node(7+move, 4.5);

        Node n26 = new Node(-1+move, 4); //tail
        Node n27 = new Node(-1+move, 5);
        Node n28 = new Node(-0.5+move, 5);
        Node n29 = new Node(-0.5+move, 4.5);

        Node n30 = new Node(8.3 , 3, true);

        Beam b1 = new Beam(n1, n5, testMaterial);
        Beam b2 = new Beam(n2, n6, testMaterial);
        Beam b3 = new Beam(n3, n7, testMaterial);
        Beam b4 = new Beam(n4, n8, testMaterial);

        Beam b5 = new Beam(n6, n7, testMaterial); //belly

        Beam b6 = new Beam(n5, n9, testMaterial);
        Beam b7 = new Beam(n9, n10, testMaterial);
        Beam b8 = new Beam(n10, n11, testMaterial);

        Beam b9 = new Beam(n11, n12, testMaterial);  //ear
        Beam b10 = new Beam(n12, n13, testMaterial);
        Beam b11 = new Beam(n13, n17, testMaterial);
        Beam b12 = new Beam(n17, n14, testMaterial);
        Beam b13 = new Beam(n14, n15, testMaterial);
        Beam b14 = new Beam(n15, n16, testMaterial);
        Beam b15 = new Beam(n16, n11, testMaterial);

        Beam b16 = new Beam(n17, n18, testMaterial); //trunk
        Beam b17 = new Beam(n18, n19, testMaterial);
        Beam b18 = new Beam(n19, n20, testMaterial);
        Beam b19 = new Beam(n20, n21, testMaterial);
        Beam b20 = new Beam(n21, n22, testMaterial);
        Beam b21 = new Beam(n22, n23, testMaterial);
        Beam b22 = new Beam(n23, n24, testMaterial);
        Beam b23 = new Beam(n24, n25, testMaterial);
        Beam b24 = new Beam(n25, n8, testMaterial);

        Beam b25 = new Beam(n5, n6, testMaterial);
        Beam b26 = new Beam(n7, n8, testMaterial);

        Beam b27 = new Beam(n18, n23, testMaterial);
        Beam b28 = new Beam(n19, n22, testMaterial);

        Beam b29 = new Beam(n14, n25, testMaterial);

        Beam b30 = new Beam(n9, n26, testMaterial);
        Beam b31 = new Beam(n26, n27, testMaterial);
        Beam b32 = new Beam(n27, n28, testMaterial);
        Beam b33 = new Beam(n28, n29, testMaterial);

      /*  Beam b34 = new Beam(n9, n16, testMaterial);
        Beam b35 = new Beam(n14, n25, testMaterial);*/


        structure.addNodes(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,n21,n22,n23,n24,n25,n26,n27,n28,n29,n30);
        structure.addBeams(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24,b25,b26,b27,b28,b29,b29,b30,b31,b32,b33);//,b34,b35);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);
        n30.setConstraint(con);

        enumerateDOFs(structure);
        return structure;


    }


    public static Structure getOneWTC() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0.000000, 32.000000);
        Node n2 = new Node(3.000000, 32.000000);
        Node n3 = new Node(6.000000, 32.000000);
        Node n4 = new Node(0.000000, 28.000000);
        Node n5 = new Node(3.000000, 28.000000);
        Node n6 = new Node(6.000000, 28.000000);
        Node n7 = new Node(1.000000, 8.000000);
        Node n8 = new Node(1.500000, 8.000000);
        Node n9 = new Node(4.500000, 8.000000);
        Node n10 = new Node(5.000000, 8.000000);
        Node n11 = new Node(1.500000, 7.500000);
        Node n12 = new Node(2.500000, 7.500000);
        Node n13 = new Node(3.500000, 7.500000);
        Node n14 = new Node(4.500000, 7.500000);
        Node n15 = new Node(3.000000, 0.000000);


        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n4, n5, testMaterial);
        Beam b4 = new Beam(n5, n6, testMaterial);
        Beam b5 = new Beam(n7, n8, testMaterial);
        Beam b6 = new Beam(n8, n9, testMaterial);
        Beam b7 = new Beam(n9, n10, testMaterial);
        Beam b8 = new Beam(n11, n12, testMaterial);
        Beam b9 = new Beam(n12, n13, testMaterial);
        Beam b10 = new Beam(n13, n14, testMaterial);
        Beam b11 = new Beam(n1, n4, testMaterial);
        Beam b12 = new Beam(n2, n5, testMaterial);
        Beam b13 = new Beam(n3, n6, testMaterial);
        Beam b14 = new Beam(n4, n7, testMaterial);
        Beam b15 = new Beam(n5, n7, testMaterial);
        Beam b16 = new Beam(n5, n10, testMaterial);
        Beam b17 = new Beam(n6, n10, testMaterial);
        Beam b18 = new Beam(n8, n11, testMaterial);
        Beam b19 = new Beam(n9, n14, testMaterial);
        Beam b20 = new Beam(n12, n15, testMaterial);
        Beam b21 = new Beam(n13, n15, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }


    public static Structure getTrumpTower() {
        double width = 8;
        double xOffset = -4;
        double height = 8;
        double height2 = 2; //shouldn't be bigger than 4
        double height3 = 1; //shoudln't be bigger than 4

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        //Ground
        Node n1 = new Node(width/3+xOffset, height);
        Node n2 = new Node(4*width/9+xOffset, height);
        Node n3 = new Node(5*width/9+xOffset, height);
        Node n4 = new Node(2*width/3+xOffset, height);
        //Left thing
        Node n5 = new Node(8*width/27+xOffset, height-height2/3);
        Node n6 = new Node(8*width/27+xOffset, height-2*height2/3);
        Node n7 = new Node(10*width/27+xOffset, height-height2);
        Node n8 = new Node(4*width/9+xOffset, height-2*height2/3);
        //Right thing
        Node n9 = new Node(19*width/27+xOffset, height-height2/3);
        Node n10 = new Node(19*width/27+xOffset, height-2*height2/3);
        Node n11 = new Node(17*width/27+xOffset, height-height2);
        Node n12 = new Node(5*width/9+xOffset, height-2*height2/3);
        //Middle thing
        Node n13 = new Node(4*width/9+xOffset, height/2);
        Node n14 = new Node(5*width/9+xOffset, height/2);
        Node n15 = new Node(4*width/9+xOffset, height3);
        Node n16 = new Node(5*width/9+xOffset, height3);
        Node n17 = new Node(11*width/27+xOffset, height3);
        Node n18 = new Node(16*width/27+xOffset, height3);
        Node n19 = new Node(12*width/27+xOffset, height3/3);
        Node n20 = new Node(width/2+xOffset, 0);
        Node n21 = new Node(15*width/27+xOffset, height3/3);
        Node n22 = new Node(4*width/9+xOffset, height3/2+height/4);
        Node n23 = new Node(5*width/9+xOffset, height3/2+height/4);
        Node n24 = new Node(4*width/9+xOffset, 3*height/4-height2/3);
        Node n25 = new Node(5*width/9+xOffset, 3*height/4-height2/3);

        //Left thing
        Beam b1 = new Beam(n1, n5, testMaterial);
        Beam b2 = new Beam(n5, n6, testMaterial);
        Beam b3 = new Beam(n6, n7, testMaterial);
        Beam b4 = new Beam(n7, n8, testMaterial);
        Beam b5 = new Beam(n8, n2, testMaterial);
        //Right thing
        Beam b6 = new Beam(n4, n9, testMaterial);
        Beam b7 = new Beam(n9, n10, testMaterial);
        Beam b8 = new Beam(n10, n11, testMaterial);
        Beam b9 = new Beam(n11, n12, testMaterial);
        Beam b10 = new Beam(n12, n3, testMaterial);
        //Middle thing
        Beam b11 = new Beam(n8, n24, testMaterial);
        Beam b12 = new Beam(n24, n13, testMaterial);
        Beam b13 = new Beam(n12, n25, testMaterial);
        Beam b14 = new Beam(n25, n14, testMaterial);
        Beam b15 = new Beam(n13, n22, testMaterial);
        Beam b16 = new Beam(n22, n15, testMaterial);
        Beam b17 = new Beam(n14, n23, testMaterial);
        Beam b18 = new Beam(n23, n16, testMaterial);
        Beam b19 = new Beam(n15, n17, testMaterial);
        Beam b20 = new Beam(n16, n18, testMaterial);
        Beam b21 = new Beam(n17, n19, testMaterial);
        Beam b22 = new Beam(n18, n21, testMaterial);
        Beam b23 = new Beam(n19, n20, testMaterial);
        Beam b24 = new Beam(n20, n21, testMaterial);

        structure.addNodes(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,n21,n22,n23,n24,n25);
        structure.addBeams(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;

        n1.setConstraint(con);
        n2.setConstraint(con);
        n3.setConstraint(con);
        n4.setConstraint(con);

        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getBurjKhalifa() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(0.000000, 35.000000);
        Node n2 = new Node(5.000000, 35.000000);
        Node n3 = new Node(0.000000, 32.000000);
        Node n4 = new Node(5.000000, 32.000000);
        Node n5 = new Node(4.500000, 30.000000);
        Node n6 = new Node(5.000000, 30.000000);
        Node n7 = new Node(0.000000, 29.000000);
        Node n8 = new Node(0.500000, 29.000000);
        Node n9 = new Node(0.500000, 27.000000);
        Node n10 = new Node(4.500000, 27.000000);
        Node n11 = new Node(4.000000, 25.000000);
        Node n12 = new Node(4.500000, 25.000000);
        Node n13 = new Node(0.500000, 23.000000);
        Node n14 = new Node(1.000000, 23.000000);
        Node n15 = new Node(1.000000, 21.000000);
        Node n16 = new Node(4.000000, 21.000000);
        Node n17 = new Node(3.500000, 19.000000);
        Node n18 = new Node(4.000000, 19.000000);
        Node n19 = new Node(1.000000, 17.000000);
        Node n20 = new Node(1.500000, 17.000000);
        Node n21 = new Node(1.500000, 15.000000);
        Node n22 = new Node(3.500000, 15.000000);
        Node n23 = new Node(3.000000, 12.000000);
        Node n24 = new Node(3.500000, 12.000000);
        Node n25 = new Node(1.500000, 11.000000);
        Node n26 = new Node(2.000000, 11.000000);
        Node n27 = new Node(2.000000, 6.000000);
        Node n28 = new Node(2.500000, 6.000000);
        Node n29 = new Node(2.500000, 3.000000);
        Node n30 = new Node(3.000000, 3.000000);
        Node n31 = new Node(3.000000, 0.000000);


        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n3, n4, testMaterial);
        Beam b3 = new Beam(n5, n6, testMaterial);
        Beam b4 = new Beam(n7, n8, testMaterial);
        Beam b5 = new Beam(n9, n10, testMaterial);
        Beam b6 = new Beam(n11, n12, testMaterial);
        Beam b7 = new Beam(n13, n14, testMaterial);
        Beam b8 = new Beam(n15, n16, testMaterial);
        Beam b9 = new Beam(n17, n18, testMaterial);
        Beam b10 = new Beam(n19, n20, testMaterial);
        Beam b11 = new Beam(n21, n22, testMaterial);
        Beam b12 = new Beam(n23, n24, testMaterial);
        Beam b13 = new Beam(n25, n26, testMaterial);
        Beam b14 = new Beam(n27, n28, testMaterial);
        Beam b15 = new Beam(n29, n30, testMaterial);
        Beam b16 = new Beam(n1, n3, testMaterial);
        Beam b17 = new Beam(n2, n4, testMaterial);
        Beam b18 = new Beam(n3, n7, testMaterial);
        Beam b19 = new Beam(n4, n6, testMaterial);
        Beam b20 = new Beam(n5, n10, testMaterial);
        Beam b21 = new Beam(n8, n9, testMaterial);
        Beam b22 = new Beam(n9, n13, testMaterial);
        Beam b23 = new Beam(n10, n12, testMaterial);
        Beam b24 = new Beam(n11, n16, testMaterial);
        Beam b25 = new Beam(n14, n15, testMaterial);
        Beam b26 = new Beam(n15, n19, testMaterial);
        Beam b27 = new Beam(n16, n18, testMaterial);
        Beam b28 = new Beam(n17, n22, testMaterial);
        Beam b29 = new Beam(n20, n21, testMaterial);
        Beam b30 = new Beam(n21, n25, testMaterial);
        Beam b31 = new Beam(n22, n24, testMaterial);
        Beam b32 = new Beam(n23, n30, testMaterial);
        Beam b33 = new Beam(n26, n27, testMaterial);
        Beam b34 = new Beam(n28, n29, testMaterial);
        Beam b35 = new Beam(n30, n31, testMaterial);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33, b34, b35);

        boolean[] con = new boolean[3];
        con[0]=true;
        con[1]=true;
        con[2]=true;
        n1.setConstraint(con);
        n2.setConstraint(con);
        enumerateDOFs(structure);
        return structure;
    }
    public static Structure getEierlaufen() {
        double width = 8;
        double height = 8;

        double half = width * 0.5;

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        Node n1 = new Node(1, 2.0, false);
        Node n2 = new Node(0.0, 4.0, false);
        Node n3 = new Node(3.0, 4.0, false);

        Beam b1 = new Beam(n1, n2, testMaterial);
        Beam b2 = new Beam(n2, n3, testMaterial);
        Beam b3 = new Beam(n3, n1, testMaterial);

        structure.addNodes(n1, n2, n3);
        structure.addBeams(b1, b2, b3);

        enumerateDOFs(structure);
        return structure;
    }

    public static Structure getStructure(Context context, int structureId, String structureName) {
        if (structureId == 0) {
            return StructureFactory.cantileverBeam();
        } else if (structureId == 1) {
            return StructureFactory.getSimpleHouse();
        } else if (structureId == 2) {
            return StructureFactory.getCraneBottom();
        } else if (structureId == 3) {
            return StructureFactory.getBetterEiffelTower();
        } else if (structureId == 4) {
            return StructureFactory.getEmpireState();
        } else if (structureId == 5) {
            return StructureFactory.getGoldenGate();
        } else if (structureId == 6) {
            return StructureFactory.getWeirdBridge();
        } else if (structureId == 7) {
            return StructureFactory.getHousingBlock();
        } else if (structureId == 8) {
            return StructureFactory.getTrumpTower();
        } else if (structureId == 9) {
            return StructureFactory.getTVtower();
        } else if (structureId == 10) {
            return StructureFactory.getTaipeh();
        } else if (structureId == 11) {
            return StructureFactory.getHouseWithMassDamper();
        } else if (structureId == 12) {
            return StructureFactory.getOneWTC();
        } else if (structureId == 13) {
            return StructureFactory.getBurjKhalifa();
        }  else if (structureId == 14) {
            return StructureFactory.getSimpleElephant();
        /**
            return StructureFactory.getPresentation_EX_w_TMD();
        } else if (structureId == 15) {
            return StructureFactory.getPresentation_EX_wo_TMD();
        } else if (structureId == 16) {
            return StructureFactory.getSimpleElephant();
        }   else if (structureId == 17) {
            return StructureFactory.getEierlaufen();
        } else if (structureId == 18) {
            return StructureFactory.getDemoTMD();
         */
        }

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = context.openFileInput(structureName);

            Structure structure = StructureIO.readStructure(fileInputStream);

            List<Node> nodes = structure.getNodes();
            List<Beam> beams = structure.getBeams();

            HashSet<Node> nodeSet = new HashSet<>();

            nodeSet.addAll(nodes);

            for (int i = nodes.size() - 1; i >= 0; i--) {
                if (nodes.get(i).getBeams().isEmpty()) nodes.remove(i);
            }

            for (int i = beams.size() - 1; i >= 0; i--) {
                if (!nodeSet.contains(beams.get(i).getStartNode())
                        || !nodeSet.contains(beams.get(i).getEndNode())) beams.remove(i);
            }

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
                    Beam beam = beams.get(j);

                    // dof for rotation of this beam
                    dofs.add(numberofDOF);

                    if (node.getConstraint(2)){
                        structure.addSingleConDOF(numberofDOF);
                    }

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

            // set the dofs at the node
            node.setDOF(dofs);
            structure.setNumberOfDOF(numberofDOF);
        }

    }

}
