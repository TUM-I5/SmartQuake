package de.ferienakademie.smartquake.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;

/**
 * Created by yuriy on 21/09/16.
 */
public class DrawHelper {

    public static void drawBeam(Beam beam, Canvas canvas, Paint paint) {
        Node startNode = beam.getStartNode();
        canvas.drawCircle((float) startNode.getX(), (float) startNode.getY(), (float) startNode.getRadius(), paint);
        Node endNode = beam.getEndNode();
        canvas.drawCircle((float) endNode.getX(), (float) endNode.getY(), (float) endNode.getRadius(), paint);
        paint.setStrokeWidth(beam.getThickness());
        canvas.drawLine((float) startNode.getX(), (float) startNode.getY(), (float) endNode.getX(), (float) endNode.getY(), paint);
    }

}
