package de.ferienakademie.smartquake.kernel1;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.List;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by alex on 22.09.16.
 */
public class Kernel1 {

    private DenseMatrix64F StiffnessMatrix;
    private DenseMatrix64F DampingMatrix;
    private DenseMatrix64F MassMatrix;

    private DenseMatrix64F LoadVector; // vector with the forces
    private DenseMatrix64F influenceVectorx;
    private DenseMatrix64F influenceVectory;
    private DenseMatrix64F DisplacementVector;  //project manager advice


    private int numDOF;

    Structure structure;

    public Kernel1(Structure structure) {
        this.structure = structure;
        //initialize displacement with zeros
        DisplacementVector = new DenseMatrix64F(getNumDOF(), 1);
        DisplacementVector.zero();
        numDOF = structure.getNodes().size()*3;      //TODO Alex: temporary solution. Changes if we add hinges.
        initMatrices();
        calcInfluenceVector();
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
    public void initMatrices() {
        StiffnessMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());
        MassMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());
        DampingMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());
        LoadVector = new DenseMatrix64F(getNumDOF(), 1);

        StiffnessMatrix.zero();
        MassMatrix.zero();
        DampingMatrix.zero();

        calcDampingMatrix();
        calclumpedMassMatrix();
        calcStiffnessMatrix();
    }

    public void calcStiffnessMatrix() {
        for (int e = 0; e < structure.getBeams().size(); e++) {
            Beam beam = structure.getBeams().get(e);
            int[] dofs = beam.getDofs();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    StiffnessMatrix.add(dofs[i], dofs[j], beam.getEleStiffnessMatrix_globalized().get(i, j));
                }
            }
        }
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumDOF(); k++) {
                StiffnessMatrix.set(j,k,0.0);
                StiffnessMatrix.set(k,j,0.0);
            }
            StiffnessMatrix.set(j,j,1.0);
        }
    }

    public void calclumpedMassMatrix() {
        for (int e = 0; e < structure.getBeams().size(); e++) {
            Beam beam = structure.getBeams().get(e);
            int[] dofs = beam.getDofs();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    MassMatrix.add(dofs[i], dofs[j], beam.getEleMassMatrix_globalized().get(i, j));
                }
            }
        }
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumDOF(); k++) {
                MassMatrix.set(j,k,0.0);
                MassMatrix.set(k,j,0.0);
            }
            MassMatrix.set(j,j,1.0);
        }
    }


    public void calcDampingMatrix() {
        // CommonOps.scale(material.getC()/material.getM(),MassMatrix,DampingMatrix);
        //CommonOps.scale(10,MassMatrix,DampingMatrix);
        double a0 = 4.788640506;
        double a1 =0.0001746899608;
        CommonOps.add(a0,MassMatrix,a1,StiffnessMatrix,DampingMatrix);
        for (int i = 0; i <structure.getConDOF().size(); i++) {
            int j = structure.getConDOF().get(i);
            for (int k = 0; k < getNumDOF(); k++) {
                DampingMatrix.set(j,k,0.0);
                DampingMatrix.set(k,j,0.0);
            }
            DampingMatrix.set(j,j,1.0);
        }
    }

    public DenseMatrix64F getDisplacementVector() {
        return DisplacementVector;
    }

    public int getNumDOF() {
        return numDOF;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    /**
     * Update {@link Structure} that is displayed using values computed by {@link de.ferienakademie.smartquake.kernel2.TimeIntegration}
     * @param displacementVector a (3 * number of nodes) x 1 matrix. Three consequent values contain displacements in x, y, z direction.
     */
    public void updateStructure(DenseMatrix64F displacementVector) {
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            node.setCurrX(node.getInitX() + displacementVector.get(3*i, 0));
            node.setCurrY(node.getInitY() + displacementVector.get(3*i+1, 0));
        }
    }

    public void updateStructureKernel1(DenseMatrix64F displacementVector) {
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            node.setCurrX(node.getInitX() + displacementVector.get(3*i, 0));
            node.setCurrY(node.getInitY() + displacementVector.get(3*i+1, 0));
            node.setSingleRotation(0,displacementVector.get(3*i+2,0)); //TODO change with introducting of hinges
        }
    }

    public DenseMatrix64F getLoadVector() {
        return LoadVector;
    }

    public void setLoadVector(DenseMatrix64F loadVector) {
        LoadVector = loadVector;
    }

    public void calcInfluenceVector(){
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            List<Integer> DOF = node.getDOF();
            int DOFx = DOF.get(0);
            int DOFy = DOF.get(1);
            influenceVectorx = new DenseMatrix64F(getNumDOF(), 1);
            influenceVectory = new DenseMatrix64F(getNumDOF(), 1);
            influenceVectorx.zero();
            influenceVectory.zero();
            influenceVectorx.add(DOFx,0,-1); //add influence vector in x-dir
            influenceVectory.add(DOFy,0,-1); //add influence vector in y-dir
        }
    }


    /**
     * Update the vector with forces using the acceleration values received from the {@link AccelerationProvider}
     * @param acceleration - view {@link AccelerationProvider} for details
     */
    public void updateLoadVector(double[] acceleration) {
        LoadVector.zero();
        CommonOps.scale(acceleration[0], influenceVectorx);
        CommonOps.scale(acceleration[1], influenceVectory);
        CommonOps.addEquals(influenceVectorx, influenceVectory);
        CommonOps.mult(MassMatrix, influenceVectorx, LoadVector);
    }
}
