package com.godox.light.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.godox.light.R;

public class RectView extends View {

    private Paint paint;
    private int w;
    private int h;

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.theme));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, w, h, paint);
        canvas.drawRect(0, 0, w, h, paint);
        canvas.drawLine(w / 2, 0, w / 2, 15f, paint);
        canvas.drawLine(w / 2, h - 15, w / 2, h, paint);
        canvas.drawLine(0, h / 2, 15, h / 2, paint);
        canvas.drawLine(w - 15, h / 2, w, h / 2, paint);
    }
}
