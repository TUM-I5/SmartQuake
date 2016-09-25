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
    private float thickness = 0.1f;
    private double length;
    private double sin_theta;
    private double cos_theta;
    private double theta;

    private List<Double> localdisplacements;

    /**
     *array of degrees of freedom in format [x1, y1, rotation1, x2, y2, rotation2]
      */
    private int[] dofs;

    private DenseMatrix64F elementStiffnessMatrix;
    private DenseMatrix64F elementMassMatrix;

    private DenseMatrix64F elementStiffnessMatrix_globalized;
    private DenseMatrix64F elementMassMatrix_globalized;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, Material material,boolean lumped) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.dofs = new int[]{
                startNode.getDOF().get(0), startNode.getDOF().get(1), startNode.getDOF().get(2),
                endNode.getDOF().get(0), endNode.getDOF().get(1), endNode.getDOF().get(2)
        };
        this.material = material;
        this.thickness = 0.1f;
        double x1 = startNode.getInitialX(), y1 = startNode.getInitialY();
        double x2 = endNode.getInitialX(), y2 = endNode.getInitialY();
        length = Math.sqrt((x1 - x2) * (x1 - x2)) + (y1 - y2) * (y1 - y2);

        theta = -Math.atan((y2 - y1) / (x2 - x1));
        cos_theta = Math.cos(theta); //rotation of displacement
        sin_theta = Math.sin(theta);
        computeStiffnessMatrix();
        elementStiffnessMatrix_globalized = GlobalizeElementMatrix(elementStiffnessMatrix);

        if (lumped){
            computelumpedMassMatrix();
            elementMassMatrix_globalized = elementMassMatrix;
        }else {
            computeconsistentMassMatrix();
            elementMassMatrix_globalized = GlobalizeElementMatrix(elementMassMatrix);
        }

    }

    void computeStiffnessMatrix() {
        double EA = this.material.getAxialStiffnessOfBar();  //Young's modulus * AreaOfCrossSection
        double EI = this.material.getBendingStiffnessOfBeam();  //Young's modulus * Moment of Inertia

        elementStiffnessMatrix = new DenseMatrix64F(6, 6);
        elementStiffnessMatrix.zero();

        elementStiffnessMatrix.set(0, 0, EA / length);
        elementStiffnessMatrix.set(0, 3, -EA / length);
        elementStiffnessMatrix.set(1, 1, 12 * EI / (length * length * length));
        elementStiffnessMatrix.set(1, 2, -6 * EI / (length * length));
        elementStiffnessMatrix.set(1, 4, -12 * EI / (length * length * length));
        elementStiffnessMatrix.set(1, 5, -6 * EI / (length * length));

        elementStiffnessMatrix.set(2, 1, -6 * EI / (length * length));
        elementStiffnessMatrix.set(2, 2, 4 * EI / length);
        elementStiffnessMatrix.set(2, 4, 6 * EI / (length * length));
        elementStiffnessMatrix.set(2, 5, 2 * EI / length);

        elementStiffnessMatrix.set(3, 0, -EA / length);
        elementStiffnessMatrix.set(3, 3, EA / length);

        elementStiffnessMatrix.set(4, 1, -12 * EI / (length * length * length));
        elementStiffnessMatrix.set(4, 2, 6 * EI / (length * length));
        elementStiffnessMatrix.set(4, 4, 12 * EI / (length * length * length));
        elementStiffnessMatrix.set(4, 5, 6 * EI / (length * length));

        elementStiffnessMatrix.set(5, 1, -6 * EI / (length * length));
        elementStiffnessMatrix.set(5, 2, 2 * EI / length);
        elementStiffnessMatrix.set(5, 4, 6 * EI / (length * length));
        elementStiffnessMatrix.set(5, 5, 4 * EI / (length * length));
    }

    void computelumpedMassMatrix() {

        elementMassMatrix = new DenseMatrix64F(6, 6);
        elementMassMatrix.zero();

        double alpha = this.material.getAlpha();
        double mass_per_length = material.getMassPerLength();

        elementMassMatrix.set(0, 0, 0.5 * mass_per_length * length);
        elementMassMatrix.set(1, 1, 0.5 * mass_per_length * length);
        elementMassMatrix.set(2, 2, alpha * mass_per_length * length * length * length);
        elementMassMatrix.set(3, 3, 0.5 * mass_per_length * length);
        elementMassMatrix.set(4, 4, 0.5 * mass_per_length * length);
        elementMassMatrix.set(5, 5, alpha * mass_per_length * length * length * length);

    }
    void computeconsistentMassMatrix() {

        elementMassMatrix = new DenseMatrix64F(6, 6);
        elementMassMatrix.zero();

        double massperlength= material.getMassPerLength();

        //consistent element mass matrix

        elementMassMatrix = new DenseMatrix64F(6,6);
        elementMassMatrix.zero();
        //row 1
        elementMassMatrix.set(0,0,140*massperlength* length /420);
        elementMassMatrix.set(0,3,70*massperlength* length /420);
        //row 2
        elementMassMatrix.set(1,1,156*massperlength* length /420);
        elementMassMatrix.set(1,2,-22* length *massperlength* length /420);
        elementMassMatrix.set(1,4,54*massperlength* length /420);
        elementMassMatrix.set(1,5,13* length *massperlength* length /420);
        //row 3
        elementMassMatrix.set(2,1,-22* length *massperlength* length /420);
        elementMassMatrix.set(2,2,4* length * length *massperlength* length /420);
        elementMassMatrix.set(2,4,-13* length *massperlength* length /420);
        elementMassMatrix.set(2,5,-3* length * length *massperlength* length /420);
        //row 4
        elementMassMatrix.set(3,0,70*massperlength* length /420);
        elementMassMatrix.set(3,3,140*massperlength* length /420);
        //row 5
        elementMassMatrix.set(4,1,54*massperlength* length /420);
        elementMassMatrix.set(4,2,-13* length *massperlength* length /420);
        elementMassMatrix.set(4,4,156*massperlength* length /420);
        elementMassMatrix.set(4,5,22* length *massperlength* length /420);
        //row 6
        elementMassMatrix.set(5,1,13* length *massperlength* length /420);
        elementMassMatrix.set(5,2,-3* length * length *massperlength* length /420);
        elementMassMatrix.set(5,4,22* length *massperlength* length /420);
        elementMassMatrix.set(5,5,4* length * length *massperlength* length /420);
    }

    public DenseMatrix64F GlobalizeElementMatrix(DenseMatrix64F elementMatrix) {



        DenseMatrix64F elementMatrix_globalized;
        elementMatrix_globalized = new DenseMatrix64F(6, 6);
        elementMatrix_globalized.zero();
        elementMatrix_globalized.set(0, 0, elementMatrix.get(0, 0) * cos_theta * cos_theta + elementMatrix.get(1, 1) * sin_theta * sin_theta);
        elementMatrix_globalized.set(0, 1, elementMatrix.get(0, 0) * cos_theta * sin_theta - elementMatrix.get(1, 1) * cos_theta * sin_theta);
        elementMatrix_globalized.set(0, 2, elementMatrix.get(1, 2) * -sin_theta);
        elementMatrix_globalized.set(0, 3, elementMatrix.get(0, 3) * cos_theta * cos_theta + elementMatrix.get(1, 4) * sin_theta * sin_theta);
        elementMatrix_globalized.set(0, 4, elementMatrix.get(0, 3) * cos_theta * sin_theta - elementMatrix.get(1, 4) * cos_theta * sin_theta);
        elementMatrix_globalized.set(0, 5, elementMatrix.get(1, 5) * -sin_theta);

        elementMatrix_globalized.set(1, 0, (elementMatrix.get(0, 0)- elementMatrix.get(1, 1) )* cos_theta * sin_theta);
        elementMatrix_globalized.set(1, 1, elementMatrix.get(1, 1) * cos_theta * cos_theta + elementMatrix.get(0, 0) * sin_theta * sin_theta);
        elementMatrix_globalized.set(1, 2, elementMatrix.get(1, 2) * cos_theta);
        elementMatrix_globalized.set(1, 3, (elementMatrix.get(0, 3)- elementMatrix.get(1, 4)) * cos_theta * sin_theta);
        elementMatrix_globalized.set(1, 4, elementMatrix.get(1, 4) * cos_theta * cos_theta + elementMatrix.get(0, 3) * sin_theta * sin_theta);
        elementMatrix_globalized.set(1, 5, elementMatrix.get(1, 5) * cos_theta);

        elementMatrix_globalized.set(2, 0, elementMatrix.get(2, 1) * -sin_theta);
        elementMatrix_globalized.set(2, 1, elementMatrix.get(2, 1) * cos_theta);
        elementMatrix_globalized.set(2, 2, elementMatrix.get(2, 2));
        elementMatrix_globalized.set(2, 3, elementMatrix.get(2, 4) * -sin_theta);
        elementMatrix_globalized.set(2, 4, elementMatrix.get(2, 4) * cos_theta);
        elementMatrix_globalized.set(2, 5, elementMatrix.get(2, 5));

        elementMatrix_globalized.set(3, 0, elementMatrix.get(3, 0) * cos_theta * cos_theta + elementMatrix.get(4, 1) * sin_theta * sin_theta);
        elementMatrix_globalized.set(3, 1, (elementMatrix.get(3, 0)- elementMatrix.get(4, 1) ) * cos_theta * sin_theta);
        elementMatrix_globalized.set(3, 2, elementMatrix.get(4, 2) * -sin_theta);
        elementMatrix_globalized.set(3, 3, elementMatrix.get(3, 3) * cos_theta * cos_theta + elementMatrix.get(4, 4) * sin_theta * sin_theta);
        elementMatrix_globalized.set(3, 4, (elementMatrix.get(3, 3)- elementMatrix.get(4, 4)) * cos_theta * sin_theta);
        elementMatrix_globalized.set(3, 5, elementMatrix.get(4, 5) * -sin_theta);

        elementMatrix_globalized.set(4, 0, (elementMatrix.get(3, 0)- elementMatrix.get(4, 1)) * cos_theta * sin_theta);
        elementMatrix_globalized.set(4, 1, elementMatrix.get(4, 1) * cos_theta * cos_theta + elementMatrix.get(3, 0) * sin_theta * sin_theta);
        elementMatrix_globalized.set(4, 2, elementMatrix.get(4, 2) * cos_theta);
        elementMatrix_globalized.set(4, 3, (elementMatrix.get(3, 3)- elementMatrix.get(4, 4)) * cos_theta * sin_theta);
        elementMatrix_globalized.set(4, 4, elementMatrix.get(4, 4) * cos_theta * cos_theta + elementMatrix.get(3, 3) * sin_theta * sin_theta);
        elementMatrix_globalized.set(4, 5, elementMatrix.get(4, 5) * cos_theta);

        elementMatrix_globalized.set(5, 0, elementMatrix.get(5, 1) * -sin_theta);
        elementMatrix_globalized.set(5, 1, elementMatrix.get(5, 1) * cos_theta);
        elementMatrix_globalized.set(5, 2, elementMatrix.get(5, 2));
        elementMatrix_globalized.set(5, 3, elementMatrix.get(4, 5) * -sin_theta);
        elementMatrix_globalized.set(5, 4, elementMatrix.get(4, 5) * cos_theta);
        elementMatrix_globalized.set(5, 5, elementMatrix.get(5, 5));


        return elementMatrix_globalized;
    }


    public Beam(Node startNode, Node endNode) {
        this(startNode, endNode, 10f);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }


    public List<Double> getdofsGlobalToLocal(){
        double[] u= new double[6];
        u[0]= startNode.getCurrentX(); //x-displacement of startnode
        u[1]=startNode.getCurrentY(); //y-displacement of startnode
        u[2]=endNode.getCurrentX();   //x-displacement of endnode
        u[3]=endNode.getCurrentY();


        //TODO: mabye refactor and remove u and always use localdisplacement array

        localdisplacements.add(u[0]*Math.cos(theta)-u[1]*Math.sin(theta));
        localdisplacements.add(u[0]*Math.sin(theta)+u[1]*Math.cos(theta));
        localdisplacements.add(u[2]*Math.cos(theta)-u[3]*Math.sin(theta));
        localdisplacements.add(u[2]*Math.sin(theta)+u[3]*Math.cos(theta));

        //TODO rotations aren't rotated therefore saving them them from global to local and vice versa isn't necessary
        localdisplacements.add((startNode.getCurrentRotations().get(0)));
        localdisplacements.add((endNode.getCurrentRotations().get(0)));

        return localdisplacements;
    }

    public List<Double> getdofsLocalToGlobal(){
        double[] u= new double[6];
        u[0]= startNode.getCurrentX(); //x-displacement of startnode
        u[1]=startNode.getCurrentY(); //y-displacement of startnode
        u[2]=endNode.getCurrentX();   //x-displacement of endnode
        u[3]=endNode.getCurrentY();

        //TODO: mabye refactor and remove u and always use localdisplacement array

        localdisplacements.add(u[0]*Math.cos(theta)+u[1]*Math.sin(theta));
        localdisplacements.add(-u[0]*Math.sin(theta)+u[1]*Math.cos(theta));
        localdisplacements.add(u[2]*Math.cos(theta)+u[3]*Math.sin(theta));
        localdisplacements.add(-u[2]*Math.sin(theta)+u[3]*Math.cos(theta));
        localdisplacements.add((startNode.getCurrentRotations().get(0)));
        localdisplacements.add((endNode.getCurrentRotations().get(0)));

        return localdisplacements;
    }

    public int[] getDofs() {
        return dofs;
    }

    public double getLength() {
        return length;
    }

    public Material getMaterial() {
        return material;
    }

    public void setDofs(int[] dofs) {
        this.dofs = dofs;
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

    public DenseMatrix64F getElementStiffnessMatrix() {
        return elementStiffnessMatrix;
    }

    public DenseMatrix64F getElementMassMatrix() {
        return elementMassMatrix;
    }

    public DenseMatrix64F getElementStiffnessMatrix_globalized(){
        return elementStiffnessMatrix_globalized;
    }

    public DenseMatrix64F getElementMassMatrix_globalized(){
        return elementMassMatrix_globalized;
    }
}
