package com.godox.light.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.godox.light.R;


public class CheckView extends LinearLayout {
    private TextView tvView1;
    private TextView tvView2;
    private OnSelectedClickListener mOnSelectedClickListener;

    public CheckView(Context context) {
        super(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = View.inflate(context, R.layout.view_select, this);
        tvView1 = view.findViewById(R.id.tv_view_1);
        tvView2 = view.findViewById(R.id.tv_view_2);
        tvView1.setTextColor(getResources().getColor(R.color.black));
        tvView1.setBackgroundColor(getResources().getColor(R.color.white));
        tvView2.setBackgroundColor(getResources().getColor(R.color.translucent_22));
        tvView2.setTextColor(getResources().getColor(android.R.color.white));
        tvView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvView1.setTextColor(getResources().getColor(R.color.black));
                tvView1.setBackgroundColor(getResources().getColor(R.color.white));
                tvView2.setBackgroundColor(getResources().getColor(R.color.translucent_22));
                tvView2.setTextColor(getResources().getColor(android.R.color.white));
                if (mOnSelectedClickListener != null) {
                    mOnSelectedClickListener.onFristClick();
                }
            }
        });
        tvView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvView1.setBackgroundColor(getResources().getColor(R.color.translucent_22));
                tvView1.setTextColor(getResources().getColor(android.R.color.white));
                tvView2.setTextColor(getResources().getColor(R.color.black));
                tvView2.setBackgroundColor(getResources().getColor(R.color.white));
                if (mOnSelectedClickListener != null) {
                    mOnSelectedClickListener.onSecondClick();
                }
            }
        });
    }


    public void setOnSelectedClickListener(OnSelectedClickListener mOnSelectedClickListener) {
        this.mOnSelectedClickListener = mOnSelectedClickListener;
    }

    public interface OnSelectedClickListener {
        void onFristClick();
        void onSecondClick();
    }

    public void setTitle(String title1, String title2) {
        tvView1.setText(title1);
        tvView2.setText(title2);
    }
    public void checkManual(){
        tvView1.setBackgroundColor(getResources().getColor(R.color.translucent_22));
        tvView1.setTextColor(getResources().getColor(android.R.color.white));
        tvView2.setTextColor(getResources().getColor(R.color.black));
        tvView2.setBackgroundColor(getResources().getColor(R.color.white));
    }
}
