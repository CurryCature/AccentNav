package com.example.accucent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RingFillView extends View {

    private Paint paint;
    private RectF rectF;
    private float sweepAngle;

    public RingFillView(Context context) {
        super(context);
        init();
    }

    public RingFillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingFillView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20); // Adjust the stroke width as needed
        paint.setColor(Color.parseColor("#912EAC")); // Set the initial color

        rectF = new RectF();
        sweepAngle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        rectF.set(10, 10, width - 10, height - 10);

        canvas.drawArc(rectF, -90, sweepAngle, false, paint);
    }

    public void setSweepAngle(float angle) {
        sweepAngle = angle;
        invalidate();
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }
}

