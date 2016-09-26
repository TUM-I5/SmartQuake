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

    public static Structure getLongFrame() {

        double width = 8;
        double height = 24;

        boolean lumped = true;

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        //Kernel1 stuff

        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();


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


        Node n1 = new Node(0, height, DOFnode1);
        Node n2 = new Node(width, height, DOFnode2);
        Node n3 = new Node(0, 0, DOFnode3);
        Node n4 = new Node(width, 0, DOFnode4);

        Beam b1 = new Beam(n1, n3, testMaterial,lumped);
        Beam b2 = new Beam(n3, n4, testMaterial,lumped);
        Beam b3 = new Beam(n4, n2, testMaterial,lumped);


        structure.addNodes(n1, n2, n3, n4);
        structure.addBeams(b1, b2, b3);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            condof.add(i);
        }

        structure.setConDOF(condof);

        return structure;

    }

    public static Structure getCraneBottom() {

        double width = 8;
        double height = 24;

        boolean lumped = true;

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        //Kernel1 stuff

        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();
        List<Integer> DOFnode5 = new LinkedList<>();
        List<Integer> DOFnode6 = new LinkedList<>();
        List<Integer> DOFnode7 = new LinkedList<>();
        List<Integer> DOFnode8 = new LinkedList<>();

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
        DOFnode6.add(15);
        DOFnode6.add(16);
        DOFnode6.add(17);
        DOFnode7.add(18);
        DOFnode7.add(19);
        DOFnode7.add(20);
        DOFnode8.add(21);
        DOFnode8.add(22);
        DOFnode8.add(23);

        Node n1 = new Node(0, height, DOFnode1);
        Node n2 = new Node(width, height, DOFnode2);
        Node n3 = new Node(0, height - width, DOFnode3);
        Node n4 = new Node(width, height - width, DOFnode4);
        Node n5 = new Node(0, height - 2 * width, DOFnode5);
        Node n6 = new Node(width, height - 2 * width, DOFnode6);
        Node n7 = new Node(0, height - 3 * width, DOFnode7);
        Node n8 = new Node(width, height - 3 * width, DOFnode8);

        Beam b1 = new Beam(n1, n3, testMaterial,lumped);
        Beam b2 = new Beam(n3, n5, testMaterial,lumped);
        Beam b3 = new Beam(n5, n7, testMaterial,lumped);
        Beam b4 = new Beam(n7, n8, testMaterial,lumped);
        Beam b5 = new Beam(n8, n6, testMaterial,lumped);
        Beam b6 = new Beam(n6, n4, testMaterial,lumped);
        Beam b7 = new Beam(n4, n2, testMaterial,lumped);
        Beam b8 = new Beam(n2, n3, testMaterial,lumped);
        Beam b9 = new Beam(n4, n5, testMaterial,lumped);
        Beam b10 = new Beam(n6, n7, testMaterial,lumped);
        Beam b11 = new Beam(n3, n4, testMaterial,lumped);
        Beam b12 = new Beam(n5, n6, testMaterial,lumped);

        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            condof.add(i);
        }

        structure.setConDOF(condof);

        return structure;

    }

    public static Structure getBetterEiffelTower() {
        double a = 1;
        double width = 6 * a;
        double height = 16 * a;

        boolean lumped = true;

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        //Kernel1 stuff

        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();
        List<Integer> DOFnode5 = new LinkedList<>();
        List<Integer> DOFnode6 = new LinkedList<>();
        List<Integer> DOFnode7 = new LinkedList<>();
        List<Integer> DOFnode8 = new LinkedList<>();
        List<Integer> DOFnode9 = new LinkedList<>();
        List<Integer> DOFnode10 = new LinkedList<>();
        List<Integer> DOFnode11 = new LinkedList<>();
        List<Integer> DOFnode12 = new LinkedList<>();
        List<Integer> DOFnode13 = new LinkedList<>();
        List<Integer> DOFnode14 = new LinkedList<>();
        List<Integer> DOFnode15 = new LinkedList<>();
        List<Integer> DOFnode16 = new LinkedList<>();
        List<Integer> DOFnode17 = new LinkedList<>();
        List<Integer> DOFnode18 = new LinkedList<>();
        List<Integer> DOFnode19 = new LinkedList<>();
        List<Integer> DOFnode20 = new LinkedList<>();
        List<Integer> DOFnode21 = new LinkedList<>();
        List<Integer> DOFnode22 = new LinkedList<>();
        List<Integer> DOFnode23 = new LinkedList<>();
        List<Integer> DOFnode24 = new LinkedList<>();

        DOFnode1.add(0);//constraint
        DOFnode1.add(1);//constraint
        DOFnode1.add(2);//constraint

        DOFnode2.add(3);//constraint
        DOFnode2.add(4);//constraint
        DOFnode2.add(5);//constraint

        DOFnode3.add(6);//constraint
        DOFnode3.add(7);//constraint
        DOFnode3.add(8);//constraint

        DOFnode4.add(9);//constraint
        DOFnode4.add(10);//constraint
        DOFnode4.add(11);//constraint

        DOFnode5.add(12);
        DOFnode5.add(13);
        DOFnode5.add(14);
        DOFnode6.add(15);
        DOFnode6.add(16);
        DOFnode6.add(17);
        DOFnode7.add(18);
        DOFnode7.add(19);
        DOFnode7.add(20);
        DOFnode8.add(21);
        DOFnode8.add(22);
        DOFnode8.add(23);
        DOFnode9.add(24);
        DOFnode9.add(25);
        DOFnode9.add(26);
        DOFnode10.add(27);
        DOFnode10.add(28);
        DOFnode10.add(29);
        DOFnode11.add(30);
        DOFnode11.add(31);
        DOFnode11.add(32);
        DOFnode12.add(33);
        DOFnode12.add(34);
        DOFnode12.add(35);
        DOFnode13.add(36);
        DOFnode13.add(37);
        DOFnode13.add(38);
        DOFnode14.add(39);
        DOFnode14.add(40);
        DOFnode14.add(41);
        DOFnode15.add(42);
        DOFnode15.add(43);
        DOFnode15.add(44);
        DOFnode16.add(45);
        DOFnode16.add(46);
        DOFnode16.add(47);
        DOFnode17.add(48);
        DOFnode17.add(49);
        DOFnode17.add(50);
        DOFnode18.add(51);
        DOFnode18.add(52);
        DOFnode18.add(53);
        DOFnode19.add(54);
        DOFnode19.add(55);
        DOFnode19.add(56);
        DOFnode20.add(57);
        DOFnode20.add(58);
        DOFnode20.add(59);
        DOFnode21.add(60);
        DOFnode21.add(61);
        DOFnode21.add(62);
        DOFnode22.add(63);
        DOFnode22.add(64);
        DOFnode22.add(65);
        DOFnode23.add(66);
        DOFnode23.add(67);
        DOFnode23.add(68);
        DOFnode24.add(69);
        DOFnode24.add(70);
        DOFnode24.add(71);

        Node n1 = new Node(0, height, DOFnode1);
        Node n2 = new Node(a, height, DOFnode2);
        Node n3 = new Node(width - a, height, DOFnode3);
        Node n4 = new Node(width, height, DOFnode4);
        Node n5 = new Node(a, height - 2 * a, DOFnode5);
        Node n6 = new Node(2 * a, height - 2 * a, DOFnode6);
        Node n7 = new Node(width - 2 * a, height - 2 * a, DOFnode7);
        Node n8 = new Node(width - a, height - 2 * a, DOFnode8);
        Node n9 = new Node(a + a/2., height - 3 * a, DOFnode9);
        Node n10 = new Node(2 * a + a/2., height - 3 * a, DOFnode10);
        Node n11 = new Node(width - 2 * a - a/2., height - 3 * a, DOFnode11);
        Node n12 = new Node(width - a - a/2., height - 3 * a, DOFnode12);
        Node n13 = new Node(2 * a, height - 6 * a, DOFnode13);
        Node n14 = new Node(2 * a + 2 * a/3., height - 6 * a, DOFnode14);
        Node n15 = new Node(2 * a + 2 * a/3. + 2 * a/3., height - 6 * a, DOFnode15);
        Node n16 = new Node(2 * a + 2 * a/3. + 2 * a/3. + 2 * a/3., height - 6 * a, DOFnode16);
        Node n17 = new Node(2 * a + a/2., height - 13 * a, DOFnode17);
        Node n18 = new Node(2 * a + a/2. + a/3., height - 13 * a, DOFnode18);
        Node n19 = new Node(2 * a + a/2. + a/3. + a/3., height - 13 * a, DOFnode19);
        Node n20 = new Node(2 * a + a/2. + a, height - 13 * a, DOFnode20);
        Node n21 = new Node(2 * a + a/2. - a/3., height - 13 * a - a/3., DOFnode21);
        Node n22 = new Node(2 * a + a/2. + a + a/3., height - 13 * a - a/3., DOFnode22);
        Node n23 = new Node(3 * a, height - 15 * a, DOFnode23);
        Node n24 = new Node(3 * a, 0, DOFnode24);


        Beam b1 = new Beam(n1, n5, testMaterial,lumped);
        Beam b2 = new Beam(n2, n6, testMaterial,lumped);
        Beam b3 = new Beam(n3, n7, testMaterial,lumped);
        Beam b4 = new Beam(n4, n8, testMaterial,lumped);
        Beam b5 = new Beam(n5, n6, testMaterial,lumped);
        Beam b6 = new Beam(n6, n7, testMaterial,lumped);
        Beam b7 = new Beam(n7, n8, testMaterial,lumped);
        Beam b8 = new Beam(n5, n9, testMaterial,lumped);
        Beam b9 = new Beam(n6, n10, testMaterial,lumped);
        Beam b10 = new Beam(n7, n11, testMaterial,lumped);
        Beam b11 = new Beam(n8, n12, testMaterial,lumped);
        Beam b12 = new Beam(n9, n10, testMaterial,lumped);
        Beam b13 = new Beam(n10, n11, testMaterial,lumped);
        Beam b14 = new Beam(n11, n12, testMaterial,lumped);
        Beam b15 = new Beam(n9, n13, testMaterial,lumped);
        Beam b16 = new Beam(n10, n14, testMaterial,lumped);
        Beam b17 = new Beam(n11, n15, testMaterial,lumped);
        Beam b18 = new Beam(n12, n16, testMaterial,lumped);
        Beam b19 = new Beam(n13, n14, testMaterial,lumped);
        Beam b20 = new Beam(n14, n15, testMaterial,lumped);
        Beam b21 = new Beam(n15, n16, testMaterial,lumped);
        Beam b22 = new Beam(n13, n17, testMaterial,lumped);
        Beam b23 = new Beam(n14, n18, testMaterial,lumped);
        Beam b24 = new Beam(n15, n19, testMaterial,lumped);
        Beam b25 = new Beam(n16, n20, testMaterial,lumped);
        Beam b26 = new Beam(n17, n18, testMaterial,lumped);
        Beam b27 = new Beam(n18, n19, testMaterial,lumped);
        Beam b28 = new Beam(n19, n20, testMaterial,lumped);
        Beam b29 = new Beam(n17, n21, testMaterial,lumped);
        Beam b30 = new Beam(n20, n22, testMaterial,lumped);
        Beam b31 = new Beam(n21, n23, testMaterial,lumped);
        Beam b32 = new Beam(n22, n23, testMaterial,lumped);
        Beam b33 = new Beam(n23, n24, testMaterial,lumped);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26, b27, b28, b29, b30, b31, b32, b33);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            condof.add(i);
        }

        structure.setConDOF(condof);

        return structure;

    }
    public static Structure getEmpireState() {

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        structure.setLumped(lumped);
        Material testMaterial = new Material();

        List<Integer> DOFnode1 = new LinkedList<>();
        List<Integer> DOFnode2 = new LinkedList<>();
        List<Integer> DOFnode3 = new LinkedList<>();
        List<Integer> DOFnode4 = new LinkedList<>();
        List<Integer> DOFnode5 = new LinkedList<>();
        List<Integer> DOFnode6 = new LinkedList<>();
        List<Integer> DOFnode7 = new LinkedList<>();
        List<Integer> DOFnode8 = new LinkedList<>();
        List<Integer> DOFnode9 = new LinkedList<>();
        List<Integer> DOFnode10 = new LinkedList<>();
        List<Integer> DOFnode11 = new LinkedList<>();
        List<Integer> DOFnode12 = new LinkedList<>();
        List<Integer> DOFnode13 = new LinkedList<>();
        List<Integer> DOFnode14 = new LinkedList<>();
        List<Integer> DOFnode15 = new LinkedList<>();
        List<Integer> DOFnode16 = new LinkedList<>();
        List<Integer> DOFnode17 = new LinkedList<>();
        List<Integer> DOFnode18 = new LinkedList<>();
        List<Integer> DOFnode19 = new LinkedList<>();
        List<Integer> DOFnode20 = new LinkedList<>();
        List<Integer> DOFnode21 = new LinkedList<>();
        List<Integer> DOFnode22 = new LinkedList<>();
        List<Integer> DOFnode23 = new LinkedList<>();
        List<Integer> DOFnode24 = new LinkedList<>();


        DOFnode1.add(0);
        DOFnode1.add(1);
        DOFnode1.add(2);
        DOFnode2.add(3);
        DOFnode2.add(4);
        DOFnode2.add(5);
        DOFnode3.add(6);
        DOFnode3.add(7);
        DOFnode3.add(8);
        DOFnode4.add(9);
        DOFnode4.add(10);
        DOFnode4.add(11);
        DOFnode5.add(12);
        DOFnode5.add(13);
        DOFnode5.add(14);
        DOFnode6.add(15);
        DOFnode6.add(16);
        DOFnode6.add(17);
        DOFnode7.add(18);
        DOFnode7.add(19);
        DOFnode7.add(20);
        DOFnode8.add(21);
        DOFnode8.add(22);
        DOFnode8.add(23);
        DOFnode9.add(24);
        DOFnode9.add(25);
        DOFnode9.add(26);
        DOFnode10.add(27);
        DOFnode10.add(28);
        DOFnode10.add(29);
        DOFnode11.add(30);
        DOFnode11.add(31);
        DOFnode11.add(32);
        DOFnode12.add(33);
        DOFnode12.add(34);
        DOFnode12.add(35);
        DOFnode13.add(36);
        DOFnode13.add(37);
        DOFnode13.add(38);
        DOFnode14.add(39);
        DOFnode14.add(40);
        DOFnode14.add(41);
        DOFnode15.add(42);
        DOFnode15.add(43);
        DOFnode15.add(44);
        DOFnode16.add(45);
        DOFnode16.add(46);
        DOFnode16.add(47);
        DOFnode17.add(48);
        DOFnode17.add(49);
        DOFnode17.add(50);
        DOFnode18.add(51);
        DOFnode18.add(52);
        DOFnode18.add(53);
        DOFnode19.add(54);
        DOFnode19.add(55);
        DOFnode19.add(56);
        DOFnode20.add(57);
        DOFnode20.add(58);
        DOFnode20.add(59);
        DOFnode21.add(60);
        DOFnode21.add(61);
        DOFnode21.add(62);
        DOFnode22.add(63);
        DOFnode22.add(64);
        DOFnode22.add(65);
        DOFnode23.add(66);
        DOFnode23.add(67);
        DOFnode23.add(68);
        DOFnode24.add(69);
        DOFnode24.add(70);
        DOFnode24.add(71);


        Node n1 = new Node(0.000000, 18.000000, DOFnode1);
        Node n2 = new Node(1.000000, 18.000000, DOFnode2);
        Node n3 = new Node(7.000000, 18.000000, DOFnode3);
        Node n4 = new Node(8.000000, 18.000000, DOFnode4);
        Node n5 = new Node(0.000000, 17.000000, DOFnode5);
        Node n6 = new Node(1.000000, 17.000000, DOFnode6);
        Node n7 = new Node(7.000000, 17.000000, DOFnode7);
        Node n8 = new Node(8.000000, 17.000000, DOFnode8);
        Node n9 = new Node(1.000000, 14.000000, DOFnode9);
        Node n10 = new Node(2.000000, 14.000000, DOFnode10);
        Node n11 = new Node(6.000000, 14.000000, DOFnode11);
        Node n12 = new Node(7.000000, 14.000000, DOFnode12);
        Node n13 = new Node(2.000000, 5.000000, DOFnode13);
        Node n14 = new Node(3.000000, 5.000000, DOFnode14);
        Node n15 = new Node(5.000000, 5.000000, DOFnode15);
        Node n16 = new Node(6.000000, 5.000000, DOFnode16);
        Node n17 = new Node(3.000000, 3.000000, DOFnode17);
        Node n18 = new Node(3.500000, 3.000000, DOFnode18);
        Node n19 = new Node(4.500000, 3.000000, DOFnode19);
        Node n20 = new Node(5.000000, 3.000000, DOFnode20);
        Node n21 = new Node(3.500000, 2.000000, DOFnode21);
        Node n22 = new Node(4.000000, 2.000000, DOFnode22);
        Node n23 = new Node(4.500000, 2.000000, DOFnode23);
        Node n24 = new Node(4.000000, 0.000000, DOFnode24);


        Beam b1 = new Beam(n1, n5, testMaterial,lumped);
        Beam b2 = new Beam(n2, n6, testMaterial,lumped);
        Beam b3 = new Beam(n3, n7, testMaterial,lumped);
        Beam b4 = new Beam(n4, n8, testMaterial,lumped);
        Beam b5 = new Beam(n5, n6, testMaterial,lumped);
        Beam b6 = new Beam(n7, n8, testMaterial,lumped);
        Beam b7 = new Beam(n6, n9, testMaterial,lumped);
        Beam b8 = new Beam(n7, n12, testMaterial,lumped);
        Beam b9 = new Beam(n9, n10, testMaterial,lumped);
        Beam b10 = new Beam(n10, n11, testMaterial,lumped);
        Beam b11 = new Beam(n11, n12, testMaterial,lumped);
        Beam b12 = new Beam(n10, n13, testMaterial,lumped);
        Beam b13 = new Beam(n11, n16, testMaterial,lumped);
        Beam b14 = new Beam(n13, n14, testMaterial,lumped);
        Beam b15 = new Beam(n14, n15, testMaterial,lumped);
        Beam b16 = new Beam(n15, n16, testMaterial,lumped);
        Beam b17 = new Beam(n14, n17, testMaterial,lumped);
        Beam b18 = new Beam(n15, n20, testMaterial,lumped);
        Beam b19 = new Beam(n17, n18, testMaterial,lumped);
        Beam b20 = new Beam(n18, n19, testMaterial,lumped);
        Beam b21 = new Beam(n19, n20, testMaterial,lumped);
        Beam b22 = new Beam(n18, n21, testMaterial,lumped);
        Beam b23 = new Beam(n19, n23, testMaterial,lumped);
        Beam b24 = new Beam(n21, n22, testMaterial,lumped);
        Beam b25 = new Beam(n22, n23, testMaterial,lumped);
        Beam b26 = new Beam(n22, n24, testMaterial,lumped);


        structure.addNodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24);
        structure.addBeams(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26);

        List<Integer> condof= new ArrayList<>();
        for (int i = 0; i < 12; i++) {
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
