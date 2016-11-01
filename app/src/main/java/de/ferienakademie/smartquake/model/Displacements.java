// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

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
