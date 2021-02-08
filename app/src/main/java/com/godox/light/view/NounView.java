package com.godox.light.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.godox.light.R;

public class NounView extends View {

    private Paint paint;
    private int childNounW;
    private int childNounH;
    private int childNounSpace;
    private int childNounSpaceCount;
    private int childNounTotalCount;
    private int NounViewwWidth;
    private int NounViewwHeight;
    private int childNounBorder;
    private int border;
    private static final String TAG = "NounView";

    public int getChildNounTotalCount() {
        return childNounTotalCount;
    }

    public NounView(Context context) {
        this(context, null);
    }

    public NounView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NounView);
        childNounW = typedArray.getInteger(R.styleable.NounView_childNounW, 1);
        childNounH = typedArray.getInteger(R.styleable.NounView_childNounH, 1);
        childNounSpace = typedArray.getInteger(R.styleable.NounView_childNounSpace, 1);
        childNounSpaceCount = typedArray.getInteger(R.styleable.NounView_childNounSpaceCount, 1);
        childNounTotalCount = typedArray.getInteger(R.styleable.NounView_childNounTotalCount, 1);
        childNounBorder = typedArray.getInteger(R.styleable.NounView_childNounBorder, 0);
        typedArray.recycle();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(childNounW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(childNounSpace * childNounTotalCount + ScreenUtils.getScreenWidth(), childNounH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        NounViewwWidth = w;
        NounViewwHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i <= childNounTotalCount; i++) {
            if (i % childNounSpaceCount != 0) {
                canvas.drawLine(ScreenUtils.getScreenWidth() / 2 + getPaddingLeft() + childNounSpace * i, childNounBorder, ScreenUtils.getScreenWidth() / 2 + getPaddingLeft() + childNounSpace * i, childNounH - childNounBorder, paint);
            } else {
                canvas.drawLine(ScreenUtils.getScreenWidth() / 2 + getPaddingLeft() + childNounSpace * i, 0, ScreenUtils.getScreenWidth() / 2 + getPaddingLeft() + childNounSpace * i, childNounH, paint);
            }

        }
    }

    public int getChildNounSpace() {
        return childNounSpace;
    }

    public int getNoPaddingWidth() {
        return NounViewwWidth - ScreenUtils.getScreenWidth();
    }
}
