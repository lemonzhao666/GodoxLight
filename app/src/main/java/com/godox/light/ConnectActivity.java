package com.godox.light;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.telink.ble.mesh.foundation.EventListener;
import com.zlm.base.BaseBackActivity;

public class ConnectActivity extends BaseBackActivity implements EventListener<String> {

    private FragmentManager supportFragmentManager;

    @Override
    public int bindLayout() {
        return R.layout.activity_connect;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        tvTitle.setVisibility(View.GONE);
        setReturnIcon(R.mipmap.left);
        setRightIcon(R.mipmap.flush);
    }

    @Override
    public void doBusiness() {
        radioGroup.setVisibility(View.VISIBLE);
        supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_container, new AddDeviceFragment());
        ivRight.setVisibility(View.INVISIBLE);
        fragmentTransaction.commit();
    }

    @Override
    public void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                if (checkedId == R.id.rbtn_connected) {
                    fragmentTransaction.replace(R.id.fl_container, new AddDeviceFragment());
                    ivRight.setVisibility(View.INVISIBLE);
                } else {
                    fragmentTransaction.replace(R.id.fl_container, new UnAddDeviceFragment());
                    ivRight.setVisibility(View.VISIBLE);
                }
                fragmentTransaction.commit();
            }
        });
    }
}

