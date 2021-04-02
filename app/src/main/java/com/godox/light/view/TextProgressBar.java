package com.godox.light.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.godox.light.R;


/**
 * 带文本提示的进度条
 */
public class TextProgressBar extends ProgressBar {
    private String text;
    private Paint mPaint;

    public TextProgressBar(Context context) {
        super(context);
        initText();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    @Override
    public void setProgress(int progress) {
        setTextProgress(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }

    // 初始化，画笔
    private void initText() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(getResources().getColor(R.color.black));
        this.mPaint.setTextSize(40f);

    }

    // 设置文字内容
    private void setTextProgress(int progress) {
        int i = (int) ((progress * 1.0f / this.getMax()) * 100);
        this.text = String.valueOf(i) + "%";
    }

    public void setTextValue(String text){
        this.text = text;
        this.invalidate();
    }

}
