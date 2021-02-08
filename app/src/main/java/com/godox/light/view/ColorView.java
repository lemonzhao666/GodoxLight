package com.godox.light.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.godox.light.R;

import java.util.HashMap;

public class ColorView extends View {
    private Paint paint;
    private float childNounW;
    private float childNounH;
    private int childNounSpace;
    private int childNounTotalCount;
    private HashMap<Integer, short[]> hashMap;
    short r = 255;
    short g = 254;
    short b = 250;

    short firstR;
    short firstG;
    short firstB ;


    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorView);
        childNounW = typedArray.getDimension(R.styleable.ColorView_childW, 14);
        childNounH = typedArray.getDimension(R.styleable.ColorView_childH, 14);
        childNounW = ConvertUtils.dp2px(14);
        childNounH = ConvertUtils.dp2px(14);
        childNounSpace = typedArray.getInteger(R.styleable.ColorView_childSpace, 1);
        childNounTotalCount = typedArray.getInteger(R.styleable.ColorView_childTotalCount, 1);
        typedArray.recycle();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        hashMap = new HashMap<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(childNounSpace * (childNounTotalCount - 1) + ScreenUtils.getScreenWidth(), (int) childNounH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        g -= (childNounTotalCount / 2) * 6;
        b -= (childNounTotalCount / 2) * 25;
        firstR = r;
        firstG = g;
        firstB = b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        hashMap.clear();
        for (int i = 1; i <= childNounTotalCount; i++) {
            if (i <= childNounTotalCount / 2) {
                r = 255;
                g += 6;
                b += 25;
            } else {
                r -= 29;
                g -= 9;
                b = 255;
            }
            paint.setColor(Color.argb(255, r, g, b));
            short[] value = new short[]{r, g, b};
            hashMap.put(i - 1, value);
            canvas.drawRect(ScreenUtils.getScreenWidth() / 2 - childNounW / 2 + childNounSpace * (i - 1), 0,
                    ScreenUtils.getScreenWidth() / 2 + childNounW / 2 + childNounSpace * (i - 1), childNounH, paint);
        }
        r = firstR;
        g = firstG;
        b = firstB;
    }

    public int getChildNounSpace() {
        return childNounSpace;
    }

    public short[] getColor(int index) {
        return hashMap.get(index);
    }
}