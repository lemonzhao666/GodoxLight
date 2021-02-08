package com.godox.light.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.godox.light.R;

public class LightControl extends FrameLayout {
    private final String TAG = "LightControl";

    private VScrollView scrollView;
    private LightView lightview;
    private OnLightChange onLightChange;
    private int scrollYY;
    private boolean isScroll;

    public LightControl(Context context) {
        this(context, null);
    }

    public LightControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.light_wheel, this);
        scrollView = view.findViewById(R.id.scrollview);
        lightview = view.findViewById(R.id.lightview);
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {
        scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollYY = scrollY;
                if (scrollYY % 100 <= 50 && isScroll) {
                    if (onLightChange != null)
                        onLightChange.change((scrollYY / 100) * 100 + 3300);
                } else {
                    if (onLightChange != null) {
                        onLightChange.change(((scrollYY / 100) + 1) * 100 + 3300);
                    }
                }
            }
        });
        scrollView.setOnSCrollEndListener(new VScrollView.OnScrollEndListener() {
            @Override
            public void endStroll() {
                int verb = scrollYY % 100;
                if (verb != 0) {
                    if (verb > 50) {
                        scrollView.scrollBy(0, 100 - verb);
                    } else {
                        scrollView.scrollBy(0, -verb);
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

    public interface OnLightChange {
        void change(int value);
    }

    public void setOnLightChangeListener(OnLightChange onLightChange) {
        this.onLightChange = onLightChange;
    }
}
