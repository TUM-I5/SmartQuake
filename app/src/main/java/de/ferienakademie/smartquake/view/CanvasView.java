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
        paint.setStrokeWidth(beam.getThickness());

        double startX = startNode.getCurrX();
        double startY = startNode.getCurrY();
        double endX = endNode.getCurrX();
        double endY = endNode.getCurrY();

        float cp1x = (float)((2 * startX + endX) / 3);
        float cp1y = (float)((2 * startY + endY) / 3);
        float cp2x = (float)((startX + 2 * endX) / 3);
        float cp2y = (float)((startY + 2 * endY) / 3);

        Path p = new Path();
        p.moveTo((float) startNode.getCurrX(), (float) startNode.getCurrY());
        p.cubicTo(cp1x, cp1y, cp2x, cp2y, (float) endNode.getCurrX(), (float) endNode.getCurrY());
        canvas.drawPath(p, paint);
    }
}
