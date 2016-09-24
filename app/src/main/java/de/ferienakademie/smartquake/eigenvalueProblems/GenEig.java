package de.ferienakademie.smartquake.eigenvalueProblems;

import java.util.ArrayList; // Variable size array to handle arbitrary dimension of input matrices
import org.netlib.util.intW;
import org.netlib.lapack.Dggev;

public class GenEig {
	/**
     *
     * Solution of generalized eigenvalue problem (A * v = lambda * B * v)
     * using LAPACK function dggev
     *
     * Author: Vincent Stimper
     *
     * Date: 30. 8. 2016
     *
     *
	 * Arguments
	 * 		a, b						input matrices
	 *
	 * Attributes
	 * 		alphaRe, alphaIm, beta		generalized eigenvalues, beta * a * v = alpha * b * v
	 * 		v							matrix with right generalized eigenvectors as columns,
	 * 									in the same order as their eigenvalues
	 * 		n							number of dimensions
	 */

    /* Attributes */
    private ArrayList<Double> alphaRe;
    private ArrayList<Double> alphaIm;
    private ArrayList<Double> beta;
    private ArrayList<Double> v;
    private int n;

    /* Constructor */
    public GenEig(double[][] a, double[][] b) {
		/* Initialize local variables */
        n = a.length;
        double[] aV = GenEig.matToVec(a);
        double[] bV = GenEig.matToVec(b);
        double[] alphaReD = new double[n];
        double[] alphaImD = new double[n];
        double[] betaD = new double[n];
        double[] vR = new double[n * n];
        double[] vL = new double[n * n];
        double[] work = new double[8 * n];
        intW info = new intW(0);

		/* Solve generalized eigenvalue problem */
        Dggev.dggev("N", "V", n, aV, 0, n, bV, 0, n, alphaReD, 0, alphaImD, 0, betaD, 0, vL, 0, n, vR, 0, n, work, 0, 8 * n, info);

		/* Assign results to attributes */
        alphaRe = GenEig.vecToArrayList(alphaReD);
        alphaIm = GenEig.vecToArrayList(alphaImD);
        beta = GenEig.vecToArrayList(betaD);
        v = GenEig.vecToArrayList(vR);
    }

    public GenEig(double[] aV, double[] bV) {
		/* Initialize local variables */
        n = aV.length;
        double[] alphaReD = new double[n];
        double[] alphaImD = new double[n];
        double[] betaD = new double[n];
        double[] vR = new double[n * n];
        double[] vL = new double[n * n];
        double[] work = new double[8 * n];
        intW info = new intW(0);

		/* Solve generalized eigenvalue problem */
        Dggev.dggev("N", "V", n, aV, 0, n, bV, 0, n, alphaReD, 0, alphaImD, 0, betaD, 0, vL, 0, n, vR, 0, n, work, 0, 8 * n, info);

		/* Assign results to attributes */
        alphaRe = GenEig.vecToArrayList(alphaReD);
        alphaIm = GenEig.vecToArrayList(alphaImD);
        beta = GenEig.vecToArrayList(betaD);
        v = GenEig.vecToArrayList(vR);
    }

    /* Getter methods */
    public double[] getAlphaRe() {
        /** Returns real part of alpha */
        return GenEig.arrayListToVec(alphaRe);
    }
    public double[] getAlphaIm() {
        /** Return imaginary part of alpha */
        return GenEig.arrayListToVec(alphaIm);
    }
    public double[] getBeta() {
        /** Return beta */
        return GenEig.arrayListToVec(beta);
    }
    public double[][] getV() {
        /** Return eigenvectors */
        return GenEig.arrayListToMat(v, n);
    }
    public double[] getLambdaRe() {
        /** Returns real part of lambda if beta != 0 */
        double[] betaD = GenEig.arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return GenEig.vecElDiv(GenEig.arrayListToVec(alphaRe), betaD);
    }
    public double[] getLambda() {
        /** Returns real part of lambda if beta != 0 *
         *  Warning: Only use this method if you are sure by theory that the eigenvalues are real! */
        double[] betaD = GenEig.arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return GenEig.vecElDiv(GenEig.arrayListToVec(alphaRe), betaD);
    }
    public double[] getLambdaIm() {
        /** Returns imaginary part of lambda if beta != 0 */
        double[] betaD = GenEig.arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return GenEig.vecElDiv(GenEig.arrayListToVec(alphaIm), betaD);
    }

    /* Functions */
    private static double[] matToVec(double[][] m) {
		/* Returns matrix m as vector of columns */
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
    private static double[] vecElDiv(double[] v1, double[] v2) {
		/* v1 ./ v2 */
        int d = v1.length;
        double[] v3 = new double[d];
        for (int i = 0; i < d; i++) {
            v3[i] = v1[i] / v2[i];
        }
        return v3;
    }
    private static boolean anyZero(double[] v) {
		/* true if one element of v is 0, false otherwise */
        boolean aZ = false;
        int i = 0;
        while (!aZ && i < v.length) {
            if (v[i] == 0) {
                aZ = true;
            }
            i++;
        }
        return aZ;
    }

}
