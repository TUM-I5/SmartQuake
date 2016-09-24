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

public class CanvasView extends View {
    // for future reference: 1 dpi = 100 / 2.54 pixels per meter
    // get dpi with context.getResources().getDisplayMetrics().xdpi

    public static final Paint PAINT = new Paint();

    static {
        PAINT.setColor(Color.RED);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        PAINT.setAntiAlias(true);
    }

    public boolean isBeingDrawn = false;

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void drawBeam(Beam beam, Canvas canvas, double xOffset, double yOffset, double displayScaling) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        Path p = new Path();
        p.moveTo((float) (startNode.getCurrentX() * displayScaling + xOffset), (float) (startNode.getCurrentY() * displayScaling + yOffset));
        p.lineTo((float) (endNode.getCurrentX() * displayScaling + xOffset), (float) (endNode.getCurrentY() * displayScaling + yOffset));


        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeWidth((float) (beam.getThickness() * displayScaling));
        canvas.drawPath(p, PAINT);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public static void drawNode(Node node, Canvas canvas, double xOffset, double yOffset, double displayScaling) {
        canvas.drawCircle((float) (node.getCurrentX() * displayScaling + xOffset), (float) (node.getCurrentY()* displayScaling + yOffset),
                (float) (node.getRadius() * displayScaling), PAINT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        double[] modelSize = DrawHelper.boundingBox;

        double displayScaling = Math.min(0.75 * canvas.getWidth() / modelSize[0], 0.75 * canvas.getWidth() / modelSize[1]);

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