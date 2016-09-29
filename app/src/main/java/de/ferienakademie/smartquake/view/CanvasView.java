package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

import de.ferienakademie.smartquake.managers.PreferenceReader;
import de.ferienakademie.smartquake.model.Beam;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

public class CanvasView extends View {

    public static final double SIDE_MARGIN_SCREEN_FRACTION = 0.125;
    public static final double TOP_MARGIN_SCREEN_FRACTION = 0.125;
    // for future reference: 1 dpi = 100 / 2.54 pixels per meter
    // get dpi with context.getResources().getDisplayMetrics().xdpi
    public static final double BEAM_UNIT_SCREEN_FRACTION = 0.1;
    public static final Paint BEAM_PAINT = new Paint();
    public static final Paint HINGE_PAINT = new Paint();
    public static final Paint RULER_PAINT = new Paint();
    public static final Paint SELECTION_PAINT = new Paint();
    private static final InternalColorStop[] colorStops =
            {
                    new InternalColorStop(Color.argb(255, 0, 0, 0), 0), //BLACK
                    new InternalColorStop(Color.argb(255, 255, 0, 0), 1) //RED

            };

    static {
        BEAM_PAINT.setColor(Color.RED);
        BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        BEAM_PAINT.setAntiAlias(true);

        RULER_PAINT.setColor(Color.BLACK);
        RULER_PAINT.setAntiAlias(true);

        HINGE_PAINT.setColor(Color.BLUE);
        HINGE_PAINT.setAntiAlias(true);

        SELECTION_PAINT.setColor(Color.CYAN);
        SELECTION_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
        SELECTION_PAINT.setAntiAlias(true);
    }

