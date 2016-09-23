package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;

public class CanvasView extends View {
    // for future reference: 1 dpi = 100 / 2.54 pixels per meter
    // get dpi with context.getResources().getDisplayMetrics().xdpi

    private final Paint paint = new Paint();
    private Canvas canvas;

    //TODO: this really shouldn't be public
    public boolean isBeingDrawn = false;

    public CanvasView(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    private void drawAll(List<Beam> beams, double[] modelSize) {
        double displayScaling = Math.min(0.75 * canvas.getWidth() / modelSize[0], 0.75 * canvas.getWidth() / modelSize[1]);

        double xOffset = 0.5 * (canvas.getWidth() - modelSize[0] * displayScaling);
        double yOffset = canvas.getHeight() - modelSize[1] * displayScaling;
        for (Beam b: beams) {
            drawBeam(b, xOffset, yOffset, displayScaling);
        }
    }

    private void drawBeam(Beam beam, double xOffset, double yOffset, double displayScaling) {
        Node startNode = beam.getStartNode();
        canvas.drawCircle((float) (startNode.getCurrX() * displayScaling + xOffset), (float) (startNode.getCurrY()* displayScaling + yOffset),
                (float) (startNode.getRadius() * displayScaling), paint);
        Node endNode = beam.getEndNode();
        canvas.drawCircle((float) (endNode.getCurrX() * displayScaling + xOffset), (float) (endNode.getCurrY() * displayScaling+ yOffset),
                (float) (endNode.getRadius() * displayScaling), paint);

        Path p = new Path();
        p.moveTo((float) (startNode.getCurrX() * displayScaling + xOffset), (float) (startNode.getCurrY() * displayScaling + yOffset));
        p.lineTo((float) (endNode.getCurrX() * displayScaling + xOffset), (float) (endNode.getCurrY() * displayScaling + yOffset));


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) (beam.getThickness() * displayScaling));
        canvas.drawPath(p, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);
        this.canvas = canvas;

        drawAll(DrawHelper.getSnapBeams(), DrawHelper.getBoundingBox());
        isBeingDrawn = false;
    }
}
