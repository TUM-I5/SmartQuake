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
        canvas.drawLine((float) startNode.getCurrentX(), (float) startNode.getCurrentY(),
                (float) endNode.getCurrentX(), (float) endNode.getCurrentY(), paint);
    }

    public static void drawNode(Node node, Canvas canvas, Paint paint) {
        canvas.drawCircle((float) node.getCurrentX(), (float) node.getCurrentY(), (float) NODE_RADIUS_PIXEL, paint);
    }
}
