package com.godox.light;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.telink.ble.mesh.core.message.generic.VendorMessage;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.GattOtaEvent;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.zlm.base.BaseActivity;
import com.zlm.base.PublicUtil;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.UpdateInfo;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeviceOtaActivity extends BaseActivity implements EventListener<String> {

    @Override
    public int bindLayout() {
        return R.layout.activity_device_ota;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle("固件升级");
        TelinkMeshApplication.getInstance().addEventListener(VendorMessage.class.getName(), this);
    }

    @Override
    public void doBusiness() {
        String updateUrl = "http://www.egodox.com/apps/update/ios/rf_led_flash/info/testgdfirmwareversion.json";
        netService.getDeviceUpdateInfo(updateUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UpdateInfo>() {
                    @Override
                    public void accept(UpdateInfo updateInfo) throws Exception {
                        String btFirmwareVersion = updateInfo.getBtFirmwareVersion();
                        LogUtils.dTag(TAG, "btFirmwareVersion = " + btFirmwareVersion);
                        String btFirmwareUrl = updateInfo.getBtFirmwareUrl();
                    }
                });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LightControl.sendSearchDeviceVersionMessage(MeshNodeList.getInstance().getCurrentDeviceMesh());
            }
        },1000);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void performed(Event<String> event) {
        super.performed(event);
        switch (event.getType()) {
            case GattOtaEvent.EVENT_TYPE_OTA_SUCCESS:
                MeshService.getInstance().idle(false);
                break;
            case GattOtaEvent.EVENT_TYPE_OTA_FAIL:
                MeshService.getInstance().idle(true);
                break;
            case GattOtaEvent.EVENT_TYPE_OTA_PROGRESS:
                int progress = ((GattOtaEvent) event).getProgress();
                break;
        }

        if (event.getType().equals(VendorMessage.class.getName())) {
//            NotificationMessage notificationMessage = ((StatusNotificationEvent) event).getNotificationMessage();
            VendorMessage vendorMessage = (VendorMessage) ((StatusNotificationEvent) event).getNotificationMessage().getStatusMessage();
            byte[] dataArray = vendorMessage.getDataParam();
            LogUtils.dTag(TAG, PublicUtil.toHexString(dataArray));
//            String version = Integer.toHexString(dataArray[2])+""+Integer.toHexString(dataArray[3])+""+Integer.toHexString(dataArray[4]);
//            try {
//                mVersion = Integer.parseInt(version);
//            }catch (Exception e){
//                e.printStackTrace();
//                mVersion = 20;
//            }
//            tv_version_info.setText(getString(R.string.version, mVersion+""));
//            mDeviceOtapresenter.initDeviceFeature();
        }
    }
}
