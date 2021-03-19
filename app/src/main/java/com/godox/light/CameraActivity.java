package com.godox.light;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Range;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshConfiguration;
import com.telink.ble.mesh.foundation.MeshService;
import com.zlm.base.BaseActivity;
import com.zlm.base.PublicUtil;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.MeshInfo;
import com.zlm.base.model.NodeInfo;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CameraActivity extends BaseActivity implements EventListener<String> {

    private static final String TAG = "CameraActivity";
    private final int RC_PERMISSION = 0;

    private void requirePermission() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                ||!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ,Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, RC_PERMISSION);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
            File file = new File(Environment.getExternalStorageDirectory(), "GodoxLight");
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION && grantResults.length == 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
            File file = new File(Environment.getExternalStorageDirectory(), "GodoxLight");
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            new QMUIDialog.MessageDialogBuilder(this)
                    .setTitle(R.string.application_authority)
                    .setMessage(R.string.permission_need_set)
                    .setSkinManager(QMUISkinManager.defaultInstance(this))
                    .addAction(R.string.concel, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .addAction(0, getResources().getString(R.string.to_setting), QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            Intent mIntent = new Intent();
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            mIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(mIntent);
                        }
                    })
                    .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
        }
    }


    /**
     * 初始化相机
     */
    private void initCameraParams() {
        try {
            CameraParamList.getInstance().getCameraParamList().clear();
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String id : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(id);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                Integer supportedHardwareLevel = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                //获取摄像头的方向
                Integer sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                Size screenSize = new Size(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //获取支持的预览尺寸
                Size[] previewSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                //获取支持的拍摄图片尺寸
                Size[] pictureSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                //支持的光感度
                Range<Integer> sensitivityRange = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                //支持的图片曝光时间范围
                Range<Long> exposureTimeRange = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
                //曝光补偿范围
                Range<Integer> aeCompensationRange = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);

                Size pixelArraySize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                LogUtils.dTag(TAG, "pixelArraySizeW = " + pixelArraySize.getWidth() + "pixelArraySizeH = " + pixelArraySize.getHeight());
                int maxPreViewWidth = ScreenUtils.getScreenWidth();
                int maxPreViewHeight = ScreenUtils.getScreenHeight();
                CameraParam cameraParam = new CameraParam();
                cameraParam.setId(id);
                cameraParam.setFacing(facing);
                cameraParam.setSupportedHardwareLevel(supportedHardwareLevel);
                cameraParam.setSensorOrientation(sensorOrientation);
                cameraParam.setDeviceSize(screenSize);
                cameraParam.setPreviewSizes(previewSizes);
                cameraParam.setPictureSizes(pictureSizes);
                cameraParam.setSensitivityRange(sensitivityRange);
                cameraParam.setExposureTimeRange(exposureTimeRange);
                cameraParam.setAeCompensationRange(aeCompensationRange);
                cameraParam.setPixelArraySize(pixelArraySize);
                boolean has4To3PreviewSize = false;
                boolean has16To9PreviewSize = false;
                boolean has1To1PreviewSize = false;
                for (int i = 0; i < previewSizes.length; i++) {
                    int w = previewSizes[i].getWidth();
                    int h = previewSizes[i].getHeight();
                    if (previewSizes[i].getWidth() <= maxPreViewHeight && previewSizes[i].getHeight() <= maxPreViewWidth) {
                        if (w == h * 4 / 3 && !has4To3PreviewSize) {
                            cameraParam.setMax4To3PreviewSize(new Size(h, w));
                            has4To3PreviewSize = true;
                        } else if (w == h * 16 / 9 && !has16To9PreviewSize) {
                            cameraParam.setMax16To9PreviewSize(new Size(h, w));
                            has16To9PreviewSize = true;
                        } else if (w == h && !has1To1PreviewSize) {
                            cameraParam.setMax1To1PreviewSize(new Size(h, w));
                            has1To1PreviewSize = true;
                        }
                    }
                    continue;
                }
                boolean has4To3PicSize = false;
                boolean has16To9PicSize = false;
                boolean has1To1PicSize = false;
                for (int i = 0; i < pictureSizes.length; i++) {
                    int w = pictureSizes[i].getWidth();
                    int h = pictureSizes[i].getHeight();
                    if (w == h * 4 / 3 && !has4To3PicSize) {
                        cameraParam.setMax4To3PicSize(new Size(h, w));
                        has4To3PicSize = true;
                    } else if (w == h * 16 / 9 && !has16To9PicSize) {
                        cameraParam.setMax16To9PicSize(new Size(h, w));
                        has16To9PicSize = true;
                    } else if (w == h && !has1To1PicSize) {
                        cameraParam.setMax1To1PicSize(new Size(h, w));
                        has1To1PicSize = true;
                    }
                    continue;
                }
                LogUtils.dTag("CameraActivity", cameraParam.toString());
                LogUtils.dTag("CameraActivity", cameraParam.toSString());
                CameraParamList.getInstance().getCameraParamList().add(cameraParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BarUtils.setNavBarVisibility(getWindow(), false);
    }


    @Override
    public int bindLayout() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        BarUtils.setStatusBarVisibility(getWindow(), false);
        BarUtils.setNavBarVisibility(getWindow(), false);
        initCameraParams();
        requirePermission();
    }

    @Override
    public void doBusiness() {
        startMeshService();
        resetNodeState();
        String requestURL = BuildConfig.IS_PUBLIC ? BuildConfig.PUBLICK_CHECK_UPDATE_REQUESTURL : BuildConfig.PRIVATE_CHECK_UPDATE_REQUESTURL;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestURL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String versionInfo = response.body().string();
                        JSONObject versionInfoObject = null;
                        versionInfoObject = new JSONObject(versionInfo);
                        if (versionInfoObject != null) {
                            int versionCode = versionInfoObject.optInt("versionCode");
                            String versionName = versionInfoObject.optString("versionName");
                            String downloadUrl = versionInfoObject.optString("downloadUrl");
                            String descInfo = versionInfoObject.optString("descInfo");
                            if (!TextUtils.isEmpty(downloadUrl) || downloadUrl != null) {
                                LogUtils.dTag(TAG,"getAppVersionCode = "+PublicUtil.getAppVersionCode(mActivity)+" versionCode = "+versionCode);
                                if (AppUtils.getAppVersionCode() < versionCode) {
                                    showNoticeDialog(versionName, descInfo, downloadUrl);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void initListener() {

    }

    private void startMeshService() {
        MeshService.getInstance().init(this, TelinkMeshApplication.getInstance());
        MeshConfiguration meshConfiguration = TelinkMeshApplication.getInstance().getMeshInfo().convertToConfiguration();
        MeshService.getInstance().setupMeshNetwork(meshConfiguration);
        MeshService.getInstance().checkBluetoothState();
    }

    private void resetNodeState() {
        MeshInfo mesh = TelinkMeshApplication.getInstance().getMeshInfo();
        if (mesh.nodes != null) {
            for (NodeInfo deviceInfo : mesh.nodes) {
                deviceInfo.setOnOff(-1);
                deviceInfo.lum = 0;
                deviceInfo.temp = 0;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MeshService.getInstance().clear();
    }

    private void showNoticeDialog(final String version, String upgradeNote, final String spec) {
        final View view = LayoutInflater.from(mActivity).inflate(
                R.layout.view_updateinforshow_dialog, null);

        final TextView ok = view
                .findViewById(R.id.view_updateinforshow_dialog_ok);
        final TextView cancel = view
                .findViewById(R.id.view_updateinforshow_dialog_cancel);
        TextView infor = view
                .findViewById(R.id.view_updateinforshow_dialog_infor);
        TextView infor2 = view
                .findViewById(R.id.view_updateinforshow_dialog_infor2);

        String inforString = getString(R.string.version_update) + version;
        if (PublicUtil.Network.checkNetWorkType(mActivity) != PublicUtil.Network.WIFI) {
            inforString = inforString + getString(R.string.isupdate);
        }
        infor.setText(inforString);
        infor2.setText(upgradeNote);
        ok.setText(R.string.immediately_update);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity,
                        R.style.dialog_nostroke).show();

                if (alertDialog != null) {
                    alertDialog.addContentView(view, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    ok.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            DolwnLoadAppService.setContent(mActivity);
                            Intent intent = new Intent(mActivity, DolwnLoadAppService.class);
                            intent.putExtra("spec", spec);
                            intent.putExtra("newVerString", version);
                            startService(intent);
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}

