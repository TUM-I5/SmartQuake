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

/**
 * Created by yuriy on 19/09/16.
 */
public class CanvasView extends View {

    private List<Beam> beams = new ArrayList<>();

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

    public void addJoint(Beam beam) { beams.add(beam); }

    public void drawStructure() {
        invalidate();
    }

    public void emptyJoints() { beams = new ArrayList<>(); }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL_AND_STROKE);
        red.setAntiAlias(true);

        for (Beam beam : beams) {
            DrawHelper.drawBeam(beam, canvas, red);
        }
    }
}
