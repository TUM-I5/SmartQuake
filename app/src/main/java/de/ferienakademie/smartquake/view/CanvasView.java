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
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by yuriy on 19/09/16.
 */
public class CanvasView extends View {

    private Structure s = new Structure();
    private Paint p = new Paint();

    public CanvasView(Context context) {
        super(context);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
    }

    public void addJoint(Beam beam) {
        s.addBeam(beam);
    }

    public void forceRedraw() {
        invalidate();
    }

    public void emptyJoints() {
        s.clearAll();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Beam beam : s.getBeams()) {
            DrawHelper.drawBeam(beam, canvas, p);
        }
    }
}
