package com.zlm.phototable;

public interface OnViewPagerListener {
    void onPageRelease(int i);

    void onPageSelected(int i, boolean z);
    void onChildViewAttachedToWindow();
}