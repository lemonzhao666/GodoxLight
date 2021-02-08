package com.godox.light.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.godox.light.R;

public class FocusView extends FrameLayout {

    private View veiw;
    private RectView rectView;
    private VerticalSeekBar exposure_seek;
    private OnExposureChangeListener onExposureChangeListener;
    private int min;
    private FrameLayout flLight;
    private ImageView ivLight;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        veiw = View.inflate(context, R.layout.view_focus, this);
        rectView = veiw.findViewById(R.id.rectview);
        exposure_seek = veiw.findViewById(R.id.exposure_seek);
        flLight = veiw.findViewById(R.id.fl_light);
        ivLight = veiw.findViewById(R.id.iv_light);
        exposure_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (onExposureChangeListener != null) {
                    onExposureChangeListener.onChange(progress - seekBar.getMax() / 2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void setScope(int min, int max) {
        this.min = min;
        exposure_seek.setMax(max - min);
    }
    public void initValue(){
        exposure_seek.setProgress(exposure_seek.getMax()/2);
        exposure_seek.clearFocus();
    }
    public void setExposureLeft() {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) flLight.getLayoutParams();
        layoutParams.leftMargin = 0;
        FrameLayout.LayoutParams rLayoutParams = (LayoutParams) rectView.getLayoutParams();
        rLayoutParams.leftMargin = ConvertUtils.dp2px(42);
    }

    public void setExposureRight() {
        FrameLayout.LayoutParams rLayoutParams = (LayoutParams) rectView.getLayoutParams();
        rLayoutParams.leftMargin = 0;
        FrameLayout.LayoutParams layoutParams = (LayoutParams) flLight.getLayoutParams();
        layoutParams.leftMargin = ConvertUtils.dp2px(100);
    }

    public void setOnExposureChangeListener(OnExposureChangeListener onLightChangeListener) {
        this.onExposureChangeListener = onLightChangeListener;
    }

    public interface OnExposureChangeListener {
        void onChange(int value);
    }

    public int[] getRectViewWH() {
        int[] arr = new int[2];
        arr[0] = rectView.getWidth();
        arr[1] = rectView.getHeight();
        return arr;
    }
    public void showEposureSeek(){
        exposure_seek.setVisibility(VISIBLE);
        ivLight.setVisibility(GONE);
        flLight.setVisibility(VISIBLE);
    }

    public void hideEposureSeek() {
        exposure_seek.setVisibility(GONE);
        ivLight.setVisibility(VISIBLE);
        flLight.setVisibility(GONE);
    }

}
