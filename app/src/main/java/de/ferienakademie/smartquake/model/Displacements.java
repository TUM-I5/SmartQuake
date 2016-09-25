package de.ferienakademie.smartquake.model;

/**
 * Created by Maximilian Berger on 9/25/16.
 */
public class Displacements {
    private double axialDisplacementStartNode, orthogonalDisplacementStartNode, rotationStartNode,
    axialDisplacementEndNode, orthogonalDisplacementEndNode, rotationEndNode;

    public Displacements(double axialDisplacementStartNode, double orthogonalDisplacementStartNode,
                         double rotationStartNode, double axialDisplacementEndNode,
                         double orthogonalDisplacementEndNode, double rotationEndNode) {
        this.axialDisplacementStartNode = axialDisplacementStartNode;
        this.orthogonalDisplacementStartNode = orthogonalDisplacementStartNode;
        this.rotationStartNode = rotationStartNode;
        this.axialDisplacementEndNode = axialDisplacementEndNode;
        this.orthogonalDisplacementEndNode = orthogonalDisplacementEndNode;
        this.rotationEndNode = rotationEndNode;
    }

    public double getAxialDisplacementStartNode() {
        return axialDisplacementStartNode;
    }

    public void setAxialDisplacementStartNode(double axialDisplacementStartNode) {
        this.axialDisplacementStartNode = axialDisplacementStartNode;
    }

    public double getOrthogonalDisplacementStartNode() {
        return orthogonalDisplacementStartNode;
    }

    public void setOrthogonalDisplacementStartNode(double orthogonalDisplacementStartNode) {
        this.orthogonalDisplacementStartNode = orthogonalDisplacementStartNode;
    }

    public double getRotationStartNode() {
        return rotationStartNode;
    }

    public void setRotationStartNode(double rotationStartNode) {
        this.rotationStartNode = rotationStartNode;
    }

    public double getAxialDisplacementEndNode() {
        return axialDisplacementEndNode;
    }

    public void setAxialDisplacementEndNode(double axialDisplacementEndNode) {
        this.axialDisplacementEndNode = axialDisplacementEndNode;
    }

    public double getOrthogonalDisplacementEndNode() {
        return orthogonalDisplacementEndNode;
    }

    public void setOrthogonalDisplacementEndNode(double orthogonalDisplacementEndNode) {
        this.orthogonalDisplacementEndNode = orthogonalDisplacementEndNode;
    }

    public double getRotationEndNode() {
        return rotationEndNode;
    }

    public void setRotationEndNode(double rotationEndNode) {
        this.rotationEndNode = rotationEndNode;
    }
}
