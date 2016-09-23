package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;

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
        canvas.drawCircle((float) startNode.getCurrX(), (float) startNode.getCurrY(), (float) startNode.getRadius(), paint);
        Node endNode = beam.getEndNode();
        canvas.drawCircle((float) endNode.getCurrX(), (float) endNode.getCurrY(), (float) endNode.getRadius(), paint);


        Pair<Float, Float> oneThirdPoint = beam.getOneThirdPoint();
        Pair<Float, Float> twoThirdPoint = beam.getTwoThirdPoint();

        Path p = new Path();
        p.moveTo((float) startNode.getCurrX(), (float) startNode.getCurrY());
        p.cubicTo(oneThirdPoint.first, oneThirdPoint.second, twoThirdPoint.first, twoThirdPoint.second, (float) endNode.getCurrX(), (float) endNode.getCurrY());

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(beam.getThickness());
        canvas.drawPath(p, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }
}
