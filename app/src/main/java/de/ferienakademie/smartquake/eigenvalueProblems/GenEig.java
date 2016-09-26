package de.ferienakademie.smartquake.eigenvalueProblems;

import java.util.ArrayList; // Variable size array to handle arbitrary dimension of input matrices

import org.ejml.data.DenseMatrix64F;
import org.netlib.util.intW;
import org.netlib.lapack.Dggev;

public class GenEig {
	/**
     *
     * Solution of generalized eigenvalue problem (AreaOfCrossSection * v = lambda * B * v)
     * using LAPACK function dggev
     *
     * Author: Vincent Stimper
     *
     * Date: 30. 8. 2016
     *
     *
	 * Arguments
	 * 		a, BreadthOfBeam						input matrices
	 *
	 * Attributes
	 * 		alphaRe, alphaIm, beta		generalized eigenvalues, beta * a * v = alpha * BreadthOfBeam * v
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
        this(matToVec(a),matToVec(b),a.length, true);
    }


    public GenEig(DenseMatrix64F a, DenseMatrix64F b) {
        this(a.getData(),b.getData(),a.getNumRows(), true);
    }

    public GenEig(double[] aV, double[] bV,int n, boolean sortEigenvalues) {
		/* Initialize local variables */
        this.n = n;
        double[] alphaReD = new double[n];
        double[] alphaImD = new double[n];
        double[] betaD = new double[n];
        double[] vR = new double[n * n];
        double[] vL = new double[n * n];
        double[] work = new double[8 * n];
        double[] aVWork = aV.clone();
        double[] bVWork = bV.clone();
        intW info = new intW(0);

		/* Solve generalized eigenvalue problem */
        Dggev.dggev("N", "V", n, aVWork, 0, n, bVWork, 0, n, alphaReD, 0, alphaImD, 0, betaD, 0, vL, 0, n, vR, 0, n, work, 0, 8 * n, info);

        /* Sort eigenvalues from smallest to biggest */
        if (sortEigenvalues && n > 1) {
            if (anyZero(betaD) && (! anyZero(alphaImD))) {
                System.out.println("Warning: At least one eigenvalue is complex or arbitrary. Hence, the eigenvalues cannot be sorted.");
            } else {
                double[] lambda = vecElDiv(alphaReD, betaD);
                double min;
                int minInd;
                for (int i = 0; i < n - 1; i++) {
                    min = lambda[i];
                    minInd = i;
                    for (int j = i + 1; j < n; j++) {
                        if (lambda[j] < min) {
                            min = lambda[j];
                            minInd = j;
                        }
                    }
                    lambda = switchElements(lambda, i, minInd);
                    alphaReD = switchElements(alphaReD, i, minInd);
                    betaD = switchElements(betaD, i, minInd);
                    for (int k = 0; k < n; k++) {
                        vR = switchElements(vR, i * n + k, minInd * n + k);
                    }
                }
            }
        }

        /* Assign results to attributes */
        alphaRe = vecToArrayList(alphaReD);
        alphaIm = vecToArrayList(alphaImD);
        beta = vecToArrayList(betaD);
        v = vecToArrayList(vR);
    }

    /* Getter methods */
    public double[] getAlphaRe() {
        /** Returns real part of alpha */
        return arrayListToVec(alphaRe);
    }
    public double[] getAlphaIm() {
        /** Return imaginary part of alpha */
        return arrayListToVec(alphaIm);
    }
    public double[] getBeta() {
        /** Return beta */
        return arrayListToVec(beta);
    }
    public double[][] getV() {
        /** Return eigenvectors */
        return arrayListToMat(v, n);
    }

    public double[] getV1D() {
        /** Return eigenvectors */
        return arrayListToVec(v);
    }

    public double[] getLambdaRe() {
        /** Returns real part of lambda if beta != 0 */
        double[] betaD = arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return vecElDiv(arrayListToVec(alphaRe), betaD);
    }
    public double[] getLambda() {
        /** Returns real part of lambda if beta != 0 *
         *  Warning: Only use this method if you are sure by theory that the eigenvalues are real! */
        double[] betaD = arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return vecElDiv(arrayListToVec(alphaRe), betaD);
    }
    public double[] getLambdaIm() {
        /** Returns imaginary part of lambda if beta != 0 */
        double[] betaD = arrayListToVec(beta);
        if (anyZero(betaD)) {
            System.out.println("Error: At least one generalized eigenvalue is either arbitrary or not defined.");
            System.exit(0);
        }
        return vecElDiv(arrayListToVec(alphaIm), betaD);
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
    private static boolean allZero(double[] v) {
		/* true if all elements of v are 0, false otherwise */
        boolean aZ = true;
        int i = 0;
        while (aZ && i < v.length) {
            if (v[i] != 0) {
                aZ = false;
            }
            i++;
        }
        return aZ;
    }
    private static double[] switchElements(double[] vec, int index1, int index2) {
        double temp = vec[index1];
        vec[index1] = vec[index2];
        vec[index2] = temp;
        return vec;
    }

}
