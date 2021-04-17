package com.godox.light;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.godox.light.view.TextProgressBar;
import com.telink.ble.mesh.core.message.MeshStatus;
import com.telink.ble.mesh.core.message.generic.VendorMessage;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.GattOtaEvent;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.telink.ble.mesh.foundation.parameter.GattOtaParameters;
import com.zlm.base.BaseActivity;
import com.zlm.base.BaseBackActivity;
import com.zlm.base.DownloadListener;
import com.zlm.base.PublicUtil;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.UpdateInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceOtaActivity extends BaseBackActivity implements EventListener<String>, DownloadListener {

    private String btFirmwareUrl;
    private TextProgressBar textProgressBar;
    private byte[] mFirmware;
    private int newVersion;
    private int currentVersion;
    private TextView tvVersionInfo;
    private TextView newVersionInfo;

    @Override
    public int bindLayout() {
        return R.layout.activity_device_ota;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(getString(R.string.Firmware_update));
        textProgressBar = findViewById(R.id.btn_start_ota);
        tvVersionInfo = findViewById(R.id.tv_version_info);
        newVersionInfo = findViewById(R.id.tv_new_version_info);
        TelinkMeshApplication.getInstance().addEventListener(GattOtaEvent.EVENT_TYPE_OTA_SUCCESS, this);
        TelinkMeshApplication.getInstance().addEventListener(GattOtaEvent.EVENT_TYPE_OTA_PROGRESS, this);
        TelinkMeshApplication.getInstance().addEventListener(GattOtaEvent.EVENT_TYPE_OTA_FAIL, this);
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
                        newVersion = Integer.parseInt(btFirmwareVersion);
                        LogUtils.dTag(TAG, "newVersion = " + btFirmwareVersion);
                        btFirmwareUrl = updateInfo.getBtFirmwareUrl();
                    }
                });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LightControl.sendSearchDeviceVersionMessage(MeshNodeList.getInstance().getCurrentDeviceMesh());
            }
        }, 1000);

    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(VendorMessage.class.getName())) {
            VendorMessage vendorMessage = (VendorMessage) ((StatusNotificationEvent) event).getNotificationMessage().getStatusMessage();
            byte[] dataArray = vendorMessage.getDataParam();
            currentVersion = dataArray[4];
            tvVersionInfo.setText(getString(R.string.Device_firmware_version)+currentVersion);
            if (currentVersion < newVersion) {
                textProgressBar.setVisibility(View.VISIBLE);
                textProgressBar.setTextValue(getString(R.string.Start_the_upgrade));
                newVersionInfo.setVisibility(View.VISIBLE);
                newVersionInfo.setText(getString(R.string.new_version_tip)+newVersion);
            }
            LogUtils.dTag(TAG, "currentVersion  = " + PublicUtil.toHexString(dataArray));
        }
        switch (event.getType()) {
            case GattOtaEvent.EVENT_TYPE_OTA_SUCCESS:
                setTextProgressBar(getString(R.string.Upgrade_Complete),false);
                MeshService.getInstance().idle(false);
                break;
            case GattOtaEvent.EVENT_TYPE_OTA_FAIL:
                setTextProgressBar(getString(R.string.Upgrade_failed),true);
                MeshService.getInstance().idle(true);
                break;
            case GattOtaEvent.EVENT_TYPE_OTA_PROGRESS:
                int progress = ((GattOtaEvent) event).getProgress();
                setTextProgressBar(getString(R.string.Upgrade_progress) + progress + "%" ,false);
                textProgressBar.setProgress(progress);
                break;
        }
    }

    @Override
    public void initListener() {
        textProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netService.downloadDevcieBin(btFirmwareUrl).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String path = Environment.getExternalStorageDirectory() + "/GodoxLight/LK8620_mesh_GD.bin";
                        Thread mThread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                writeResponseToDisk(path, response, DeviceOtaActivity.this);
                            }
                        };
                        mThread.start();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        setTextProgressBar(getString(R.string.Download_failed) ,true);
                    }
                });
            }
        });
    }


    private static void writeResponseToDisk(String path, Response<ResponseBody> response, DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
    }

    private static int sBufferSize = 8192;

    private static void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        downloadListener.onStartDownLoad();
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }
        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            downloadListener.onFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStartDownLoad() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextProgressBar(getString(R.string.Start_downloading) ,false);
            }
        });

    }

    @Override
    public void onProgress(int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextProgressBar(getString(R.string.Downloading) + progress + "%",false);
                textProgressBar.setProgress(progress);
            }
        });

    }

    @Override
    public void onFinish(String path) {
        try {
            InputStream stream = new FileInputStream(new File(path));
            int length = stream.available();
            mFirmware = new byte[length];
            stream.read(mFirmware);
            stream.close();
            if (mFirmware == null) {
                toastMsg("select firmware!");
                return;
            }
            GattOtaParameters parameters = new GattOtaParameters(MeshNodeList.getInstance().getCurrentDeviceMesh(), mFirmware);
            MeshService.getInstance().startGattOta(parameters);
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTextProgressBar(getString(R.string.Upgrade_file_error),true);
                }
            });
        }
    }

    @Override
    public void onFail(String errorInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextProgressBar(getString(R.string.Download_failed),true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(this);
    }

    private void setTextProgressBar(String text, boolean isProgress) {
        textProgressBar.setTextValue(text);
        textProgressBar.setEnabled(isProgress);
    }

    static {
        MeshStatus.Container.register(0x0211F1, VendorMessage.class);
    }
}
