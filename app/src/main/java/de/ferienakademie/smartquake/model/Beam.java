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
    private double E;
    private double A;
    private double I;
    private double rho;
    private double alpha;
    private double x1;
    private double x2;
    private double y1;



    private double y2;
    private List<Integer> Dofs;
    private double EA;
    private double EI;

    private DenseMatrix64F eleStiffnessMatrix;
    private DenseMatrix64F eleMassMatrix;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, float thickness, Material material) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.Dofs.addAll(startNode.getDOF());
        this.Dofs.addAll(endNode.getDOF());
        this.thickness = thickness;
        this.material = material;
        l=Math.sqrt((x1-x2)*(x1-x2))+(y1-y2)*(y1-y2);
        A=this.material.getA();
        EA=this.material.getEA();
        EI=this.material.getEI();
        rho = this.material.getRho();
        alpha=this.material.getAlpha();

        eleStiffnessMatrix = new DenseMatrix64F(6, 6);
        eleStiffnessMatrix.zero();

        eleStiffnessMatrix.set(1,1,EA/l);
        eleStiffnessMatrix.set(1,4,-EA/l);

        eleStiffnessMatrix.set(2,2,12*EI/(l*l*l));
        eleStiffnessMatrix.set(2,3,-6*EI/(l*l));
        eleStiffnessMatrix.set(2,5,-12*EI/(l*l*l));
        eleStiffnessMatrix.set(2,6,-6*EI/(l*l));

        eleStiffnessMatrix.set(3,2,-6*EI/(l*l));
        eleStiffnessMatrix.set(3,3,4*EI/l);
        eleStiffnessMatrix.set(3,5,6*EI/(l*l));
        eleStiffnessMatrix.set(3,6,2*EI/l);

        eleStiffnessMatrix.set(4,1,-EA/l);
        eleStiffnessMatrix.set(4,4,EA/l);

        eleStiffnessMatrix.set(5,2,-12*EI/(l*l*l));
        eleStiffnessMatrix.set(5,3,6*EI/(l*l));
        eleStiffnessMatrix.set(5,5,12*EI/(l*l*l));
        eleStiffnessMatrix.set(5,6,6*EI/(l*l));

        eleStiffnessMatrix.set(6,2,-6*EI/(l*l));
        eleStiffnessMatrix.set(6,3,2*EI/l);
        eleStiffnessMatrix.set(6,5,6*EI/(l*l));
        eleStiffnessMatrix.set(6,6,6*EI/(l*l));


        eleMassMatrix = new DenseMatrix64F(6, 6);
        eleMassMatrix.zero();

        eleMassMatrix.set(1,1,0.5*rho*A*l);
        eleMassMatrix.set(2,2,0.5*rho*A*l);
        eleMassMatrix.set(3,3,alpha*rho*A*l*l*l);
        eleMassMatrix.set(4,4,0.5*rho*A*l);
        eleMassMatrix.set(5,5,0.5*rho*A*l);
        eleMassMatrix.set(6,6,alpha*rho*A*l*l*l);

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
}
