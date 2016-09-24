package de.ferienakademie.smartquake.model;

import org.ejml.data.DenseMatrix64F;
import org.junit.runner.Describable;

import java.util.ArrayList;
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
    private double s;
    private double c;
    private double theta;

    private List<Double> localdisplacements;

    // array of degrees of freedom in format [x1, y1, rotation1, x2, y2, rotation2]
    private int[] Dofs;

    private DenseMatrix64F eleStiffnessMatrix;
    private DenseMatrix64F eleMassMatrix;

    private DenseMatrix64F eleStiffnessMatrix_globalized;
    private DenseMatrix64F eleMassMatrix_globalized;

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
        this.thickness = 0.1f;
        double x1 = startNode.getInitX(), y1 = startNode.getInitY();
        double x2 = endNode.getInitX(), y2 = endNode.getInitY();
        l = Math.sqrt((x1 - x2) * (x1 - x2)) + (y1 - y2) * (y1 - y2);

        theta = -Math.atan((y2 - y1) / (x2 - x1));
        c = Math.cos(theta); //rotation of displacement
        s = Math.sin(theta);
        computeStiffnessMatrix();
        eleStiffnessMatrix_globalized = GlobalizeElementMatrix(eleStiffnessMatrix);

        if (lumped){
            computelumpedMassMatrix();
            eleMassMatrix_globalized = eleMassMatrix;
        }else {
            computeconsistentMassMatrix();
            eleMassMatrix_globalized = GlobalizeElementMatrix(eleMassMatrix);
        }

    }

    void computeStiffnessMatrix() {
        double EA = this.material.getEA();
        double EI = this.material.getEI();

        eleStiffnessMatrix = new DenseMatrix64F(6, 6);
        eleStiffnessMatrix.zero();

        eleStiffnessMatrix.set(0, 0, EA / l);
        eleStiffnessMatrix.set(0, 3, -EA / l);

        eleStiffnessMatrix.set(1, 1, 12 * EI / (l * l * l));
        eleStiffnessMatrix.set(1, 2, -6 * EI / (l * l));
        eleStiffnessMatrix.set(1, 4, -12 * EI / (l * l * l));
        eleStiffnessMatrix.set(1, 5, -6 * EI / (l * l));

        eleStiffnessMatrix.set(2, 1, -6 * EI / (l * l));
        eleStiffnessMatrix.set(2, 2, 4 * EI / l);
        eleStiffnessMatrix.set(2, 4, 6 * EI / (l * l));
        eleStiffnessMatrix.set(2, 5, 2 * EI / l);

        eleStiffnessMatrix.set(3, 0, -EA / l);
        eleStiffnessMatrix.set(3, 3, EA / l);

        eleStiffnessMatrix.set(4, 1, -12 * EI / (l * l * l));
        eleStiffnessMatrix.set(4, 2, 6 * EI / (l * l));
        eleStiffnessMatrix.set(4, 4, 12 * EI / (l * l * l));
        eleStiffnessMatrix.set(4, 5, 6 * EI / (l * l));

        eleStiffnessMatrix.set(5, 1, -6 * EI / (l * l));
        eleStiffnessMatrix.set(5, 2, 2 * EI / l);
        eleStiffnessMatrix.set(5, 4, 6 * EI / (l * l));
        eleStiffnessMatrix.set(5, 5, 6 * EI / (l * l));
    }

    void computelumpedMassMatrix() {

        eleMassMatrix = new DenseMatrix64F(6, 6);
        eleMassMatrix.zero();

        double alpha = this.material.getAlpha();
        double m = material.getM();

        eleMassMatrix.set(0, 0, 0.5 * m * l);
        eleMassMatrix.set(1, 1, 0.5 * m * l);
        eleMassMatrix.set(2, 2, alpha * m * l * l * l);
        eleMassMatrix.set(3, 3, 0.5 * m * l);
        eleMassMatrix.set(4, 4, 0.5 * m * l);
        eleMassMatrix.set(5, 5, alpha * m * l * l * l);

    }
    void computeconsistentMassMatrix() {

        eleMassMatrix = new DenseMatrix64F(6, 6);
        eleMassMatrix.zero();

        double m= material.getM();

        //consistent element mass matrix

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
    }

    public DenseMatrix64F GlobalizeElementMatrix(DenseMatrix64F elementMatrix) {



        DenseMatrix64F elementMatrix_globalized;
        elementMatrix_globalized = new DenseMatrix64F(6, 6);
        elementMatrix_globalized.zero();

        elementMatrix_globalized.set(0, 0, elementMatrix.get(0, 0) * c * c + elementMatrix.get(1, 1) * s * s);
        elementMatrix_globalized.set(0, 1, elementMatrix.get(0, 0) * c * s - elementMatrix.get(1, 1) * c * s);
        elementMatrix_globalized.set(0, 2, elementMatrix.get(1, 2) * -s);
        elementMatrix_globalized.set(0, 3, elementMatrix.get(0, 3) * c * c + elementMatrix.get(1, 4) * s * s);
        elementMatrix_globalized.set(0, 4, elementMatrix.get(0, 3) * c * s - elementMatrix.get(1, 4) * c * s);
        elementMatrix_globalized.set(0, 5, elementMatrix.get(1, 5) * -s);

        elementMatrix_globalized.set(1, 0, (elementMatrix.get(0, 0)- elementMatrix.get(1, 1) )* c * s);
        elementMatrix_globalized.set(1, 1, elementMatrix.get(1, 1) * c * c + elementMatrix.get(0, 0) * s * s);
        elementMatrix_globalized.set(1, 2, elementMatrix.get(1, 2) * c);
        elementMatrix_globalized.set(1, 3, (elementMatrix.get(0, 3)- elementMatrix.get(1, 4)) * c * s);
        elementMatrix_globalized.set(1, 4, elementMatrix.get(1, 4) * c * c + elementMatrix.get(0, 3) * s * s);
        elementMatrix_globalized.set(1, 5, elementMatrix.get(1, 5) * c);

        elementMatrix_globalized.set(2, 0, elementMatrix.get(2, 1) * -s);
        elementMatrix_globalized.set(2, 1, elementMatrix.get(2, 1) * c);
        elementMatrix_globalized.set(2, 2, elementMatrix.get(2, 2));
        elementMatrix_globalized.set(2, 3, elementMatrix.get(2, 4) * -s);
        elementMatrix_globalized.set(2, 4, elementMatrix.get(2, 4) * c);
        elementMatrix_globalized.set(2, 5, elementMatrix.get(2, 5));

        elementMatrix_globalized.set(3, 0, elementMatrix.get(3, 0) * c * c + elementMatrix.get(4, 1) * s * s);
        elementMatrix_globalized.set(3, 1, (elementMatrix.get(3, 0)- elementMatrix.get(4, 1) ) * c * s);
        elementMatrix_globalized.set(3, 2, elementMatrix.get(4, 2) * -s);
        elementMatrix_globalized.set(3, 3, elementMatrix.get(3, 3) * c * c + elementMatrix.get(4, 4) * s * s);
        elementMatrix_globalized.set(3, 4, (elementMatrix.get(3, 3)- elementMatrix.get(4, 4)) * c * s);
        elementMatrix_globalized.set(3, 5, elementMatrix.get(4, 5) * -s);

        elementMatrix_globalized.set(4, 0, (elementMatrix.get(3, 0)- elementMatrix.get(4, 1)) * c * s);
        elementMatrix_globalized.set(4, 1, elementMatrix.get(4, 1) * c * c + elementMatrix.get(3, 0) * s * s);
        elementMatrix_globalized.set(4, 2, elementMatrix.get(4, 2) * c);
        elementMatrix_globalized.set(4, 3, (elementMatrix.get(3, 3)- elementMatrix.get(4, 4)) * c * s);
        elementMatrix_globalized.set(4, 4, elementMatrix.get(4, 4) * c * c + elementMatrix.get(3, 3) * s * s);
        elementMatrix_globalized.set(4, 5, elementMatrix.get(4, 5) * c);

        elementMatrix_globalized.set(5, 0, elementMatrix.get(5, 1) * -s);
        elementMatrix_globalized.set(5, 1, elementMatrix.get(5, 1) * c);
        elementMatrix_globalized.set(5, 2, elementMatrix.get(5, 2));
        elementMatrix_globalized.set(5, 3, elementMatrix.get(4, 5) * -s);
        elementMatrix_globalized.set(5, 4, elementMatrix.get(4, 5) * c);
        elementMatrix_globalized.set(5, 5, elementMatrix.get(5, 5));


        return elementMatrix_globalized;
    }


    public Beam(Node startNode, Node endNode) {
        this(startNode, endNode, 10f);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }

    public void dofsGlobalToLocal(){
        double[] u= new double[6];
        u[0]= startNode.getCurrX(); //x-displacement of startnode
        u[1]=startNode.getCurrY(); //y-displacement of startnode
        u[2]=endNode.getCurrX();   //x-displacement of endnode
        u[3]=endNode.getCurrY();


        //TODO: mabye refactor and remove u and always use localdisplacement array

        localdisplacements.add(u[0]*Math.cos(theta)-u[1]*Math.sin(theta));
        localdisplacements.add(u[0]*Math.sin(theta)+u[1]*Math.cos(theta));
        localdisplacements.add(u[2]*Math.cos(theta)-u[3]*Math.sin(theta));
        localdisplacements.add(u[2]*Math.sin(theta)+u[3]*Math.cos(theta));

        //TODO rotations aren't rotated therefore saving them them from global to local and vice versa isn't necessary
        localdisplacements.add((startNode.getCurrROT().get(0)));
        localdisplacements.add((endNode.getCurrROT().get(0)));

    }

    public void dofsLocalToGlobal(){
        double[] u= new double[6];
        u[0]= startNode.getCurrX(); //x-displacement of startnode
        u[1]=startNode.getCurrY(); //y-displacement of startnode
        u[2]=endNode.getCurrX();   //x-displacement of endnode
        u[3]=endNode.getCurrY();

        //TODO: mabye refactor and remove u and always use localdisplacement array

        localdisplacements.add(u[0]*Math.cos(theta)+u[1]*Math.sin(theta));
        localdisplacements.add(-u[0]*Math.sin(theta)+u[1]*Math.cos(theta));
        localdisplacements.add(u[2]*Math.cos(theta)+u[3]*Math.sin(theta));
        localdisplacements.add(-u[2]*Math.sin(theta)+u[3]*Math.cos(theta));
        localdisplacements.add((startNode.getCurrROT().get(0)));
        localdisplacements.add((endNode.getCurrROT().get(0)));

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

    public DenseMatrix64F getEleStiffnessMatrix_globalized(){
        return eleStiffnessMatrix_globalized;
    }

    public DenseMatrix64F getEleMassMatrix_globalized(){
        return eleMassMatrix_globalized;
    }
}
