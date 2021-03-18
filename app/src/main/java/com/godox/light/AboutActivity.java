package com.godox.light;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.zlm.base.BaseBackActivity;

import butterknife.Bind;

public class AboutActivity extends BaseBackActivity {

    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;

    @Override
    public int bindLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        tvVersion.setText(getString(R.string.version)+ AppUtils.getAppVersionName());
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {
        tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity,AgreementActivity.class));
            }
        });
    }


}
