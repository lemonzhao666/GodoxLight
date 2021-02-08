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

            }
        });
    }

    public void setOnCloseControlLayoutListener(OnCloseControlLayoutListener onCloseControlLayoutListener) {
        this.onCloseControlLayoutListener = onCloseControlLayoutListener;
    }

    public interface OnCloseControlLayoutListener {
        void closeControlLayout();
    }
}
