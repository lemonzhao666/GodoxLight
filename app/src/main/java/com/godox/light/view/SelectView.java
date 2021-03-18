package com.godox.light.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.godox.light.R;

public class SelectView extends FrameLayout {
    private static final String TAG = "WheelView";
    private HScrollView scrollView;
    private NounView nounView;
    private int scrollXX;
    private OnWheelViewChange onWheelViewChange;
    private int perNounValue;
    private int nounTotalValue;
    private boolean isScroll;
    boolean canTouch;
    public boolean isScroll() {
        return isScroll;
    }

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.view_wheel, this);
        scrollView = view.findViewById(R.id.scrollview);
        nounView = view.findViewById(R.id.nounView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollXX = scrollX;
                int childNounSpace = nounView.getChildNounSpace();
                if (onWheelViewChange != null && isScroll) {
                    if (scrollX % childNounSpace <= nounView.getChildNounSpace() / 2) {
                        onWheelViewChange.change((scrollX / childNounSpace) * perNounValue);
                    } else {
                        onWheelViewChange.change((scrollX / childNounSpace + 1) * perNounValue);
                    }
                }
            }
        });
        scrollView.setOnSCrollEndListener(new HScrollView.OnScrollEndListener() {
            @Override
            public void endStroll() {
                int verb = scrollXX % nounView.getChildNounSpace();
                if (verb != 0) {
                    if (verb > nounView.getChildNounSpace() / 2) {
                        scrollView.scrollBy(nounView.getChildNounSpace() - verb, 0);
                    } else {
                        scrollView.scrollBy(-verb, 0);
                    }
                }
                isScroll = false;
            }

            @Override
            public void startStroll() {
                isScroll = true;
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        init();
    }

    public  interface OnWheelViewChange {
        void change(int value);
    }

    public void setOnWheelViewChange(OnWheelViewChange onWheelViewChange) {
        this.onWheelViewChange = onWheelViewChange;
    }

    public void setNounTotalValue(int nounTotalValue) {
        this.nounTotalValue = nounTotalValue;
        this.perNounValue = nounTotalValue / nounView.getChildNounTotalCount();
    }

    public int getNounTotalValue() {
        return nounTotalValue;
    }

    public void setCurrentNounValue(int currentNounValue) {
        float scroll = currentNounValue * (nounView.getNoPaddingWidth() / Float.valueOf(nounTotalValue));
        LogUtils.dTag(TAG, "scroll = " + scroll);
        scrollView.smoothScrollTo((int) scroll, 0);
    }

    public void setCanTouchEvent(boolean canTouch){
        this.canTouch = canTouch;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(canTouch){
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }
}
