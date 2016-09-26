package de.ferienakademie.smartquake.model;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;


        Node n1 = new Node(0, height, true);
        Node n2 = new Node(width, height, true);
        Node n3 = new Node(width, height - half, true);
        Node n4 = new Node(0, height - half, true);
        Node n5 = new Node(half, height - 2 * half, true);

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
        Material testMaterial = Material.STEEL;

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

        //t1.setHinge(true);
        //t2.setHinge(true);
        //t3.setHinge(true);
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

    public static Structure getTrumpTower() {
        double width = 8;
        double height = 8;
        double height2 = 2; //shouldn't be bigger than 4
        double height3 = 1; //shoudln't be bigger than 4

        boolean lumped = true; // Make it false for consistent mass matrices!

        Structure structure = new Structure();
        Material testMaterial = Material.STEEL;

        //Ground
        Node n1 = new Node(width/3, height);
        Node n2 = new Node(4*width/9, height);
        Node n3 = new Node(5*width/9, height);
        Node n4 = new Node(2*width/3, height);
        //Left thing
        Node n5 = new Node(8*width/27, height-height2/3);
        Node n6 = new Node(8*width/27, height-2*height2/3);
        Node n7 = new Node(10*width/27, height-height2);
        Node n8 = new Node(4*width/9, height-2*height2/3);
        //Right thing
        Node n9 = new Node(19*width/27, height-height2/3);
        Node n10 = new Node(19*width/27, height-2*height2/3);
        Node n11 = new Node(17*width/27, height-height2);
        Node n12 = new Node(5*width/9, height-2*height2/3);
        //Middle thing
        Node n13 = new Node(4*width/9, height/2);
        Node n14 = new Node(5*width/9, height/2);
        Node n15 = new Node(4*width/9, height3);
        Node n16 = new Node(5*width/9, height3);
        Node n17 = new Node(11*width/27, height3);
        Node n18 = new Node(16*width/27, height3);


        Node n19 = new Node(12.25*width/27, height3/3);
        Node n20 = new Node(width/2, 0);
        Node n21 = new Node(14.75*width/27, height3/3);

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
        Beam b11 = new Beam(n8, n13, testMaterial);
        Beam b12 = new Beam(n12, n14, testMaterial);
        Beam b13 = new Beam(n13, n15, testMaterial);
        Beam b14 = new Beam(n14, n16, testMaterial);
        Beam b15 = new Beam(n15, n17, testMaterial);
        Beam b16 = new Beam(n16, n18, testMaterial);
        Beam b17 = new Beam(n17, n19, testMaterial);
        Beam b18 = new Beam(n18, n21, testMaterial);
        Beam b19 = new Beam(n19, n20, testMaterial);
        Beam b20 = new Beam(n20, n21, testMaterial);

        structure.addNodes(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,n21);
        structure.addBeams(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20);

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



    public static Structure getStructure(Context context, String structureName) {

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

            // dofs am Knoten setzen
            node.setDOF(dofs);
            structure.setNumberOfDOF(numberofDOF);
        }

    }

}
