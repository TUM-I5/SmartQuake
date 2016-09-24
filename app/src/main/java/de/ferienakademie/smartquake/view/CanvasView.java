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

    private static final Paint BEAM_PAINT = new Paint();
    private static final Paint RULER_PAINT = new Paint();

    public static final double SIDE_MARGIN_SCREEN_FRACTION = 0.125;
    public static final double TOP_MARGIN_SCREEN_FRACTION = 0.125;
    public static final double BEAM_UNIT_SCREEN_FRACTION = 0.1;

    static {
        BEAM_PAINT.setColor(Color.RED);
        BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        BEAM_PAINT.setAntiAlias(true);

        RULER_PAINT.setColor(Color.BLACK);
        RULER_PAINT.setAntiAlias(true);
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

    public static void drawNode(Node node, Canvas canvas, double xOffset, double yOffset, double modelScaling, double beamUnitSize) {
        canvas.drawCircle((float) (node.getCurrX() * modelScaling + xOffset), (float) (node.getCurrY() * modelScaling + yOffset),
                (float) (node.getRadius() * beamUnitSize), BEAM_PAINT);
    }

    private void drawBeam(Beam beam, Canvas canvas, double xOffset, double yOffset, double modelScaling, double beamUnitSize) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        Path p = new Path();
        p.moveTo((float) (startNode.getCurrentX() * modelScaling + xOffset), (float) (startNode.getCurrentY() * modelScaling + yOffset));
        p.lineTo((float) (endNode.getCurrentX() * modelScaling + xOffset), (float) (endNode.getCurrentY() * modelScaling + yOffset));


        BEAM_PAINT.setStrokeWidth((float) (beam.getThickness() * beamUnitSize));
        canvas.drawPath(p, BEAM_PAINT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        RULER_PAINT.setStrokeWidth(canvas.getWidth() / 144);
        RULER_PAINT.setTextSize(canvas.getHeight() / 38);

        double[] modelSize = DrawHelper.boundingBox;

        double widthFitScaling = (1 - 2 * SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth() / modelSize[0];
        double heightFitScaling = (1 - TOP_MARGIN_SCREEN_FRACTION) * canvas.getHeight() / modelSize[1];
        double modelScaling;

        if (widthFitScaling < heightFitScaling) {
            modelScaling = widthFitScaling;
            drawRuler(modelSize[0], canvas);
        } else {
            modelScaling = heightFitScaling;
            drawRuler(modelSize[1], canvas);
        }

        double xOffset = 0.5 * (canvas.getWidth() - modelSize[0] * modelScaling);
        double yOffset = canvas.getHeight() - modelSize[1] * modelScaling;
        double beamUnitSize = canvas.getWidth() * BEAM_UNIT_SCREEN_FRACTION;


        BEAM_PAINT.setStyle(Paint.Style.STROKE);
        for (Beam beam : DrawHelper.snapBeams) {
            drawBeam(beam, canvas, xOffset, yOffset, modelScaling, beamUnitSize);
        }
        BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        for (Node node : DrawHelper.snapNodes) {
            drawNode(node, canvas, xOffset, yOffset, modelScaling, beamUnitSize);
        }

        isBeingDrawn = false;
    }

    private void drawRuler(double meterWidth, Canvas canvas) {
        canvas.drawLine((float) (SIDE_MARGIN_SCREEN_FRACTION * canvas.getWidth()), (float) (0.5 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                (float) ((1 - SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth()), (float) (0.5 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                RULER_PAINT);
        canvas.drawText(Double.toString(meterWidth) + " meter(s)",
                (float) SIDE_MARGIN_SCREEN_FRACTION * canvas.getWidth(),
                (float) ((TOP_MARGIN_SCREEN_FRACTION - 0.025) * canvas.getHeight()), RULER_PAINT);
    }
}