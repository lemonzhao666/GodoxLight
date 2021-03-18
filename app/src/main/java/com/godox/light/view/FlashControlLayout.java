package com.godox.light.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.godox.light.R;

public class FlashControlLayout extends LinearLayout {

    private TextView tvTempera;
    private LightControl lightControl;
    private TextView tvClose;
    private OnCloseControlLayoutListener onCloseControlLayoutListener;
    private OnCCTChangeListener OnCCTChangeListener;
    private OnEVChangeListener onEVChangeListener;
    private RadioGroup rgEV;

    public FlashControlLayout(Context context) {
        this(context, null);
    }

    public FlashControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.flash_layout, this);
        tvTempera = view.findViewById(R.id.temperature);
        lightControl = view.findViewById(R.id.lightcontrol);
        tvClose = view.findViewById(R.id.close_btn);
        rgEV = findViewById(R.id.rg_ev);
        init();
    }

    private void init() {
        lightControl.setOnLightChangeListener(new LightControl.OnLightChange() {
            @Override
            public void change(int value) {
                tvTempera.setText(value + "K");
                OnCCTChangeListener.cctChange(value);
            }
        });
        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCloseControlLayoutListener)
                    onCloseControlLayoutListener.closeControlLayout();
            }
        });
        rgEV.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_1:
                        onEVChangeListener.evChange(2f);
                        break;
                    case R.id.rbtn_2:
                        onEVChangeListener.evChange(1.5f);
                        break;
                    case R.id.rbtn_3:
                        onEVChangeListener.evChange(1f);
                        break;
                    case R.id.rbtn_4:
                        onEVChangeListener.evChange(0.5f);
                        break;
                    case R.id.rbtn_5:
                        onEVChangeListener.evChange(0);
                        break;
                    case R.id.rbtn_6:
                        onEVChangeListener.evChange(-0.5f);
                        break;
                    case R.id.rbtn_7:
                        onEVChangeListener.evChange(-1f);
                        break;
                    case R.id.rbtn_8:
                        onEVChangeListener.evChange(-1.5f);
                        break;
                    case R.id.rbtn_9:
                        onEVChangeListener.evChange(-2f);
                        break;
                }
            }
        });
    }

    public void setOnCloseControlLayoutListener(OnCloseControlLayoutListener onCloseControlLayoutListener) {
        this.onCloseControlLayoutListener = onCloseControlLayoutListener;
    }

    public interface OnCloseControlLayoutListener {
        void closeControlLayout();
    }

    public void setOnCCTChangeListener(OnCCTChangeListener onCCTChangeListener) {
        this.OnCCTChangeListener = onCCTChangeListener;
    }

    public void setOnEVChangeListener(OnEVChangeListener onEVChangeListener) {
        this.onEVChangeListener = onEVChangeListener;
    }

    public interface OnCCTChangeListener {
        void cctChange(int value);
    }

    public interface OnEVChangeListener {
        void evChange(float value);
    }
}
