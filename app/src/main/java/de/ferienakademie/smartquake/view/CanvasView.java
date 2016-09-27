package de.ferienakademie.smartquake.view;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;

public class CanvasView extends View {
    // for future reference: 1 dpi = 100 / 2.54 pixels per meter
    // get dpi with context.getResources().getDisplayMetrics().xdpi

    public static final double SIDE_MARGIN_SCREEN_FRACTION = 0.125;
    public static final double TOP_MARGIN_SCREEN_FRACTION = 0.125;
    public static final double BEAM_UNIT_SCREEN_FRACTION = 0.1;
    public static final Paint BEAM_PAINT = new Paint();
    public static final Paint HINGE_PAINT = new Paint();
    public static final Paint RULER_PAINT = new Paint();

    static {
        BEAM_PAINT.setColor(Color.RED);
        BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        BEAM_PAINT.setAntiAlias(true);

        RULER_PAINT.setColor(Color.BLACK);
        RULER_PAINT.setAntiAlias(true);

        HINGE_PAINT.setColor(Color.BLUE);
        HINGE_PAINT.setAntiAlias(true);
    }

    // TODO: improve?
    public boolean isBeingDrawn = false;
    private double[] screenCenteringOffsets = new double[2];
    private double[] negativeMinCorrections = new double[2];
    private double modelScaling, beamUnitSize;

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

    private float internalToScreen(double internal_value, Axis axis) {
        return (float) ((internal_value + negativeMinCorrections[axis.ordinal()]) * modelScaling + screenCenteringOffsets[axis.ordinal()]);
    }

    public void drawNode(Node node, Canvas canvas) {
        Paint nodePaint;
        if (node.isHinge()) nodePaint = HINGE_PAINT;
        else nodePaint = BEAM_PAINT;

        canvas.drawCircle(internalToScreen(node.getCurrentX(), Axis.X),
                internalToScreen(node.getCurrentY(), Axis.Y),
                (float) (node.getRadius() * beamUnitSize), nodePaint);
    }

    private static class InternalColorStop implements Comparable
    {
        int color;
        double pos;

        InternalColorStop(int color, double pos)
        {
            this.color = color;
            this.pos = pos;
        }


        @Override
        public int compareTo(Object o) {
            return Double.compare(pos, ((InternalColorStop)o).pos);
        }
    }

    private static final InternalColorStop[] colorStops =
            {
                    new InternalColorStop(Color.argb(255, 0, 0, 255), -250), //BLUE
                    new InternalColorStop(Color.argb(255, 0, 0, 0), 0), //BLACK
                    new InternalColorStop(Color.argb(255, 255, 0, 0), 250) //RED

            };

    private int lerp(int f, int s, double p)
    {
        //Standard linear interpolation.
        return Color.argb(
                (int)Math.round(Color.alpha(f) * (1 - p) + Color.alpha(s) * p),
                (int)Math.round(Color.red(f) * (1 - p) + Color.red(s) * p),
                (int)Math.round(Color.green(f) * (1 - p) + Color.green(s) * p),
                (int)Math.round(Color.blue(f) * (1 - p) + Color.blue(s) * p)
        );
    }

    private void beamDeformationColor(Beam beam, Paint paint)
    {
        double force = beam.calculateNormalForceOfBeam(); //Might still show some errors.

        //Please don't ask for a reason to take such a comparably const-high algo on a small list, I was just lazy.
        int idx = Arrays.binarySearch(colorStops, new InternalColorStop(0, force));

        //The case why this is needed is explained very well in the documentation.
        if (idx < 0) {
            idx = -(idx + 1);
        }
        //Handling
        if (idx <= 0)
        {
            paint.setColor(colorStops[0].color);
        }
        else if (idx >= colorStops.length)
        {
            paint.setColor(colorStops[colorStops.length - 1].color);
        }
        else
        {
            //We interpolate.
            InternalColorStop thisStep = colorStops[idx];
            InternalColorStop lastStep = colorStops[idx - 1];

            double factor = (force - lastStep.pos) / (thisStep.pos - lastStep.pos);

            int color = lerp(lastStep.color, thisStep.color, factor);

            paint.setColor(color);
        }
    }

