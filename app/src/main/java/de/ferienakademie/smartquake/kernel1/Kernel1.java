package de.ferienakademie.smartquake.kernel1;

import org.ejml.data.DenseMatrix64F;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;
import de.ferienakademie.smartquake.view.CanvasView;
import de.ferienakademie.smartquake.view.DrawHelper;

/**
 * Created by alex on 22.09.16.
 */
public class Kernel1 {

    private DenseMatrix64F     StiffnessMatrix;
    private DenseMatrix64F     DampingMatrix;
    private DenseMatrix64F     MassMatrix;
    private DenseMatrix64F     LoadVector;
    private DenseMatrix64F     DisplacementVector;  //project manager advice

    //TODO: ask why we have three degrees of freedom while modelling in 2D
    private int numDOF;
    private int[] conDOF;

    Structure structure;
    CanvasView view;
    AccelerationProvider accelerationProvider;

    public Kernel1(Structure structure, CanvasView view, AccelerationProvider accelerationProvider) {
        this.structure = structure;
        this.view = view;
        this.accelerationProvider = accelerationProvider;

        this.numDOF = 3 * structure.getNodes().size();
        this.conDOF = structure.getConDOF();

        //initialize displacement with zeros
        DisplacementVector = new DenseMatrix64F(numDOF, 1);
        DisplacementVector.zero();

        initMatrices();

    }


    public void initMatrices(){
        StiffnessMatrix = new DenseMatrix64F(numDOF, numDOF);
        MassMatrix = new DenseMatrix64F(numDOF, numDOF);
        DampingMatrix = new DenseMatrix64F(numDOF, numDOF);


        StiffnessMatrix.zero();
        MassMatrix.zero();
        DampingMatrix.zero();

        calcDampingMatrix();
        calcMassMatrix();
        calcStiffnessMatrix();
    }

    public void calcStiffnessMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            StiffnessMatrix.add(i,i,1);
        }
    }

    public void calcMassMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            MassMatrix.add(i,i,1);
        }
    }

    public void calcDampingMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            DampingMatrix.add(i,i,1);
        }
    }

    public DenseMatrix64F getDisplacementVector(){
        return DisplacementVector;
    }

    public int getNumDOF(){
        return numDOF;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public CanvasView getView() {
        return view;
    }

    public void setView(CanvasView view) {
        this.view = view;
    }

    public AccelerationProvider getAccelerationProvider() {
        return accelerationProvider;
    }

    public void setAccelerationProvider(AccelerationProvider accelerationProvider) {
        this.accelerationProvider = accelerationProvider;
    }

    public void updateStructure(DenseMatrix64F displacementVector) {
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            node.setX(node.getX() + displacementVector.get(3*i, 0));
            node.setY(node.getY() + displacementVector.get(3*i+1, 0));
        }
        DrawHelper.drawStructure(structure, view);

    }
}
