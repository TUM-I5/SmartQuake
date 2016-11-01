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

package de.ferienakademie.smartquake.view;

import android.view.View;
import java.util.ArrayList;
import java.util.List;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;


/**
 * Created by yuriy on 22/09/16.
 */
public class DrawHelper {

    public static List<Node> snapNodes = new ArrayList<>();
    public static List<Beam> snapBeams = new ArrayList<>();
    public static double[] boundingBox = new double[4];

    public static void drawStructure(Structure structure, View view1, Integer selectedNodeId) {
        snapShot(structure.getNodes(), structure.getBeams());
        boundingBox = structure.getBoundingBox();
        if (view1 instanceof CanvasView) {
            CanvasView view = (CanvasView)view1;
            if (selectedNodeId != null) {
                view.setSelectedNodeId(selectedNodeId);
            }
            view.isBeingDrawn = true;
        }
        if (view1 instanceof DrawCanvasView) {
            DrawCanvasView view = (DrawCanvasView)view1;
            view.isBeingDrawn = true;
        }

        view1.postInvalidate();
    }

    public static void clearCanvas(View view1) {
        snapBeams.clear();
        snapNodes.clear();
        if (view1 instanceof CanvasView) {
            CanvasView view = (CanvasView)view1;
            view.isBeingDrawn = true;
        }
        if (view1 instanceof DrawCanvasView) {
            DrawCanvasView view = (DrawCanvasView)view1;
            view.isBeingDrawn = true;
        }
        view1.postInvalidate();
    }

    private static void snapShot(List<Node> nodes, List<Beam> beams) {
        snapBeams.clear();
        snapNodes.clear();
        snapBeams.addAll(beams);
        snapNodes.addAll(nodes);
    }

}
