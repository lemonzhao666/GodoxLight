package com.godox.light.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.godox.light.R;

public class ColorSelectView extends FrameLayout {

    private View view;
    private HScrollView hScrollView;
    private ColorView colorView;
    private int scrollXX;
    private OnWheelViewChange onWheelViewChange;
    private int nounTotalValue;
    private boolean isScroll = false;
    private static final String TAG = "ColorSelectView";
    private int index;

    public ColorSelectView(Context context) {
        this(context, null);
    }

    public ColorSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.view_color_select, this);
        hScrollView = view.findViewById(R.id.scrollview);
        colorView = view.findViewById(R.id.colorView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        hScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollXX = scrollX;
                int childNounSpace = colorView.getChildNounSpace();
                LogUtils.dTag(TAG, "isScroll = " + isScroll + " scrollX = " + scrollX + " childNounSpace = " + childNounSpace + " selectIndex = " + scrollX / childNounSpace);
                if (onWheelViewChange != null && isScroll) {
                    if (scrollX % childNounSpace <= colorView.getChildNounSpace() / 2) {
                        index = scrollX / childNounSpace;
                    } else {
                        index = scrollX / childNounSpace + 1;
                    }
                    onWheelViewChange.change(index);
                    LogUtils.dTag(TAG, "r = " + colorView.getColor(index)[0] + " g = " + colorView.getColor(index)[1] + " b = " + colorView.getColor(index)[2]);
                }
            }
        });

        hScrollView.setOnSCrollEndListener(new HScrollView.OnScrollEndListener() {
            @Override
            public void endStroll() {
                int verb = scrollXX % colorView.getChildNounSpace();
                LogUtils.dTag(TAG, "verb = " + verb);
                if (verb != 0) {
                    if (verb > colorView.getChildNounSpace() / 2) {
                        hScrollView.scrollBy(colorView.getChildNounSpace() - verb, 0);
                        index++;
                    } else {
                        hScrollView.scrollBy(-verb, 0);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        init();
    }

    public interface OnWheelViewChange {
        void change(int index);
    }

    public void setOnWheelViewChange(OnWheelViewChange onWheelViewChange) {
        this.onWheelViewChange = onWheelViewChange;
    }
//
//    void setNounTotalValue(int nounTotalValue) {
//        this.nounTotalValue = nounTotalValue;
//        this.perNounValue = nounTotalValue / nounView.getChildNounTotalCount();
//    }
//
//    public int getNounTotalValue() {
//        return nounTotalValue;
//    }
//
//    pub lic void setCurrentNounValue(int currentNounValue) {
//        float scroll = currentNounValue * (nounView.getNoPaddingWidth() / Float.valueOf(nounTotalValue));
//        LogUtils.dTag(TAG, "scroll = " + scroll);
//        hScrollView.smoothScrollTo((int) scroll, 0);
//    }
}
