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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;


/**
 * Created by yuriy on 24/09/16.
 */
public class DrawCanvasView extends View {
    public static final Paint PAINT = new Paint();

    public static final double NODE_RADIUS_PIXEL = 15;

    static {
        PAINT.setColor(Color.RED);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        PAINT.setAntiAlias(true);
    }

    public boolean isBeingDrawn = false;

    public DrawCanvasView(Context context) {
        super(context);
    }

    public DrawCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawCanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        for (Beam beam : DrawHelper.snapBeams) {
            drawBeam(beam, canvas, PAINT);
        }
        for (Node node : DrawHelper.snapNodes) {
            drawNode(node, canvas, PAINT);
        }
        isBeingDrawn = false;
    }

    public static void drawBeam(Beam beam, Canvas canvas, Paint paint) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        paint.setStrokeWidth(10);
        canvas.drawLine((float) startNode.getInitialX(), (float) startNode.getInitialY(),
                (float) endNode.getInitialX(), (float) endNode.getInitialY(), paint);
    }

    public static void drawNode(Node node, Canvas canvas, Paint paint) {
        Paint nodePaint;
        if (node.isHinge()) nodePaint = CanvasView.HINGE_PAINT;
        else nodePaint = CanvasView.BEAM_PAINT;
        canvas.drawCircle((float) node.getInitialX(), (float) node.getInitialY(), (float) NODE_RADIUS_PIXEL, nodePaint);
    }
}
