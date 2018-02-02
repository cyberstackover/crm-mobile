package com.sinergiinformatika.sisicrm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {

    private int color = Color.RED, baseColor = Color.BLACK;
    private float strokeWidth = 5, baseStrokeWidth = 2;
    private int width = (int) (getWidth() - (strokeWidth / 2));
    private int baseWidth = (int) (getWidth() - strokeWidth);
    private float angleStart = -90;
    private float sweep = 0;

    private Path path;
    private Paint paint;
    private RectF oval, baseOval;

    public GaugeView(Context context) {
        super(context);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        path = new Path();
        paint = new Paint();
        oval = new RectF((strokeWidth / 2), (strokeWidth / 2), width, width);
        baseOval = new RectF(strokeWidth, strokeWidth, baseWidth, baseWidth);
    }

    public void setColor(int c) {
        color = c;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
    }

    public void setBaseWidth(int baseWidth) {
        this.baseWidth = baseWidth;
    }

    public void setAngleStart(float angleStart) {
        this.angleStart = angleStart;
    }

    public void setColorResId(int resId) {
        setColor(getResources().getColor(resId));
    }

    public void setStrokeWidth(float s) {
        strokeWidth = s;
        width -= (strokeWidth / 2);
        baseWidth = (int) (width - (strokeWidth / 2));
        oval = new RectF((strokeWidth / 2), (strokeWidth / 2), width, width);
        baseOval = new RectF(strokeWidth, strokeWidth, baseWidth, baseWidth);
    }

    public void setBaseStrokeWidth(float baseStrokeWidth) {
        this.baseStrokeWidth = baseStrokeWidth;
    }

    public float getSweep() {
        return sweep;
    }

    public void setSweep(float s) {
        sweep = s;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = (int) (w - (strokeWidth / 2));
        baseWidth = (int) (w - strokeWidth);
        oval = new RectF((strokeWidth / 2), (strokeWidth / 2), width, width);
        baseOval = new RectF(strokeWidth, strokeWidth, baseWidth, baseWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.addArc(oval, angleStart, sweep);

        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(color);

        canvas.drawPath(path, paint);

        paint.setColor(baseColor);
        paint.setStrokeWidth(baseStrokeWidth);

        canvas.drawArc(baseOval, angleStart, 360f, true, paint);
    }

}