    private void resetBeamColor(Paint paint)
    {
        paint.setColor(Color.RED);
    }

    private void drawBeam(Beam beam, Canvas canvas) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        Path p = new Path();

        p.moveTo(internalToScreen(startNode.getCurrentXf(), Axis.X), internalToScreen(startNode.getCurrentYf(), Axis.Y));

        int numberOfSegments = 20;
        double singleSegmentLength = beam.getLength() / numberOfSegments;

        for (float x = 0; x < beam.getLength(); x += singleSegmentLength) {
            double px = (endNode.getInitialX() - startNode.getInitialX()) / beam.getLength() * x + startNode.getInitialX();
            double py = (endNode.getInitialY() - startNode.getInitialY()) / beam.getLength() * x + startNode.getInitialY();

            float[] intermediateDisplacement = beam.getGlobalDisplacementAt(x);
            intermediateDisplacement[0] = internalToScreen(intermediateDisplacement[0] + px, Axis.X);
            intermediateDisplacement[1] = internalToScreen(intermediateDisplacement[1] + py, Axis.Y);
            p.lineTo(intermediateDisplacement[0], intermediateDisplacement[1]);
        }

        p.lineTo(internalToScreen(endNode.getCurrentXf(), Axis.X), internalToScreen(endNode.getCurrentYf(), Axis.Y));

        BEAM_PAINT.setStrokeWidth((float) (beam.getThickness() * beamUnitSize));

        //Sorry for this code.
        beamDeformationColor(beam, BEAM_PAINT);
        canvas.drawPath(p, BEAM_PAINT);
        resetBeamColor(BEAM_PAINT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        isBeingDrawn = true;
        super.onDraw(canvas);

        RULER_PAINT.setStrokeWidth(canvas.getWidth() / 144);
        RULER_PAINT.setTextSize(canvas.getHeight() / 38);

        double[] boundingBox = DrawHelper.boundingBox;
        double modelXSize = boundingBox[1] - boundingBox[0];
        double modelYSize = boundingBox[3] - boundingBox[2];
        // special case for single beam
        if (modelXSize == 0){
            modelXSize = 8;
        }
        if (modelYSize == 0) {
            modelYSize = 8;
        }

        double widthFitScaling = (1 - 2 * SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth() / modelXSize;
        double heightFitScaling = (1 - TOP_MARGIN_SCREEN_FRACTION) * canvas.getHeight() / modelYSize;

        if (widthFitScaling < heightFitScaling) {
            modelScaling = widthFitScaling;
            drawRuler(modelXSize, canvas);
        } else {
            modelScaling = heightFitScaling;
            drawRuler(modelYSize, canvas);
        }

        screenCenteringOffsets[0] = 0.5 * (canvas.getWidth() - modelXSize * modelScaling);
        screenCenteringOffsets[1] = canvas.getHeight() - modelYSize * modelScaling;
        beamUnitSize = canvas.getWidth() * BEAM_UNIT_SCREEN_FRACTION;

        if (boundingBox[0] < 0) {
            negativeMinCorrections[0] = -boundingBox[0];
        } else {
            negativeMinCorrections[0] = 0;
        }

        if (boundingBox[2] < 0) {
            negativeMinCorrections[1] = -boundingBox[1];
        } else {
            negativeMinCorrections[1] = 0;
        }

        BEAM_PAINT.setStyle(Paint.Style.STROKE);
        for (Beam beam : DrawHelper.snapBeams) {
            drawBeam(beam, canvas);
        }
        BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        for (Node node : DrawHelper.snapNodes) {
            drawNode(node, canvas);
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

    private enum Axis {
        X,
        Y
    }
}