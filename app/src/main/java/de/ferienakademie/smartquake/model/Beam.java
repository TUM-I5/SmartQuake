package de.ferienakademie.smartquake.model;

import org.ejml.data.DenseMatrix64F;

import java.util.List;

import de.ferienakademie.smartquake.BuildConfig;

/**
 * Created by yuriy on 21/09/16.
 */

public class Beam {

    private static Material stdMaterial = new Material();

    private Node startNode;
    private Node endNode;
    private Material material = stdMaterial;
    private float thickness = 0.1f;
    private double length;
    private double sin_theta;
    private double cos_theta;
    private double theta;

    private double[] displacement;


    /**
     *array of degrees of freedom in format [x1, y1, rotation1, x2, y2, rotation2]
      */
    private int[] dofs;



    private DenseMatrix64F elementStiffnessMatrix;
    private DenseMatrix64F elementMassMatrix;

    private DenseMatrix64F elementStiffnessMatrix_globalized;
    private DenseMatrix64F elementMassMatrix_globalized;

    public Beam(Node startNode, Node endNode, float thickness) {
        this.dofs = new int[6];
        this.startNode = startNode;
        this.endNode = endNode;
        startNode.addBeam(this);
        endNode.addBeam(this);
        this.thickness = thickness;
        material = stdMaterial;
    }

    //Kernel1 constructor
    public Beam(Node startNode, Node endNode, Material material) {

        this(startNode, endNode, 0.1f);
        this.displacement = new double[6];
        this.material = material;
        double x1 = startNode.getInitialX(), y1 = startNode.getInitialY();
        double x2 = endNode.getInitialX(), y2 = endNode.getInitialY();
        length = computeLength();

        theta = Math.atan2(y2 - y1, x2 - x1);
        cos_theta = Math.cos(theta); //rotation of displacement
        sin_theta = Math.sin(theta);


    }

