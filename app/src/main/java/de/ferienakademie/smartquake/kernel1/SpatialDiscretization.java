package de.ferienakademie.smartquake.kernel1;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.List;

import de.ferienakademie.smartquake.eigenvalueProblems.GenEig;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.managers.PreferenceReader;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by alex on 22.09.16.
 */
public class SpatialDiscretization {

    private DenseMatrix64F StiffnessMatrix;
    private DenseMatrix64F DampingMatrix;
    private DenseMatrix64F MassMatrix;
    private DenseMatrix64F InverseMassMatrix;
    private DenseMatrix64F LoadVector; // vector with the forces

    private DenseMatrix64F influenceVectorX;
    private DenseMatrix64F influenceVectorY;
    //private DenseMatrix64F DisplacementVector;  //project manager advice

    private double dampingCoefficient;

    //Modal Analysis part
    private DenseMatrix64F eigenvectorsmatrix;
    private DenseMatrix64F[] eigenvectors;
    private double[] eigenvalues;
    private DenseMatrix64F StiffnessMatrixModalAnalysis;
    private DenseMatrix64F MassMatrixModalAnalysis;
    private DenseMatrix64F DampingMatrixModalAnalysis;
    private double[] ReducedEigenvalues;
    private DenseMatrix64F redLoadVectorModalAnalysis;
    private  DenseMatrix64F[] Reducedeigenvectors;
    private DenseMatrix64F ReducedeigenvectorsMatrixTranspose;
    private DenseMatrix64F RedinfluenceVectorX;
    private DenseMatrix64F RedinfluenceVectorY;
    private DenseMatrix64F RedinfluenceVectorX_temp;
    private DenseMatrix64F RedinfluenceVectorY_temp;
    double a0;
    double a1;
    private int numberofDOF;

    Structure structure;
    // temporary vectors that will be scaled by acceleration
    private DenseMatrix64F influenceVectorX_temp;
    private DenseMatrix64F influenceVectorY_temp;

    public SpatialDiscretization(Structure structure) {
        this.structure = structure;
        //initialize displacement with zeros
        numberofDOF = structure.getNumberOfDOF();

        influenceVectorX = new DenseMatrix64F(getNumberOfDOF(), 1);
        influenceVectorY = new DenseMatrix64F(getNumberOfDOF(), 1);

        initializeMatrices();
        calculateInfluenceVector();
        calculateEigenvaluesAndVectors();

        dampingCoefficient = PreferenceReader.getDampingCoefficient();

        calculateDampingMatrix();
    }

    /**
     * return StiffnessMatrix
     */
    public DenseMatrix64F getStiffnessMatrix() {
        return StiffnessMatrix;
    }

    /**
     * return DampingMatrix
     */
    public DenseMatrix64F getDampingMatrix() {
        return DampingMatrix;
    }

    /**
     * return MassMatrix
     */
    public DenseMatrix64F getMassMatrix() {
        return MassMatrix;
    }

    /**
     * Calculate the stiffness, mass and damping matrices.
     */
    public void initializeMatrices() {
        StiffnessMatrix = new DenseMatrix64F(getNumberOfDOF(), getNumberOfDOF());
        MassMatrix = new DenseMatrix64F(getNumberOfDOF(), getNumberOfDOF());
        DampingMatrix = new DenseMatrix64F(getNumberOfDOF(), getNumberOfDOF());
        LoadVector = new DenseMatrix64F(getNumberOfDOF(), 1);

        StiffnessMatrix.zero();
        MassMatrix.zero();


        calculateMassMatrix();
        calculateStiffnessMatrix();

    }


