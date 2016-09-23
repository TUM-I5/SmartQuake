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
    // array of degress of freedom in format [x1, y1, rotation1, x2, y2, rotation2]
    private int[] Dofs;

    private DenseMatrix64F eleStiffnessMatrix;
    private DenseMatrix64F elelumpedMassMatrix;
    private DenseMatrix64F eleconsistentMassMatrix;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, Material material,boolean lumped) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.Dofs = new int[]{
                startNode.getDOF().get(0), startNode.getDOF().get(1), startNode.getDOF().get(2),
                endNode.getDOF().get(0), endNode.getDOF().get(1), endNode.getDOF().get(2)
        };
        this.material = material;
        double x1 = startNode.getInitX(), y1 = startNode.getInitY();
        double x2 = endNode.getInitX(), y2 = endNode.getInitY();
        this.l=Math.sqrt((x1-x2)*(x1-x2))+(y1-y2)*(y1-y2);
        this.thickness = 15;
        computeStiffnessMatrix();
        if (lumped){
            computelumpedMassMatrix();
        }else {
            computeconsistentMassMatrix();
        }

    }

    void computeStiffnessMatrix() {
        double EA=this.material.getEA();
        double EI=this.material.getEI();


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


    }

    void computelumpedMassMatrix() {
        elelumpedMassMatrix = new DenseMatrix64F(6, 6);
        elelumpedMassMatrix.zero();

        double rho = this.material.getRho();
        double alpha = this.material.getAlpha();
        double m = material.getM();

        elelumpedMassMatrix.set(0, 0, 0.5 * m * l);
        elelumpedMassMatrix.set(1, 1, 0.5 * m * l);
        elelumpedMassMatrix.set(2, 2, alpha * m * l * l * l);
        elelumpedMassMatrix.set(3, 3, 0.5 * m * l);
        elelumpedMassMatrix.set(4, 4, 0.5 * m * l);
        elelumpedMassMatrix.set(5, 5, alpha * m * l * l * l);
    }

    void computeconsistentMassMatrix() {
        eleconsistentMassMatrix = new DenseMatrix64F(6, 6);
        eleconsistentMassMatrix.zero();

        double m= material.getM();

         //consistent element mass matrix
        /*
        eleMassMatrix = new DenseMatrix64F(6,6);
        eleMassMatrix.zero();
        //row 1
        eleMassMatrix.set(0,0,140*m*l*l/420);
        eleMassMatrix.set(0,3,70*m*l*l/420);
        //row 2
        eleMassMatrix.set(1,1,156*m*l*l/420);
        eleMassMatrix.set(1,2,-22*l*m*l*l/420);
        eleMassMatrix.set(1,4,54*m*l*l/420);
        eleMassMatrix.set(1,5,13*l*m*l*l/420);
        //row 3
        eleMassMatrix.set(2,1,-22*l*m*l*l/420);
        eleMassMatrix.set(2,2,4*l*l*m*l*l/420);
        eleMassMatrix.set(2,4,-13*l*m*l*l/420);
        eleMassMatrix.set(2,5,-3*l*l*m*l*l/420);
        //row 4
        eleMassMatrix.set(3,0,70*m*l*l/420);
        eleMassMatrix.set(3,3,140*m*l*l/420);
        //row 5
        eleMassMatrix.set(4,1,54*m*l*l/420);
        eleMassMatrix.set(4,2,-13*l*m*l*l/420);
        eleMassMatrix.set(4,4,156*m*l*l/420);
        eleMassMatrix.set(4,5,22*l*m*l*l/420);
        //row 6
        eleMassMatrix.set(5,1,13*l*m*l*l/420);
        eleMassMatrix.set(5,2,-3*l*l*m*l*l/420);
        eleMassMatrix.set(5,4,22*l*m*l*l/420);
        eleMassMatrix.set(5,5,4*l*l*m*l*l/420);
        */


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

    public DenseMatrix64F getEleStiffnessMatrix() {
        return eleStiffnessMatrix;
    }

    public DenseMatrix64F getEleMassMatrix() {
        return elelumpedMassMatrix;
    }
}