    public void computeAll(boolean lumped) {

        //this.dofs = new int[]{
        //        startNode.getDOF().get(0), startNode.getDOF().get(1), startNode.getDOF().get(2),
        //        endNode.getDOF().get(0), endNode.getDOF().get(1), endNode.getDOF().get(2)
        //};

        double x1 = startNode.getInitialX(), y1 = startNode.getInitialY();
        double x2 = endNode.getInitialX(), y2 = endNode.getInitialY();
        length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        theta = Math.atan2(y2 - y1, x2 - x1);
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

    double computeLength() {
        double x1 = startNode.getInitialX();
        double y1 = startNode.getInitialY();
        double x2 = endNode.getInitialX();
        double y2 = endNode.getInitialY();

        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
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
        elementStiffnessMatrix.set(5, 5, 4 * EI / (length));
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
        this(startNode, endNode, 0.1f);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }


    /**
     * Transform global displacements to local displacements.
     * @return local displacements in form:<br>axialDisplacementStartNode, orthogonalDisplacementStartNode, rotationStartNode,
    axialDisplacementEndNode, orthogonalDisplacementEndNode, rotationEndNode
     */
    public Displacements getLocalDisplacements() {
        double startNodeDisplacementX = startNode.getCurrentX() - startNode.getInitialX();
        double startNodeDisplacementY = startNode.getCurrentY() - startNode.getInitialY();
        double endNodeDisplacementX   = endNode.getCurrentX() - endNode.getInitialX();
        double endNodeDisplacementY   = endNode.getCurrentY() - endNode.getInitialY();

        double axialDisplacementStartNode =
                computeLocalAxialDisplacement(startNodeDisplacementX, startNodeDisplacementY);
        double orthogonalDisplacementStartNode =
                computeLocalOrthogonalDisplacement(startNodeDisplacementX, startNodeDisplacementY);
        double axialDisplacementEndNode =
                computeLocalAxialDisplacement(endNodeDisplacementX, endNodeDisplacementY);
        double orthogonalDisplacementEndNode =
                computeLocalOrthogonalDisplacement(endNodeDisplacementX, endNodeDisplacementY);

        /*
        if (BuildConfig.DEBUG) { // assert that formulas are right
            double eps = 0.01;
            double v = startNodeDisplacementX * Math.cos(theta) + startNodeDisplacementY * Math.sin(theta);
            if (Math.abs(axialDisplacementStartNode - v) > eps) {
                throw new AssertionError("axialDisplacementStartNode not right: " + axialDisplacementStartNode + " should be " + v);
            }

            double v1 = -startNodeDisplacementX * Math.sin(theta) + startNodeDisplacementY * Math.cos(theta);
            if (Math.abs(orthogonalDisplacementStartNode - v1) > eps) {
                throw new AssertionError("orthogonalStartNode not right: " + orthogonalDisplacementEndNode + " should be " + v1);
            }

            double v2 = endNodeDisplacementX * Math.cos(theta) + endNodeDisplacementY * Math.sin(theta);
            if (Math.abs(axialDisplacementEndNode - v2) > eps) {
                throw new AssertionError("axialDisplacementEndNode not right: " + axialDisplacementEndNode + " should be " + v2);
            }

            double v3 = -endNodeDisplacementX * Math.sin(theta) + endNodeDisplacementY * Math.cos(theta);
            if (Math.abs(orthogonalDisplacementEndNode - v3) > eps) {
                throw new AssertionError("orthogonalDisplacementEndNode wrong: " + orthogonalDisplacementEndNode + " should be " + v3);
            }
        }
        */

        double rotationStartNode = startNode.getCurrentRotations().get(0);
        double rotationEndNode = endNode.getCurrentRotations().get(0);

        return new Displacements(axialDisplacementStartNode, orthogonalDisplacementStartNode, rotationStartNode,
                            axialDisplacementEndNode, orthogonalDisplacementEndNode, rotationEndNode);
    }

    /**
     * U = axial
     * W = orthogonal
     */

    public float[] getGlobalDisplacementAt(double _x) {
        double axialDisplacement = getAxialDisplacement(_x);
        double orthogonalDisplacement = getOrthogonalDisplacement(_x);

        double u = axialDisplacement * Math.cos(theta) - orthogonalDisplacement * Math.sin(theta);
        double w = axialDisplacement * Math.sin(theta) + orthogonalDisplacement * Math.cos(theta);

        return new float[]{(float) u, (float) w};
    }

    private double getOrthogonalDisplacement(double _x) {
        double orthogonalDisplacementStartNode, orthogonalDisplacementEndNode,
                rotationStartNode, rotationEndNode, initialLength;

        Displacements localDisplacements = this.getLocalDisplacements();

        orthogonalDisplacementStartNode = localDisplacements.getOrthogonalDisplacementStartNode();
        orthogonalDisplacementEndNode = localDisplacements.getOrthogonalDisplacementEndNode();
        rotationStartNode = localDisplacements.getRotationStartNode();
        rotationEndNode = localDisplacements.getRotationEndNode();

        initialLength = this.getLength();

        double xl = _x / initialLength;
        double xl2 = xl * xl;
        double xl3 = xl2 * xl;

        double h1 = 1 - 3 * xl2 + 2 * xl3;
        double h2 = (- _x * (xl - 1) * (xl - 1));
        double h3 = 3 * xl2 - 2 * xl3;
        double h4 = ((_x * xl) * (1 - xl));

        return h1 * orthogonalDisplacementStartNode + h2 * rotationStartNode + h3 * orthogonalDisplacementEndNode + h4 * rotationEndNode;
    }

    private double getAxialDisplacement(double _x) {
        Displacements localDisplacements = this.getLocalDisplacements();

        double axialDisplacementStartNode = localDisplacements.getAxialDisplacementStartNode();
        double axialDisplacementEndNode = localDisplacements.getAxialDisplacementEndNode();

        double initialLength = this.getLength();
        double xl = _x / initialLength;

        return xl * axialDisplacementEndNode + (1 - xl) * axialDisplacementStartNode;
    }

    private double computeLocalAxialDisplacement(double x, double y) {
        return x * Math.cos(theta) + y * Math.sin(theta);
    }

    private double computeLocalOrthogonalDisplacement(double x, double y) {
        return -x * Math.sin(theta) + y * Math.cos(theta);
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

    public void setSingleDof(int i, int dof){this.dofs[i]=dof; };

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



    public void setSingleDisplacement(int i,double displacement) {
        this.displacement[i] = displacement;
    }

    public DenseMatrix64F getElementMassMatrix_globalized(){
        return elementMassMatrix_globalized;
    }

    public void setRotationDOF(boolean left, int i){
        if(left){

        }

    }
}
