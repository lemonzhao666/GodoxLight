package com.zlm.base;
import android.os.Bundle;
import android.view.View;

interface IBaseView {
    void initData(final Bundle bundle);

    int bindLayout();

    void initView(Bundle savedInstanceState, View view);

    void doBusiness();

    void initListener();
}
