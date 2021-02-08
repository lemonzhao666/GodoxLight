package com.zlm.phototable;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener;

import com.blankj.utilcode.util.LogUtils;

public class MyLayoutManager extends LinearLayoutManager implements OnChildAttachStateChangeListener {
    private OnViewPagerListener onViewPagerListener;
    private PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
    private boolean isFrist = true;
    private boolean isTouch = false;
    private boolean hasAttach = false;
    private int attachPosition = 0;

    public MyLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        view.addOnChildAttachStateChangeListener(this);
        pagerSnapHelper.attachToRecyclerView(view);
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        hasAttach = true;
        attachPosition = getPosition(view);
        if (isFrist || !isTouch) {
            onViewPagerListener.onPageSelected(attachPosition, false);
            isFrist = false;
        } else {
            onViewPagerListener.onChildViewAttachedToWindow();
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        onViewPagerListener.onPageRelease(getPosition(view));
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                isTouch = true;
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                int position = getPosition(this.pagerSnapHelper.findSnapView(this));
                if (attachPosition == position&&hasAttach == true) {
                    if (position == 0 || position == getItemCount() - 1) {
                        onViewPagerListener.onPageSelected(position, true);
                    } else {
                        onViewPagerListener.onPageSelected(position, false);
                    }
                    isTouch = false;
                    hasAttach =false;
                }
                break;
        }

    }

    public void setOnViewPagerListener(OnViewPagerListener listener) {
        onViewPagerListener = listener;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }
}