    public boolean includeRuler = true;
    public boolean centerOnFirstNode = false;
    // TODO: improve?
    public boolean isBeingDrawn = false;
    private NodePositionChoiceListener nodePositionChoiceListener;
    private GestureDetectorCompat mGestureDetector;
    private StructureProvider structureProvider;
    private Integer selectedNodeId;
    private double[] screenCenteringOffsets = new double[2];
    private double[] negativeMinCorrections = new double[2];
    private double modelScaling, beamUnitSize;

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetectorCompat(getContext(), new CanvasViewLongPressListener());
    }
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Integer getSelectedNodeId() {
        return selectedNodeId;
    }

    public void setSelectedNodeId(Integer selectedNodeId) {
        this.selectedNodeId = selectedNodeId;
    }

    public void setNodePositionChoiceListener(NodePositionChoiceListener listener) {
        this.nodePositionChoiceListener = listener;
    }

    public void setStructureProvider(StructureProvider structureProvider) {
        this.structureProvider = structureProvider;
    }

    private float internalToScreen(double internalValue, Axis axis) {
        return (float) ((internalValue + negativeMinCorrections[axis.ordinal()]) * modelScaling + screenCenteringOffsets[axis.ordinal()]);
    }

    private float screenToInternal(double screenValue, Axis axis) {
        return (float) ((screenValue - screenCenteringOffsets[axis.ordinal()]) / modelScaling - negativeMinCorrections[axis.ordinal()]);
    }

    private int lerp(int f, int s, double p) {
        //Standard linear interpolation.
        return Color.argb(
                (int) Math.round(Color.alpha(f) * (1 - p) + Color.alpha(s) * p),
                (int) Math.round(Color.red(f) * (1 - p) + Color.red(s) * p),
                (int) Math.round(Color.green(f) * (1 - p) + Color.green(s) * p),
                (int) Math.round(Color.blue(f) * (1 - p) + Color.blue(s) * p)
        );
    }

    private void beamDeformationColor(Beam beam, Paint paint) {
        //Handling
        if (!PreferenceReader.showColors()) {
            paint.setColor(Color.BLACK);
            return;
        }

        if (beam.isOverloaded()) {
            paint.setColor(Color.argb(127, 255, 0, 0));
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        } else {

            double force = beam.returnMaximumStress() / beam.getTensileStrength(); //Might still show some errors.

            //Please don't ask for a reason to take such a comparably const-high algo on a small list, I was just lazy.
            int idx = Arrays.binarySearch(colorStops, new InternalColorStop(0, force));

            //The case why this is needed is explained very well in the documentation.
            if (idx < 0) {
                idx = -(idx + 1);
            }
            //Handling
            if (idx <= 0) {
                paint.setColor(colorStops[0].color);
            } else if (idx >= colorStops.length) {
                paint.setColor(colorStops[colorStops.length - 1].color);
            } else {
                //We interpolate.
                InternalColorStop thisStep = colorStops[idx];
                InternalColorStop lastStep = colorStops[idx - 1];

                double factor = (force - lastStep.pos) / (thisStep.pos - lastStep.pos);

                int color = lerp(lastStep.color, thisStep.color, factor);

                paint.setColor(color);
            }
        }
    }

    private void resetBeamColor(Paint paint) {
        paint.setColor(Color.BLACK);
        paint.setPathEffect(null);
    }

    public void drawNode(Node node, Canvas canvas, boolean nodeSelected) {
        Paint nodePaint;
        if (node.isHinge()) nodePaint = HINGE_PAINT;
        else nodePaint = BEAM_PAINT;

        float xCoord = internalToScreen(node.getCurrentX(), Axis.X);
        float yCoord = internalToScreen(node.getCurrentY(), Axis.Y);
        float xCorrection = 0;
        float yCorrection = 0;

        if (centerOnFirstNode) {
            xCorrection = canvas.getWidth() * 0.5f - internalToScreen(structureProvider.getStructure().getNodes().get(selectedNodeId).getCurrentX(), Axis.X);
            yCorrection = canvas.getWidth() * 0.5f - internalToScreen(structureProvider.getStructure().getNodes().get(selectedNodeId).getCurrentY(), Axis.Y);
        }
        canvas.drawCircle(xCoord + xCorrection,
                yCoord + yCorrection,
                (float) (node.getRadius() * beamUnitSize), nodePaint);

        if (nodeSelected) {
            canvas.drawCircle(xCoord + xCorrection,
                    yCoord + yCorrection,
                    (float) (node.getRadius() * beamUnitSize * 0.95), SELECTION_PAINT);
        }
    }

    private void drawBeam(Beam beam, Canvas canvas) {
        Node startNode = beam.getStartNode();
        Node endNode = beam.getEndNode();
        Path p = new Path();

        float xCorrection = 0;
        float yCorrection = 0;

        if (centerOnFirstNode) {
            xCorrection = canvas.getWidth() * 0.5f - internalToScreen(structureProvider.getStructure().getNodes().get(selectedNodeId).getCurrentX(), Axis.X);
            yCorrection = canvas.getWidth() * 0.5f - internalToScreen(structureProvider.getStructure().getNodes().get(selectedNodeId).getCurrentY(), Axis.Y);
        }

        p.moveTo(internalToScreen(startNode.getCurrentX(), Axis.X) + xCorrection, internalToScreen(startNode.getCurrentY(), Axis.Y) + yCorrection);

        int numberOfSegments = 20;
        double singleSegmentLength = beam.getLength() / numberOfSegments;

        for (float x = 0; x < beam.getLength(); x += singleSegmentLength) {
            double px = (endNode.getInitialX() - startNode.getInitialX()) / beam.getLength() * x + startNode.getInitialX();
            double py = (endNode.getInitialY() - startNode.getInitialY()) / beam.getLength() * x + startNode.getInitialY();

            float[] intermediateDisplacement = beam.getGlobalDisplacementAt(x);
            intermediateDisplacement[0] = internalToScreen(intermediateDisplacement[0] + px, Axis.X) + xCorrection;
            intermediateDisplacement[1] = internalToScreen(intermediateDisplacement[1] + py, Axis.Y) + yCorrection;
            p.lineTo(intermediateDisplacement[0], intermediateDisplacement[1]);
        }

        p.lineTo(internalToScreen(endNode.getCurrentXf(), Axis.X) + xCorrection, internalToScreen(endNode.getCurrentYf(), Axis.Y) + yCorrection);

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

        Structure s = null;
        double[] boundingBox;
        if (structureProvider != null) {
            s = structureProvider.getStructure();
            boundingBox = s.getBoundingBox();
        } else {
            boundingBox = DrawHelper.boundingBox;
        }

        double modelXSize = boundingBox[1] - boundingBox[0];
        double modelYSize = boundingBox[3] - boundingBox[2];
        // special case for single beam
        if (modelXSize == 0) {
            modelXSize = 8;
        }
        if (modelYSize == 0) {
            modelYSize = 8;
        }

        double widthFitScaling = (1 - 2 * SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth() / modelXSize;
        double heightFitScaling = (1 - TOP_MARGIN_SCREEN_FRACTION) * canvas.getHeight() / modelYSize;

        if (widthFitScaling < heightFitScaling) {
            modelScaling = widthFitScaling;
            if (includeRuler) {
                drawRuler(modelXSize, canvas);
            }
        } else {
            modelScaling = heightFitScaling;
            if (includeRuler) {
                drawRuler(modelYSize, canvas);
            }
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

        if (s != null) {
            for (Beam beam : s.getBeams()) {
                drawBeam(beam, canvas);
            }
            BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
            boolean nodeSelected = false;
            for (int i = 0; i < s.getNodes().size(); ++i) {
                if (selectedNodeId != null && selectedNodeId == i) {
                    nodeSelected = true;
                }
                drawNode(s.getNodes().get(i), canvas, nodeSelected);

                if (nodeSelected) {
                    nodeSelected = false;
                }
            }
        } else {
            for (Beam beam : DrawHelper.snapBeams) {
                drawBeam(beam, canvas);
            }
            BEAM_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
            boolean nodeSelected = false;
            for (int i = 0; i < DrawHelper.snapNodes.size(); ++i) {
                if (selectedNodeId != null && selectedNodeId == i) {
                    nodeSelected = true;
                }
                drawNode(DrawHelper.snapNodes.get(i), canvas, nodeSelected);

                if (nodeSelected) {
                    nodeSelected = false;
                }
            }
        }

        isBeingDrawn = false;
    }

    private void drawRuler(double meterWidth, Canvas canvas) {
        canvas.drawLine((float) (SIDE_MARGIN_SCREEN_FRACTION * canvas.getWidth()), (float) (0.5 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                (float) ((1 - SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth()), (float) (0.5 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                RULER_PAINT);
        canvas.drawLine((float) (SIDE_MARGIN_SCREEN_FRACTION * canvas.getWidth()), (float) (TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight() * 3.0 / 8.0),
                (float) (SIDE_MARGIN_SCREEN_FRACTION * canvas.getWidth()), (float) (5.0 / 8.0 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                RULER_PAINT);
        canvas.drawLine((float) ((1 - SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth()), (float) (TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight() * 3.0 / 8.0),
                (float) ((1 - SIDE_MARGIN_SCREEN_FRACTION) * canvas.getWidth()), (float) (5.0 / 8.0 * TOP_MARGIN_SCREEN_FRACTION * canvas.getHeight()),
                RULER_PAINT);
        canvas.drawText(Double.toString(meterWidth) + " m",
                (float) ((canvas.getWidth() - RULER_PAINT.measureText(Double.toString(meterWidth) + " m")) / 2.0),
                (float) ((TOP_MARGIN_SCREEN_FRACTION - 0.025) * canvas.getHeight()), RULER_PAINT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public enum Axis {
        X,
        Y
    }

    public interface NodePositionChoiceListener {
        void onNodePositionChosen(double internalX, double internalY, double scale);
    }

    public interface StructureProvider {
        Structure getStructure();
    }

    private static class InternalColorStop implements Comparable {
        int color;
        double pos;

        InternalColorStop(int color, double pos) {
            this.color = color;
            this.pos = pos;
        }


        @Override
        public int compareTo(Object o) {
            return Double.compare(pos, ((InternalColorStop) o).pos);
        }
    }

    public class CanvasViewLongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            if (nodePositionChoiceListener != null) {
                super.onLongPress(e);
                float internalX = screenToInternal(e.getX(), Axis.X);
                float internalY = screenToInternal(e.getY(), Axis.Y);
                nodePositionChoiceListener.onNodePositionChosen(internalX, internalY, modelScaling);
            }
        }
    }
}