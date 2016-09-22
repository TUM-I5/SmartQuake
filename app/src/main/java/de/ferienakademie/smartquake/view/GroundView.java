package de.ferienakademie.smartquake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yuriy on 21/09/16.
 */
class GroundView extends View {

    public GroundView(Context context) {
        super(context);
    }

    public GroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.FILL_AND_STROKE);
        red.setAntiAlias(true);

        canvas.drawPaint(red);
    }
}
