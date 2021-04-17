package com.godox.light.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * 倒计时viewß
 */
public class CountdownView extends View {

    private static final String TAG = CountdownView.class.getSimpleName();

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 宽
     */
    private float mWith;

    /**
     * 高
     */
    private float mHeight;

    /**
     * 展示的数字
     */
    private int showNumber;
    private RelativeLayout framControl;

    /**
     * 动画
     */
    Animator mAnimator;

    /**
     * 倒计时结束回调ß
     */
    private CountDownViewEndListener listener;

    public interface CountDownViewEndListener {
        void countDownEnd();
    }

    public void setListener(CountDownViewEndListener listener) {
        this.listener = listener;
    }

    public CountdownView(Context context) {
        super(context);
        init();
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWith = w;
        mHeight = h;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        drawNumber(canvas);
        canvas.restore();
    }

    /**
     * 画数字
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        //画倒计时数字
        float textSize = mWith * 0.15f;
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
        float textX = mWith / 2;
        float textY = mHeight / 2 - (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.drawText(Integer.toString(showNumber), textX, textY, mPaint);
    }

    public int getShowNumber() {
        return showNumber;
    }

    public void setShowNumber(int showNumber) {
        this.showNumber = showNumber;
        this.invalidate();
    }

    public CountdownView getSelf() {
        return this;
    }

    /**
     * 开始倒计时
     *
     * @param showNumber
     */
    public void start(int showNumber) {
        this.showNumber = showNumber;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        this.setVisibility(VISIBLE);
        mAnimator = prepareAnimator();
        mAnimator.start();
    }

    public void start(int showNumber, CountDownViewEndListener end) {
        this.listener = end;
        start(showNumber);
    }

    /**
     * 停止
     */
    public void stop() {
        listener = null;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    /**
     * 动画定义
     *
     * @return
     */
    private Animator prepareAnimator() {
        AnimatorSet animation = new AnimatorSet();
        // 进度条动画
        ObjectAnimator showNumberAnimator = ObjectAnimator.ofInt(this, "showNumber"
                , showNumber, 0);
        showNumberAnimator.setDuration(showNumber * 1000);
        showNumberAnimator.setInterpolator(new LinearInterpolator());
        showNumberAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.countDownEnd();
                getSelf().setVisibility(View.GONE);
                framControl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                getSelf().setVisibility(View.GONE);
            }
        });
        animation.play(showNumberAnimator);
        return animation;
    }
    public void setFramControl(RelativeLayout framControl){
        this.framControl = framControl;
    }


}
