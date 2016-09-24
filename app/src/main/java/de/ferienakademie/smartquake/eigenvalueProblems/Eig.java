package de.ferienakademie.smartquake.eigenvalueProblems;


import java.util.ArrayList;
import org.netlib.lapack.Dgeev;
import org.netlib.util.intW;

public class Eig {
    /**
     * Solution of eigenvalue problem (AreaOfCrossSection * v = lambda * v)
     * using LAPACK function dgeev
     *
     * Author: Vincent Stimper
     *
     * Date: 31. 8. 2016
     *
     *
     * Arguments
     * 		a			input matrix
     *
     * Attributes
     * 		lambda		eigenvalues, a * v = lambda * v
     * 		v			matrix with right eigenvectors as columns,
     * 					in the same order as their eigenvalues
     * 		n			number of dimensions
     */

	/* Attributes */
    private ArrayList<Double> lambdaRe;
    private ArrayList<Double> lambdaIm;
    private ArrayList<Double> v;
    private int n;

    /* Constructor */
    public Eig(double[][] a) {
		/* Initialize local variables */
        n = a.length;
        double[] aV = Eig.matToVec(a);
        double[] lambdaReD = new double[n];
        double[] lambdaImD = new double[n];
        double[] vR = new double[n * n];
        double[] vL = new double[n * n];
        double[] work = new double[8 * n];
        intW info = new intW(0);

		/* Solve generalized eigenvalue problem */
        Dgeev.dgeev("N", "V", n, aV, 0, n, lambdaReD, 0, lambdaImD, 0, vL, 0, n, vR, 0, n, work, 0, 8 * n, info);

		/* Assign results to attributes */
        lambdaRe = Eig.vecToArrayList(lambdaReD);
        lambdaIm = Eig.vecToArrayList(lambdaImD);
        v = Eig.vecToArrayList(vR);
    }

    /* Getter methods */
    public double[] getLambdaRe() {
        /** Returns real part of lambda */
        return Eig.arrayListToVec(lambdaRe);
    }
    public double[] getLambdaIm() {
        /** Returns imaginary part of lambda */
        return Eig.arrayListToVec(lambdaIm);
    }
    public double[] getLambda() {
        /** Returns real part of lambda */
        return Eig.arrayListToVec(lambdaRe);
    }
    public double[][] getV() {
        return Eig.arrayListToMat(v, n);
    }

    /* Functions */
    private static double[] matToVec(double[][] m) {
		/* Returns matrix MassPerLength as vector of columns */
        int d = m.length;
        double[] mV = new double[d * d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                mV[i + j * d] = m[i][j];
            }
        }
        return mV;
    }
    private static ArrayList<Double> vecToArrayList(double[] v) {
		/* Returns vector v as ArrayList */
        int d = v.length;
        ArrayList<Double> aL = new ArrayList<Double>(d);
        for (int i = 0; i < d; i++) {
            aL.add(v[i]);
        }
        return aL;
    }
    private static double[] arrayListToVec(ArrayList<Double> aL) {
		/* Returns ArrayList aL as vector */
        int d = aL.size();
        double[] v = new double[d];
        for (int i = 0; i < d; i++) {
            v[i] = aL.get(i);
        }
        return v;
    }
    private static double[][] arrayListToMat(ArrayList<Double> aL, int d) {
		/* Returns ArrayList aL as matrix
		 * 	d	number of dimension of matrix */
        double[][] m = new double[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                m[i][j] = aL.get(i + j * d);
            }
        }
        return m;
    }

}