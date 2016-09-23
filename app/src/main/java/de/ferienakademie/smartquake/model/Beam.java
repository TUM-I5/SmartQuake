package de.ferienakademie.smartquake.model;

import org.ejml.data.DenseMatrix64F;

import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */

public class Beam {

    private Node startNode;
    private Node endNode;
    private Material material;
    private float thickness;
    private double l;
    private double theta;

    /*
    private double E;
    private double A;
    private double I;
    private double rho;
    private double alpha;
    private double EA;
    private double EI;*/

    private double x1;
    private double x2;
    private double y1;
    private double y2;
    private List<Integer> Dofs;



    private DenseMatrix64F eleStiffnessMatrix;
    private DenseMatrix64F eleMassMatrix;
    private DenseMatrix64F rotationMatrix;

    private DenseMatrix64F eleStiffnessMatrix_globalized;
    private DenseMatrix64F eleMassMatrix_globalized;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, Material material) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.Dofs.addAll(startNode.getDOF());
        this.Dofs.addAll(endNode.getDOF());
        this.material = material;
        this.thickness = 15;
        l=Math.sqrt((x1-x2)*(x1-x2))+(y1-y2)*(y1-y2);
        theta=Math.atan((y2-y1)/(x2-x1));

        eleMassMatrix = new DenseMatrix64F(6, 6);
        eleMassMatrix.zero();

        eleMassMatrix.set(1,1,0.5*this.material.rho*this.material.A*l);
        eleMassMatrix.set(2,2,0.5*this.material.rho*this.material.A*l);
        eleMassMatrix.set(3,3,this.material.alpha*this.material.rho*this.material.A*l*l*l);
        eleMassMatrix.set(4,4,0.5*this.material.rho*this.material.A*l);
        eleMassMatrix.set(5,5,0.5*this.material.rho*this.material.A*l);
        eleMassMatrix.set(6,6,this.material.alpha*this.material.rho*this.material.A*l*l*l);

        eleStiffnessMatrix = new DenseMatrix64F(6, 6);
        eleStiffnessMatrix.zero();

        eleStiffnessMatrix.set(1,1,this.material.EA/l);
        eleStiffnessMatrix.set(1,4,-this.material.EA/l);

        eleStiffnessMatrix.set(2,2,12*this.material.EI/(l*l*l));
        eleStiffnessMatrix.set(2,3,-6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(2,5,-12*this.material.EI/(l*l*l));
        eleStiffnessMatrix.set(2,6,-6*this.material.EI/(l*l));

        eleStiffnessMatrix.set(3,2,-6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(3,3,4*this.material.EI/l);
        eleStiffnessMatrix.set(3,5,6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(3,6,2*this.material.EI/l);

        eleStiffnessMatrix.set(4,1,-this.material.EA/l);
        eleStiffnessMatrix.set(4,4,this.material.EA/l);

        eleStiffnessMatrix.set(5,2,-12*this.material.EI/(l*l*l));
        eleStiffnessMatrix.set(5,3,6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(5,5,12*this.material.EI/(l*l*l));
        eleStiffnessMatrix.set(5,6,6*this.material.EI/(l*l));

        eleStiffnessMatrix.set(6,2,-6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(6,3,2*this.material.EI/l);
        eleStiffnessMatrix.set(6,5,6*this.material.EI/(l*l));
        eleStiffnessMatrix.set(6,6,6*this.material.EI/(l*l));

        eleStiffnessMatrix_globalized = GlobalizeElementMatrix(eleStiffnessMatrix, theta);
        eleMassMatrix_globalized = GlobalizeElementMatrix(eleMassMatrix, theta);

       /* rotationMatrix.set(1,1,Math.cos(theta));
        rotationMatrix.set(1,2,Math.sin(theta));

        rotationMatrix.set(2,1,-Math.sin(theta));
        rotationMatrix.set(2,2,Math.cos(theta));

        rotationMatrix.set(3,3,1);

        rotationMatrix.set(4,4,Math.cos(theta));
        rotationMatrix.set(4,5,Math.sin(theta));

        rotationMatrix.set(5,4,-Math.sin(theta));
        rotationMatrix.set(5,5,Math.cos(theta));

        rotationMatrix.set(6,6,1);*/

    }

    public Beam(Node startNode, Node endNode) {
        this(startNode, endNode, 10);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }


    public List<Integer> getDofs() {
        return Dofs;
    }

    public void setDofs(List<Integer> dofs) {
        Dofs = dofs;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public DenseMatrix64F getEleStiffnessMatrix() {
        return eleStiffnessMatrix;
    }

    public DenseMatrix64F getEleMassMatrix() {
        return eleMassMatrix;
    }

    public DenseMatrix64F GlobalizeElementMatrix(DenseMatrix64F elementMatrix, double theta) {

        double c = Math.cos(theta);
        double s = Math.sin(theta);

        DenseMatrix64F elementMatrix_globalized;
        elementMatrix_globalized = new DenseMatrix64F(6, 6);
        elementMatrix_globalized.zero();

        elementMatrix_globalized.set(1, 1, elementMatrix.get(1, 1) * c * c + elementMatrix.get(2, 2) * s * s);
        elementMatrix_globalized.set(1, 2, elementMatrix.get(1, 1) * c * s - elementMatrix.get(2, 2) * c * s);
        elementMatrix_globalized.set(1, 3, elementMatrix.get(2, 3) * -s);
        elementMatrix_globalized.set(1, 4, elementMatrix.get(1, 4) * c * c + elementMatrix.get(2, 5) * s * s);
        elementMatrix_globalized.set(1, 5, elementMatrix.get(1, 4) * c * s - elementMatrix.get(2, 5) * c * s);
        elementMatrix_globalized.set(1, 6, elementMatrix.get(2, 6) * -s);
        elementMatrix_globalized.set(2, 1, elementMatrix.get(1, 1) * c * s - elementMatrix.get(2, 2) * c * s);
        elementMatrix_globalized.set(2, 2, elementMatrix.get(2, 2) * c * c + elementMatrix.get(1, 1) * s * s);
        elementMatrix_globalized.set(2, 3, elementMatrix.get(2, 3) * c);
        elementMatrix_globalized.set(2, 4, elementMatrix.get(1, 4) * c * s - elementMatrix.get(2, 5) * c * s);
        elementMatrix_globalized.set(2, 5, elementMatrix.get(2, 5) * c * c + elementMatrix.get(1, 4) * s * s);
        elementMatrix_globalized.set(2, 6, elementMatrix.get(2, 6) * c);
        elementMatrix_globalized.set(3, 1, elementMatrix.get(3, 2) * -s);
        elementMatrix_globalized.set(3, 2, elementMatrix.get(3, 2) * c);
        elementMatrix_globalized.set(3, 3, elementMatrix.get(3, 3));
        elementMatrix_globalized.set(3, 4, elementMatrix.get(3, 5) * -s);
        elementMatrix_globalized.set(3, 5, elementMatrix.get(3, 5) * c);
        elementMatrix_globalized.set(3, 6, elementMatrix.get(3, 6));
        elementMatrix_globalized.set(4, 1, elementMatrix.get(4, 1) * c * c + elementMatrix.get(5, 2) * s * s);
        elementMatrix_globalized.set(4, 2, elementMatrix.get(4, 1) * c * s - elementMatrix.get(5, 2) * c * s);
        elementMatrix_globalized.set(4, 3, elementMatrix.get(5, 3) * -s);
        elementMatrix_globalized.set(4, 4, elementMatrix.get(4, 4) * c * c + elementMatrix.get(5, 5) * s * s);
        elementMatrix_globalized.set(4, 5, elementMatrix.get(4, 4) * c * s - elementMatrix.get(5, 5) * c * s);
        elementMatrix_globalized.set(4, 6, elementMatrix.get(5, 6) * -s);
        elementMatrix_globalized.set(5, 1, elementMatrix.get(4, 1) * c * s - elementMatrix.get(5, 2) * c * s);
        elementMatrix_globalized.set(5, 2, elementMatrix.get(5, 2) * c * c + elementMatrix.get(4, 1) * s * s);
        elementMatrix_globalized.set(5, 3, elementMatrix.get(5, 3) * c);
        elementMatrix_globalized.set(5, 4, elementMatrix.get(4, 4) * c * s - elementMatrix.get(5, 5) * c * s);
        elementMatrix_globalized.set(5, 5, elementMatrix.get(5, 5) * c * c + elementMatrix.get(4, 4) * s * s);
        elementMatrix_globalized.set(5, 6, elementMatrix.get(5, 6) * c);
        elementMatrix_globalized.set(6, 1, elementMatrix.get(6, 2) * -s);
        elementMatrix_globalized.set(6, 2, elementMatrix.get(6, 2) * c);
        elementMatrix_globalized.set(6, 3, elementMatrix.get(6, 3));
        elementMatrix_globalized.set(6, 4, elementMatrix.get(6, 5) * -s);
        elementMatrix_globalized.set(6, 5, elementMatrix.get(6, 5) * c);
        elementMatrix_globalized.set(6, 6, elementMatrix.get(6, 6));

        return elementMatrix_globalized;
    }

}
