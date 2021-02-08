package com.godox.light.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.godox.light.R;

public class LightControlLayout extends LinearLayout {

    private TextView tvTempera;
    private LightControl lightControl;
    private TextView tvClose;
    private OnCloseControlLayoutListener onCloseControlLayoutListener;
    private OnDMChangeListener OnDMChangeListener;
    private OnCCTChangeListener OnCCTChangeListener;
    private VerticalSeekBar verticalSeekBar;
    private TextView tvDM;

    public LightControlLayout(Context context) {
        this(context, null);
    }

    public LightControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.light_layout, this);
        tvTempera = view.findViewById(R.id.temperature);
        tvDM = view.findViewById(R.id.tv_dm);
        lightControl = view.findViewById(R.id.lightcontrol);
        tvClose = view.findViewById(R.id.close_btn);
        verticalSeekBar = view.findViewById(R.id.vseekbar);
        init();
    }

    private void init() {
        lightControl.setOnLightChangeListener(new LightControl.OnLightChange() {
            @Override
            public void change(int value) {
                tvTempera.setText(value + "K");
                if (null != OnCCTChangeListener) {
                    OnCCTChangeListener.cctChange(value);
                }
            }
        });
        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCloseControlLayoutListener)
                    onCloseControlLayoutListener.closeControlLayout();
            }
        });
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null != OnDMChangeListener) {
                    OnDMChangeListener.dmChange(progress);
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

    public void setOnCloseControlLayoutListener(OnCloseControlLayoutListener onCloseControlLayoutListener) {
        this.onCloseControlLayoutListener = onCloseControlLayoutListener;
    }

    public interface OnCloseControlLayoutListener {
        void closeControlLayout();
    }

    public void setOnDMChangeListener(OnDMChangeListener onDMChangeListener) {
        this.OnDMChangeListener = onDMChangeListener;
    }

    public void setOnCCTChangeListener(OnCCTChangeListener onCCTChangeListener) {
        this.OnCCTChangeListener = onCCTChangeListener;
    }

    public interface OnDMChangeListener {
        void dmChange(int value);
    }

    public interface OnCCTChangeListener {
        void cctChange(int value);
    }
}
