package com.godox.light.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author yink
 * 画线自定义view，
 */
public class GridLineView extends View {
    private Paint mPaint;
    // 自定义view的宽和高
    private int mWith;
    private int mHeight;
    private int type;

    public GridLineView(Context context) {
        super(context);
    }

    public GridLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWith, mHeight);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        if (type == 1) {
            drawSquare(canvas);
        } else if (type == 2) {
            drawSquare(canvas);
            drawDiagonal(canvas);
        } else if (type == 3) {
            drawPoint(canvas);
        }
        canvas.restore();
    }

    /**
     * 方框线
     *
     * @param canvas
     */
    private void drawSquare(Canvas canvas) {
        init();
        canvas.drawLine(0, mHeight / 3, mWith, mHeight / 3, mPaint);
        canvas.drawLine(0, 2 * mHeight / 3, mWith, 2 * mHeight / 3, mPaint);
        canvas.drawLine(mWith / 3, 0, mWith / 3, mHeight, mPaint);
        canvas.drawLine(2 * mWith / 3, 0, 2 * mWith / 3, mHeight, mPaint);

    }

    /**
     * 对角线
     *
     * @param canvas
     */
    private void drawDiagonal(Canvas canvas) {
        init();
        canvas.drawLine(0, 0, mWith, mHeight, mPaint);
        canvas.drawLine(0, mHeight, mWith, 0, mPaint);
    }

    /**
     * 画中心点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        init();
        canvas.drawCircle(mWith / 2, mHeight / 2, 10, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWith / 2, mHeight / 2, 80, mPaint);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth((float) 2.0);
    }

    public void setWh(int w, int h, int type) {
        this.mWith = w;
        this.mHeight = h;
        this.type = type;
        requestLayout();
    }
}
