package de.ferienakademie.smartquake.kernel1;

import android.content.SharedPreferences;
import android.util.Log;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.eigenvalueProblems.GenEig;
import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.managers.PreferenceReader;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.preferenceElements.SliderPreference;

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

    //Modal Analysis part
    private DenseMatrix64F eigenvectorsmatrix;
    private DenseMatrix64F[] eigenvectors;
    private double[] eigenvalues;
    private DenseMatrix64F eigentransposemultMassmatrix; //product of phi^T *M


    private int numberofDOF;

    private double displacementScale = 1.0;

    Structure structure;
    // temporary vectors that will be scaled by acceleration
    private DenseMatrix64F influenceVectorX_temp;
    private DenseMatrix64F influenceVectorY_temp;

    public SpatialDiscretization(Structure structure) {
        this.structure = structure;
        //initialize displacement with zeros
        numberofDOF = structure.getNumberOfDOF();

        influenceVectorX = new DenseMatrix64F(getNumberofDOF(), 1);
        influenceVectorY = new DenseMatrix64F(getNumberofDOF(), 1);

        initializeMatrices();
        calculateInfluenceVector();
        calculateEigenvaluesAndVectors();

        displacementScale = 4.0 * PreferenceReader.getDisplacementScaling() + 1.0;
   }

    /**
     * return StiffnessMatrix
     */
    public DenseMatrix64F getStiffnessMatrix(){
        return StiffnessMatrix;
    }

    /**
     * return DampingMatrix
     */
    public DenseMatrix64F getDampingMatrix(){
        return DampingMatrix;
    }

    /**
     * return MassMatrix
     */
    public DenseMatrix64F getMassMatrix(){
        return MassMatrix;
    }

    /**
     * Calculate the stiffness, mass and damping matrices.
     */
    public void initializeMatrices() {
        StiffnessMatrix = new DenseMatrix64F(getNumberofDOF(), getNumberofDOF());
        MassMatrix = new DenseMatrix64F(getNumberofDOF(), getNumberofDOF());
        DampingMatrix = new DenseMatrix64F(getNumberofDOF(), getNumberofDOF());
        LoadVector = new DenseMatrix64F(getNumberofDOF(), 1);

        StiffnessMatrix.zero();
        MassMatrix.zero();
        DampingMatrix.zero();

        calculateMassMatrix();
        calculateStiffnessMatrix();
        calculateDampingMatrix();
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
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberofDOF(); k++) {
                StiffnessMatrix.set(j,k,0.0);
                StiffnessMatrix.set(k,j,0.0);
            }
            StiffnessMatrix.set(j,j,1.0);
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
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberofDOF(); k++) {
                MassMatrix.set(j,k,0.0);
                MassMatrix.set(k,j,0.0);
            }
            MassMatrix.set(j,j,1.0);
        }
    }

    public void calculateinverseMassMatrix(){
        if (!structure.isLumped()) {
            throw new RuntimeException("No diagonal mass matrix!");
        }
        InverseMassMatrix = new DenseMatrix64F(getNumberofDOF(), getNumberofDOF());
        InverseMassMatrix.zero();
        for (int e = 0; e < getNumberofDOF(); e++) {
            InverseMassMatrix.add(e,e, 1./MassMatrix.get(e,e));
        }
    }

    public DenseMatrix64F getInverseMassMatrix(){ // Only call for lumped cases.
        calculateinverseMassMatrix();
        return InverseMassMatrix;
    }

    public void calculateDampingMatrix() {
        // CommonOps.scale(material.getDampingCoefficient()/material.getMassPerLength(),MassMatrix,DampingMatrix);
        //CommonOps.scale(10,MassMatrix,DampingMatrix);
        double a0 = 4.788640506/10;
        double a1 =0.0001746899608/10;
        CommonOps.add(a0,MassMatrix,a1,StiffnessMatrix,DampingMatrix);
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumberofDOF(); k++) {
                DampingMatrix.set(j,k,0.0);
                DampingMatrix.set(k,j,0.0);
            }
            DampingMatrix.set(j,j,1.0);
        }
    }


   public int getNumberofDOF() {
        return numberofDOF;
   }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void updateStructure_SpatialDiscretization(DenseMatrix64F DisplacementVector) {
        DenseMatrix64F displacementVector2 = DisplacementVector.copy();
        List<Integer> conDOF = structure.getConDOF();
        for(int k=0; k<conDOF.size(); k++){
            displacementVector2.set(conDOF.get(k),0,0);
        }
        for (int e = 0; e < structure.getNodes().size(); e++) {
            Node node = structure.getNodes().get(e);

            List<Integer> dofs = node.getDOF();
            node.setCurrentX(displacementVector2.get(dofs.get(0), 0) * displacementScale + node.getInitialX());
            node.setCurrentY(displacementVector2.get(dofs.get(1), 0) * displacementScale + node.getInitialY());
            for (int j = 2; j < dofs.size(); j++) {
                node.setSingleRotation(j - 2, displacementScale * displacementVector2.get(dofs.get(j), 0));
            }
        }

    }

    public DenseMatrix64F getLoadVector() {
        return LoadVector;
    }

    public void setLoadVector(DenseMatrix64F loadVector) {
        LoadVector = loadVector;
    }

    public void calculateInfluenceVector(){

        influenceVectorX.zero();
        influenceVectorY.zero();
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            List<Integer> DOF = node.getDOF();
            int DOFx = DOF.get(0);
            int DOFy = DOF.get(1);
            influenceVectorX.add(DOFx,0,-1); //add influence vector in x-dir
            influenceVectorY.add(DOFy,0,-1); //add influence vector in y-dir
        }

        influenceVectorX_temp = new DenseMatrix64F(influenceVectorX.getNumRows(),1);
        influenceVectorY_temp = new DenseMatrix64F(influenceVectorY.getNumRows(),1);

    }


    /**
     * Update the vector with forces using the acceleration values received from the {@link AccelerationProvider}
     * @param acceleration - view {@link AccelerationProvider} for details
     */
    public void updateLoadVector(double[] acceleration) {
        CommonOps.scale(acceleration[0]-acceleration[2], influenceVectorX, influenceVectorX_temp);
        CommonOps.scale(acceleration[1]-acceleration[3], influenceVectorY, influenceVectorY_temp);
        CommonOps.addEquals(influenceVectorX_temp, influenceVectorY_temp);
        CommonOps.mult(MassMatrix, influenceVectorX_temp, LoadVector);
    }

    public void updateLoadVectorModalAnalyis(double[] acceleration) {
        calcEigentransposemultMassmatrix();
        CommonOps.scale(acceleration[0], influenceVectorX);
        CommonOps.scale(acceleration[1], influenceVectorY);
        CommonOps.addEquals(influenceVectorX, influenceVectorY);
        CommonOps.mult(eigentransposemultMassmatrix, influenceVectorX, LoadVector);
    }

    public void calculateEigenvaluesAndVectors(){
        DenseMatrix64F K =StiffnessMatrix.copy();
        DenseMatrix64F M =MassMatrix.copy();
        GenEig eigen = new GenEig(K,M); //solve GEN eigenvalues problem
        eigenvalues = eigen.getLambda();
        double[][] ev = eigen.getV();
        eigenvectorsmatrix = new DenseMatrix64F(ev);
        CommonOps.transpose(eigenvectorsmatrix,eigenvectorsmatrix); //transpose due to constructor of DenseMatrix64F in which rows and column are switched
    }


    //Normalise eigenvectors
    public void normaliseEigenvectors() {
        for (int i = 0; i < getNumberofDOF(); i++) {
            CommonOps.scale(1 / Math.sqrt(MassMatrix.get(i, i)), eigenvectors[i]);
        }
    }

    public void performModalAnalysis(){


        calculateEigenvaluesAndVectors();

        DenseMatrix64F eigenvectorsDenseTranspose = new DenseMatrix64F(getNumberofDOF());
        CommonOps.transpose(eigenvectorsmatrix,eigenvectorsDenseTranspose);

        CommonOps.columnsToVector(eigenvectorsmatrix,eigenvectors);

        DenseMatrix64F temp = new DenseMatrix64F(getNumberofDOF());
        CommonOps.mult(eigenvectorsDenseTranspose,MassMatrix,temp);
        CommonOps.mult(temp,eigenvectorsmatrix,MassMatrix); //massmatrix converted into Eigenvectorspace

        normaliseEigenvectors();


    }
    public void getModalAnalysisMatrices(){
        performModalAnalysis();
        StiffnessMatrix.zero();
        MassMatrix.zero();
        for (int i = 0; i < numberofDOF; i++) {
            StiffnessMatrix.set(i,i,eigenvalues[i]);

            MassMatrix.set(i,i,1.0);
        }
    }
    public void calcEigentransposemultMassmatrix(){
        DenseMatrix64F eigenvectorsDenseTranspose = new DenseMatrix64F(getNumberofDOF());
        CommonOps.transpose(eigenvectorsmatrix,eigenvectorsDenseTranspose);
        CommonOps.mult(eigenvectorsDenseTranspose,MassMatrix,eigentransposemultMassmatrix);
    }



    public void superimposeModalAnalyisSolutions(double[] modalSolutionvector){
        DenseMatrix64F DisplacementVector = new DenseMatrix64F();
        DisplacementVector.zero();
        for (int i = 0; i < numberofDOF; i++) {
            CommonOps.add(eigenvectors[i],modalSolutionvector[i],DisplacementVector);
        }

        updateStructure_SpatialDiscretization(DisplacementVector);


    }
}
