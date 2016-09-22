package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

public class CanvasView extends View {

    public static final Paint PAINT = new Paint();

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


    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        PAINT.setColor(Color.RED);
        PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        PAINT.setAntiAlias(true);

        for (Beam beam : DrawHelper.snapBeams) {
            drawBeam(beam, canvas, PAINT);
        }
        isBeingDrawn = false;
    }

    public static void drawBeam(Beam beam, Canvas canvas, Paint paint) {
        Node startNode = beam.getStartNode();
        canvas.drawCircle((float) startNode.getX(), (float) startNode.getY(), (float) startNode.getRadius(), paint);
        Node endNode = beam.getEndNode();
        canvas.drawCircle((float) endNode.getX(), (float) endNode.getY(), (float) endNode.getRadius(), paint);
        paint.setStrokeWidth(beam.getThickness());
        canvas.drawLine((float) startNode.getX(), (float) startNode.getY(), (float) endNode.getX(), (float) endNode.getY(), paint);
    }
}