    public void calculateStiffnessMatrix() {
        for (int e = 0; e < structure.getBeams().size(); e++) {
            Beam beam = structure.getBeams().get(e);
            int[] dofs = beam.getDofs();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    StiffnessMatrix.add(dofs[i], dofs[j], beam.getElementStiffnessMatrix_globalized().get(i, j));
                }
            }
        }
        for (int i = 0; i < structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberOfDOF(); k++) {
                StiffnessMatrix.set(j, k, 0.0);
                StiffnessMatrix.set(k, j, 0.0);
            }
            StiffnessMatrix.set(j, j, 11.0);
        }
    }

    public void calculateMassMatrix() {
        for (int e = 0; e < structure.getBeams().size(); e++) {
            Beam beam = structure.getBeams().get(e);
            int[] dofs = beam.getDofs();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    MassMatrix.add(dofs[i], dofs[j], beam.getElementMassMatrix_globalized().get(i, j));
                }
            }
        }

        for (int k = 0; k < structure.getNodes().size() ; k++) {
            MassMatrix.add(3*k,3*k,structure.getNodes().get(k).getNodeMass());
            MassMatrix.add(3*k+1,3*k+1,structure.getNodes().get(k).getNodeMass());
        }

        for (int i = 0; i < structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberOfDOF(); k++) {
                MassMatrix.set(j, k, 0.0);
                MassMatrix.set(k, j, 0.0);
            }
            MassMatrix.set(j, j, -1.0);
        }
    }

    public void calculateInverseMassMatrix() {
        if (!structure.isLumped()) {
            throw new RuntimeException("No diagonal mass matrix!");
        }
        InverseMassMatrix = new DenseMatrix64F(getNumberOfDOF(), getNumberOfDOF());
        InverseMassMatrix.zero();
        for (int e = 0; e < getNumberOfDOF(); e++) {
            InverseMassMatrix.add(e, e, 1. / MassMatrix.get(e, e));
        }
    }

    public DenseMatrix64F getInverseMassMatrix() { // Only call for lumped cases.
        calculateInverseMassMatrix();
        return InverseMassMatrix;
    }

    public void calculateDampingMatrix() {
        DampingMatrix.zero();
        double omega1 = ReducedEigenvalues[0];
        double omega2 = ReducedEigenvalues[1];

        double a0 =  2 * dampingCoefficient * omega1 * omega2 / (omega1 + omega2);
        double a1 = 2 * dampingCoefficient / (omega1 + omega2);
        CommonOps.add(a0, MassMatrix, a1, StiffnessMatrix, DampingMatrix);
        for (int i = 0; i < structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberOfDOF(); k++) {
                DampingMatrix.set(j, k, 0.0);
                DampingMatrix.set(k, j, 0.0);
            }
            DampingMatrix.set(j, j, 1.0);
        }
    }

    public void calculateModalAnalysisDampingMatrix() {
        DampingMatrix.zero();
        double omega1 = ReducedEigenvalues[0];
        double omega2 = ReducedEigenvalues[1];
        double xi = 0.05;
        a0 =  2 * xi * omega1 * omega2 / (omega1 + omega2);
        a1 = 2 * xi / (omega1 + omega2);
        CommonOps.add(a0, MassMatrixModalAnalysis, a1, StiffnessMatrixModalAnalysis, DampingMatrixModalAnalysis);

    }

    public int getNumberOfDOF() {
        return numberofDOF;
    }

    public int getNumberOfUnconstraintDOF() {
        return getNumberOfDOF() - structure.getConDOF().size();
    }

    public Structure getStructure() {
        return structure;
    }


    public void setStructure(Structure structure) {
        this.structure = structure;
    }


    public void updateDisplacementsOfStructure(DenseMatrix64F displacementVector, double[] groundDisplacement) {

        // list of constrained dofs
        List<Integer> conDOF = structure.getConDOF();

        DenseMatrix64F displacementVector2 = displacementVector.copy();

        for (int k = 0; k < conDOF.size(); k++) {
            displacementVector2.set(conDOF.get(k), 0, 0);
        }

        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);

            List<Integer> dofsOfNode = node.getDOF();

            for (int j = 0; j < dofsOfNode.size(); j++) {
                node.setSingleDisplacement(j, displacementVector2.get(dofsOfNode.get(j), 0));
            }
            node.saveTimeStepGroundDisplacement(groundDisplacement);
            node.saveTimeStepDisplacement();
        }

    }


    public DenseMatrix64F getLoadVector() {
        return LoadVector;
    }

    public void setLoadVector(DenseMatrix64F loadVector) {
        LoadVector = loadVector;
    }

    public void calculateInfluenceVector() {

        influenceVectorX.zero();
        influenceVectorY.zero();
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            List<Integer> DOF = node.getDOF();
            int DOFx = DOF.get(0);
            int DOFy = DOF.get(1);
            influenceVectorX.add(DOFx, 0, 1); //add influence vector in x-dir
            influenceVectorY.add(DOFy, 0, 1); //add influence vector in y-dir
        }

        influenceVectorX_temp = new DenseMatrix64F(influenceVectorX.getNumRows(), 1);
        influenceVectorY_temp = new DenseMatrix64F(influenceVectorY.getNumRows(), 1);

    }


    /**
     * Update the vector with forces using the acceleration values received from the {@link AccelerationProvider}
     *
     * @param acceleration - view {@link AccelerationProvider} for details
     */
    public void updateLoadVector(double[] acceleration) {
        if (PreferenceReader.includeGravity()) {
            CommonOps.scale(-acceleration[0] - acceleration[2], influenceVectorX, influenceVectorX_temp);
            CommonOps.scale(-acceleration[1] + acceleration[3], influenceVectorY, influenceVectorY_temp);
            CommonOps.addEquals(influenceVectorX_temp, influenceVectorY_temp);
            CommonOps.mult(MassMatrix, influenceVectorX_temp, LoadVector);
        } else {
            CommonOps.scale(-acceleration[0], influenceVectorX, influenceVectorX_temp);
            CommonOps.scale(-acceleration[1], influenceVectorY, influenceVectorY_temp);
            CommonOps.addEquals(influenceVectorX_temp, influenceVectorY_temp);
            CommonOps.mult(MassMatrix, influenceVectorX_temp, LoadVector);
        }


    }


    public void updateLoadVectorModalAnalysis(double[] acceleration) {

        redLoadVectorModalAnalysis = new DenseMatrix64F(getNumberOfUnconstraintDOF(),1);
        if (PreferenceReader.includeGravity()) {

            CommonOps.scale(-acceleration[0]- acceleration[2], RedinfluenceVectorX, RedinfluenceVectorX_temp); //influenceVectorX_temp
            CommonOps.scale(-acceleration[1]- acceleration[3], RedinfluenceVectorY, RedinfluenceVectorY_temp);
            CommonOps.addEquals(RedinfluenceVectorX_temp, RedinfluenceVectorY_temp);
        } else {
            CommonOps.scale(-acceleration[0], RedinfluenceVectorX, RedinfluenceVectorX_temp);
            CommonOps.scale(-acceleration[1], RedinfluenceVectorY, RedinfluenceVectorY_temp);
            CommonOps.addEquals(RedinfluenceVectorX_temp, RedinfluenceVectorY_temp);
        }

        CommonOps.mult(ReducedeigenvectorsMatrixTranspose, RedinfluenceVectorX_temp, redLoadVectorModalAnalysis);

    }


    public void calculateEigenvaluesAndVectors() {
        GenEig eigen = new GenEig(StiffnessMatrix, MassMatrix); //solve GEN eigenvalues problem
        eigenvalues = eigen.getLambda();
        double[][] ev = eigen.getV();
        eigenvectorsmatrix = new DenseMatrix64F(ev);
        CommonOps.transpose(eigenvectorsmatrix, eigenvectorsmatrix); //transpose due to constructor of DenseMatrix64F in which rows and column are switched
        eigenvectors = CommonOps.columnsToVector(eigenvectorsmatrix, null);
        ReducedEigenvalues = new double[getNumberOfUnconstraintDOF()];

        Reducedeigenvectors = new DenseMatrix64F[getNumberOfUnconstraintDOF()];

        RedinfluenceVectorX = new DenseMatrix64F(getNumberOfUnconstraintDOF(),1);
        RedinfluenceVectorY = new DenseMatrix64F(getNumberOfUnconstraintDOF(),1);
        RedinfluenceVectorX_temp  = new DenseMatrix64F(getNumberOfUnconstraintDOF(),1);
        RedinfluenceVectorY_temp  = new DenseMatrix64F(getNumberOfUnconstraintDOF(),1);


        for (int i = 0; i < getNumberOfUnconstraintDOF(); i++) {
            Reducedeigenvectors[i]=new DenseMatrix64F(getNumberOfUnconstraintDOF());
        }
        int counter =0;
        // Throw away eigenvectors that belong to constraint frequencies
        for (int i = 0; i < numberofDOF; i++) {

            if (eigenvalues[i]<0){
                continue;
            }else {
                ReducedEigenvalues[counter]=eigenvalues[i];
                Reducedeigenvectors[counter] = eigenvectors[i];
                RedinfluenceVectorX.set(counter,0,influenceVectorX.get(i,0));
                RedinfluenceVectorY.set(counter,0,influenceVectorY.get(i,0));
                RedinfluenceVectorY_temp.set(counter,0,influenceVectorY_temp.get(i,0));
                RedinfluenceVectorX_temp.set(counter,0,influenceVectorX_temp.get(i,0));
                counter++;
            }
        }
        double[][] temporary = new double[getNumberOfUnconstraintDOF()][getNumberOfUnconstraintDOF()];
        // Throw away eigenvector entries that belong to constraint dofs.

        for (int j = 0; j < getNumberOfUnconstraintDOF(); j++) {
            int counter_k=0;
            for (int k = 0; k < numberofDOF; k++) {
                boolean isConstraint = false;
                for (int i: structure.getConDOF()) {
                    if (k == i) {
                        isConstraint = true;
                        break;
                    }
                }
                if (!isConstraint) {
                    temporary[j][counter_k++] = Reducedeigenvectors[j].get(k);
                }
            }
        }
        ReducedeigenvectorsMatrixTranspose = new DenseMatrix64F(temporary);
    }


    //Normalise eigenvectors
    public void normaliseEigenvectors() {
        for (int i = 0; i < getNumberOfDOF(); i++) {
            CommonOps.scale(1 / Math.sqrt(MassMatrix.get(i, i)), eigenvectors[i]);
        }
    }



    public void calculateModalAnalysisMatrices(){
        normaliseEigenvectors();
        StiffnessMatrixModalAnalysis = new DenseMatrix64F(getNumberOfUnconstraintDOF(), getNumberOfUnconstraintDOF());
        MassMatrixModalAnalysis = new DenseMatrix64F(getNumberOfUnconstraintDOF(), getNumberOfUnconstraintDOF());
        DampingMatrixModalAnalysis = new DenseMatrix64F(getNumberOfUnconstraintDOF(), getNumberOfUnconstraintDOF());

        for (int i = 0; i < getNumberOfUnconstraintDOF(); i++) {
            StiffnessMatrixModalAnalysis.set(i,i,ReducedEigenvalues[i]);
            MassMatrixModalAnalysis.set(i,i,1.0);
        }
        calculateModalAnalysisDampingMatrix();


    }
    public void superimposeModalAnalysisSolutions(DenseMatrix64F modalSolutionvector, double[] groundDisplacement){
        DenseMatrix64F DisplacementVector = new DenseMatrix64F(numberofDOF, 1);
        DenseMatrix64F solVecCopy = new DenseMatrix64F(getNumberOfUnconstraintDOF(), 1);

        CommonOps.multTransA(1, ReducedeigenvectorsMatrixTranspose, modalSolutionvector, solVecCopy);


        // Extend displacements by inserting zeros in the position of constraint dofs
        for (int i = 0; i < getNumberOfUnconstraintDOF(); i++) {
            int disp = 0;
            for (int k: structure.getConDOF()) {
                if (k <= i) {
                    disp++;
                }
            }
            DisplacementVector.set(i+disp, 0, solVecCopy.get(i, 0));
        }
        updateDisplacementsOfStructure(DisplacementVector, groundDisplacement);

    }

    public DenseMatrix64F getDampingMatrixModalAnalysis() {
        return DampingMatrixModalAnalysis;
    }

    public DenseMatrix64F getRedLoadVectorModalAnalysis() {
        return redLoadVectorModalAnalysis;
    }


    public DenseMatrix64F getMassMatrixModalAnalysis() {
        return MassMatrixModalAnalysis;
    }


    public DenseMatrix64F getStiffnessMatrixModalAnalysis() {
        return StiffnessMatrixModalAnalysis;
    }

}
