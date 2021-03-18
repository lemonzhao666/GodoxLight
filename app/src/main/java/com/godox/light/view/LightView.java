package com.godox.light.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ConvertUtils;
import com.zlm.base.PublicUtil;

public class LightView extends View {

    private static final String TAG = "LightView";
    private Paint mPaint;
    int top = 0;
    int w = 0;

    public LightView(Context context) {
        this(context, null);
    }

    public LightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec) + ConvertUtils.dp2px(300) - 50);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        top = ConvertUtils.dp2px(150) - 25;
        for (int i = 0; i <= 37; i++) {
            if (i == 0) {
                top += 25;
            } else {
                top += 100;
            }
            int[] colors = PublicUtil.temperatureToColor(i * 100 + 2800);
            mPaint = new Paint();
            mPaint.setColor(Color.rgb(colors[0], colors[1], colors[2]));
            canvas.drawCircle(w / 2, top, 25, mPaint);
        }
    }
}
