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
    // array of degress of freedom in format [x1, y1, rotation1, x2, y2, rotation2]
    private int[] Dofs;

    private DenseMatrix64F eleStiffnessMatrix;
    private DenseMatrix64F eleMassMatrix;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, Material material) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.Dofs = new int[]{
                startNode.getDOF().get(0), startNode.getDOF().get(1), startNode.getDOF().get(2),
                endNode.getDOF().get(0), endNode.getDOF().get(1), endNode.getDOF().get(2)
        };
        this.material = material;
        this.thickness = 15;
        computeMatrices();
    }

    void computeMatrices() {
        double x1 = startNode.getInitX(), y1 = startNode.getInitY();
        double x2 = endNode.getInitX(), y2 = endNode.getInitY();
        double l=Math.sqrt((x1-x2)*(x1-x2))+(y1-y2)*(y1-y2);
        double A=this.material.getA();
        double EA=this.material.getEA();
        double EI=this.material.getEI();
        double rho = this.material.getRho();
        double alpha=this.material.getAlpha();

        eleStiffnessMatrix = new DenseMatrix64F(6, 6);
        eleStiffnessMatrix.zero();

        eleStiffnessMatrix.set(0,0,EA/l);
        eleStiffnessMatrix.set(0,3,-EA/l);

        eleStiffnessMatrix.set(1,1,12*EI/(l*l*l));
        eleStiffnessMatrix.set(1,2,-6*EI/(l*l));
        eleStiffnessMatrix.set(1,4,-12*EI/(l*l*l));
        eleStiffnessMatrix.set(1,5,-6*EI/(l*l));

        eleStiffnessMatrix.set(2,1,-6*EI/(l*l));
        eleStiffnessMatrix.set(2,2,4*EI/l);
        eleStiffnessMatrix.set(2,4,6*EI/(l*l));
        eleStiffnessMatrix.set(2,5,2*EI/l);

        eleStiffnessMatrix.set(3,0,-EA/l);
        eleStiffnessMatrix.set(3,3,EA/l);

        eleStiffnessMatrix.set(4,1,-12*EI/(l*l*l));
        eleStiffnessMatrix.set(4,2,6*EI/(l*l));
        eleStiffnessMatrix.set(4,4,12*EI/(l*l*l));
        eleStiffnessMatrix.set(4,5,6*EI/(l*l));

        eleStiffnessMatrix.set(5,1,-6*EI/(l*l));
        eleStiffnessMatrix.set(5,2,2*EI/l);
        eleStiffnessMatrix.set(5,4,6*EI/(l*l));
        eleStiffnessMatrix.set(5,5,6*EI/(l*l));

        eleMassMatrix = new DenseMatrix64F(6, 6);
        eleMassMatrix.zero();

        eleMassMatrix.set(0,0,0.5*rho*A*l);
        eleMassMatrix.set(1,1,0.5*rho*A*l);
        eleMassMatrix.set(2,2,alpha*rho*A*l*l*l);
        eleMassMatrix.set(3,3,0.5*rho*A*l);
        eleMassMatrix.set(4,4,0.5*rho*A*l);
        eleMassMatrix.set(5,5,alpha*rho*A*l*l*l);
    }

    public Beam(Node startNode, Node endNode) {
        this(startNode, endNode, 10);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }

    public int[] getDofs() {
        return Dofs;
    }

    public void setDofs(int[] dofs) {
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

    @Override
    public boolean equals(Object b) {
        if (b instanceof Beam) {
            Beam temp = (Beam) b;
            return (temp.startNode.equals(startNode) && temp.endNode.equals(endNode))
                    || (temp.endNode.equals(startNode) && temp.startNode.equals(endNode));
        } else {
            return false;
        }
    }

    public DenseMatrix64F getEleStiffnessMatrix() {
        return eleStiffnessMatrix;
    }

    public DenseMatrix64F getEleMassMatrix() {
        return eleMassMatrix;
    }
}
