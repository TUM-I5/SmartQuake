package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;

/**
 * Created by yuriy on 24/09/16.
 */
public class DrawCanvasView extends View {
    public static final Paint PAINT = new Paint();

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

    private void drawBeam(Beam beam, Canvas canvas, double xOffset, double yOffset, double displayScaling) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        Path p = new Path();
        p.moveTo((float) (startNode.getCurrX() * displayScaling + xOffset), (float) (startNode.getCurrY() * displayScaling + yOffset));
        p.lineTo((float) (endNode.getCurrX() * displayScaling + xOffset), (float) (endNode.getCurrY() * displayScaling + yOffset));


        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeWidth((float) (beam.getThickness() * displayScaling));
        canvas.drawPath(p, PAINT);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void drawNode(Node node, Canvas canvas, double xOffset, double yOffset, double displayScaling) {
        canvas.drawCircle((float) (node.getCurrX() * displayScaling + xOffset), (float) (node.getCurrY()* displayScaling + yOffset),
                (float) (node.getRadius() * displayScaling), PAINT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        double[] modelSize = DrawHelper.boundingBox;

        double displayScaling = Math.min(0.75 * canvas.getWidth() / modelSize[0], 0.75 * canvas.getHeight() / modelSize[1]);

        double xOffset = 0.5 * (canvas.getWidth() - modelSize[0] * displayScaling);
        double yOffset = canvas.getHeight() - modelSize[1] * displayScaling;
        for (Beam beam : DrawHelper.snapBeams) {
            drawBeam(beam, canvas, xOffset, yOffset, displayScaling);
        }
        for (Node node : DrawHelper.snapNodes) {
            drawNode(node, canvas, xOffset, yOffset, displayScaling);
        }

        isBeingDrawn = false;
    }
}
