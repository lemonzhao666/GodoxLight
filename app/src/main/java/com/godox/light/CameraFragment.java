package com.godox.light;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Range;
import android.util.Size;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.godox.light.view.AutoFitTextureView;
import com.godox.light.view.CheckView;
import com.godox.light.view.ColorSelectView;
import com.godox.light.view.CountdownView;
import com.godox.light.view.FlashControlLayout;
import com.godox.light.view.FocusView;
import com.godox.light.view.GridLineView;
import com.godox.light.view.LightControlLayout;
import com.godox.light.view.SelectView;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.telink.ble.mesh.core.message.MeshStatus;
import com.telink.ble.mesh.core.message.generic.OnOffGetMessage;
import com.telink.ble.mesh.core.message.generic.VendorMessage;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.AutoConnectEvent;
import com.telink.ble.mesh.foundation.event.MeshEvent;
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent;
import com.telink.ble.mesh.foundation.parameter.AutoConnectParameters;
import com.zlm.base.BaseFragment;
import com.zlm.base.PublicUtil;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.AppSettings;
import com.zlm.base.model.NodeInfo;
import com.zlm.base.model.NodeStatusChangedEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CameraFragment extends BaseFragment implements EventListener<String> {

    private AutoFitTextureView textureView;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private ImageReader mImageSaveReader;
    private ImageReader mImagePreviewReader;
    private int mFacing = CameraCharacteristics.LENS_FACING_BACK;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewCaptuRerequestBuilder;
    private GestureListener gestureListener = new GestureListener();
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener();
    private CameraDeviceStateCallback mDeviceStateCallback = new CameraDeviceStateCallback();
    private CameraCaptureSessionStateCallback mCameraCaptureSessionStateCallback = new CameraCaptureSessionStateCallback();
    private CameraCaptureSessionCaptureCallback mCameraCaptureSessionCaptureCallback = new CameraCaptureSessionCaptureCallback();
    private String SAVEPATH = Environment.getExternalStorageDirectory().toString() + "/DCIM/GodoxLight";
    private File mFile;
    private Size mOutImageSize;
    private Size mPreviewSize;
    private int mState = STATE_PREVIEW;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_FOCUS_LOCK = 1;
    private static final int STATE_WAITING_EXPOSURE_PRECAPTURE = 2;
    private static final int STATE_WAITING_EXPOSURE_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private OrientationEventListener mOrientationListener;
    int orientation;
    int lastorientation;
    private Surface surface;
    private ArrayList<CameraParam> cameraParamList;
    private ImageButton ivSetting;
    private SelectView selectView1;
    private SelectView selectView2;

    private TextView tvIso1;
    private TextView tvIso2;
    private ImageButton ibtnTake;
    private ImageButton ibtnSwitch;
    private CameraParam cameraParam;
    private ImageButton ibtnResult;
    private int scale = 1;
    private int lens;
    private GridLineView gridView;
    private GestureDetector gestureDetector;
    private SPUtils spUtils;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private CountdownView countdownView;
    private CheckedTextView rbtnBoard;
    private int verb;
    private CheckedTextView rbtnIso;
    private LinearLayout llAE;
    private CheckedTextView rbtnTrade;
    private CheckedTextView checkedTextMe;
    private FrameLayout flControl;
    private FrameLayout flPreview;
    private FocusView focusView;
    private int textureViewW;
    private int textureViewH;
    private RadioGroup rgAWB;
    private ColorSelectView colorSelectView;
    private FrameLayout flAwb;
    private RadioButton rbAwb2;
    private LinearLayout llAWB;
    private CheckView checkView;
    private int iso;
    private Long exposure_time;
    private int colorTemp = 2500;
    private boolean isManualAe;
    private boolean isRegionAF;
    private byte awbType;
    private int ae_exposure_value;
    private Rect rect;
    private ImageButton add;
    private FlashControlLayout flashControlLayout;
    private LinearLayout llControl;
    private ImageButton ibtnFlash;
    private ImageButton ibtnLight;
    private LightControlLayout lightControlLayout;
    private Handler handler = new Handler();
    private FrameLayout rootLayout;
    private ArrayList<NodeInfo> nodeList;
    private int currentDeviceMesh = -1;
    private String currentDeviceName;
    private byte currentDIM;
    private byte currentCCT;
    private float currentEV;
    private boolean isSent = true;
    private boolean flash_open;
    private boolean light_open;
    private boolean isTakeComplete = true;
    private TextView tvDevice;
    private QMUIPopup mNormalPopup;
    private ConnectedNodeAdapter adapter;
    private View lightInfoView;
    private TextView tvEV;
    private TextView tvDIM;
    private TextView tvCCT;
    private boolean isPopupDismiss;
    private Timer timer;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadImage();
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
        startBackgroundThread();
        setUpCameraOutput();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        TelinkMeshApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        TelinkMeshApplication.getInstance().addEventListener(NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED, this);
        TelinkMeshApplication.getInstance().addEventListener(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);
        TelinkMeshApplication.getInstance().addEventListener(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);
        TelinkMeshApplication.getInstance().addEventListener(VendorMessage.class.getName(), this);
//        updataNodeInfo();
//        adapter.notifyDataSetChanged();
        MeshService.getInstance().autoConnect(new AutoConnectParameters());


//        flash_open = spUtils.getBoolean("flash_open", false);
//        light_open = spUtils.getBoolean("light_open", false);
//        if (flash_open) {
//            ibtnFlash.setSelected(true);
//        } else {
//            ibtnFlash.setSelected(false);
//        }
//        if (light_open) {
//            ibtnLight.setSelected(true);
//        } else {
//            ibtnLight.setSelected(false);
//        }
        if (nodeList.size() > 0) {

//            if (light_open) {
//                ibtnLight.setSelected(true);
//            } else {
//                ibtnLight.setSelected(false);
//            }
//            if (light_open) {
//                LightControl.sendLightStatusMessage(0, currentDeviceMesh);
//                handler.postDelayed(sentRunnable, 300);
//            } else {
//                LightControl.sendLightStatusMessage(1, currentDeviceMesh);
//            }
//            if (flash_open) {
//                LightControl.sendFlashStatusMessage(0, currentDeviceMesh);
//                handler.postDelayed(sentFlashRunnable, 300);
//            } else {
//                LightControl.sendFlashStatusMessage(1, currentDeviceMesh);
//            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void loadImage() {
        File[] images = new File(SAVEPATH).listFiles();
        if (images.length == 0) {
            ibtnResult.setImageResource(R.mipmap.image);
        } else if (images.length == 1) {
            Bitmap bitmap = BitmapFactory.decodeFile(images[0].getAbsolutePath());
            int pictureDegree = PublicUtil.readPictureDegree(images[0].getAbsolutePath());
            ibtnResult.setImageBitmap(PublicUtil.rotateBitmap(pictureDegree, bitmap));
        } else {
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < images.length - 1; i++) {
                        for (int j = 0; j < images.length - i - 1; j++) {
                            if (images[j].lastModified() < images[j + 1].lastModified()) {
                                File temp = images[j];
                                images[j] = images[j + 1];
                                images[j + 1] = temp;
                            }
                        }
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapFactory.decodeFile(images[0].getAbsolutePath());
                            ibtnResult.setImageBitmap(bitmap);
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    public void onPause() {
        TelinkMeshApplication.getInstance().removeEventListener(this);
        mOrientationListener.disable();
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == mFacing) {
                    mCameraId = cameraId;
                    break;
                } else {
                    continue;
                }
            }
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mDeviceStateCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageSaveReader) {
                mImageSaveReader.close();
                mImageSaveReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutput() {
        cameraParamList = CameraParamList.getInstance().getCameraParamList();
        if (mFacing == CameraCharacteristics.LENS_FACING_BACK) {
            cameraParam = cameraParamList.get(0);
        } else {
            cameraParam = cameraParamList.get(1);
        }
        scale = spUtils.getInt("scale", scale);
        lens = spUtils.getInt("lens", lens);
        if (scale == 0) { //4:3
            mPreviewSize = cameraParam.getMax4To3PreviewSize();
            mOutImageSize = cameraParam.getMax4To3PicSize();
        } else if (scale == 1) { //16:9
            mPreviewSize = cameraParam.getMax16To9PreviewSize();
            mOutImageSize = cameraParam.getMax16To9PicSize();
        } else if (scale == 2) { //1:1
            mPreviewSize = cameraParam.getMax1To1PreviewSize();
            mOutImageSize = cameraParam.getMax1To1PicSize();
        }
        textureViewW = (int) (mPreviewSize.getWidth() * (ScreenUtils.getScreenWidth() / ((float) mPreviewSize.getWidth())));
        textureViewH = (int) (mPreviewSize.getHeight() * (ScreenUtils.getScreenWidth() / ((float) mPreviewSize.getWidth())));

        textureView.setAspectRatio(textureViewW, textureViewH);
        configCameraUI(textureViewW, textureViewH);
        mImageSaveReader = ImageReader.newInstance(mOutImageSize.getHeight(), mOutImageSize.getWidth(), ImageFormat.JPEG, /*maxImages*/2);
        mImageSaveReader.setOnImageAvailableListener(mOnImageSaveAvailableListener, mBackgroundHandler);
        mImagePreviewReader = ImageReader.newInstance(mPreviewSize.getHeight(), mPreviewSize.getWidth(), ImageFormat.YUV_420_888, /*maxImages*/2);
        mImagePreviewReader.setOnImageAvailableListener(mOnImagePreviewAvailableListener, mBackgroundHandler);
    }

    private void configCameraUI(int w, int h) {
        switch (lens) {
            case 0:
                gridView.setWh(0, 0, 0);
                break;
            case 1:
                gridView.setWh(w, h, 1);
                break;
            case 2:
                gridView.setWh(w, h, 2);
                break;
            case 3:
                gridView.setWh(w, h, 3);
                break;
        }
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_camera;
    }

    @Override
    public void initView(Bundle savedInstanceState, final View view) {
        textureView = view.findViewById(R.id.textureview);
        ibtnTake = view.findViewById(R.id.ibtn_take);
        ibtnSwitch = view.findViewById(R.id.ibtn_switch);
        ivSetting = view.findViewById(R.id.iv_setting);
        ibtnResult = view.findViewById(R.id.ibtn_result);
        rbtnIso = view.findViewById(R.id.rbtn_iso);
        selectView1 = view.findViewById(R.id.selectview1);
        selectView2 = view.findViewById(R.id.selectview2);
        tvIso1 = view.findViewById(R.id.tv_Iso1);
        tvIso2 = view.findViewById(R.id.tv_iso2);
        gridView = view.findViewById(R.id.gridview);
        countdownView = view.findViewById(R.id.countdownview);
        rbtnBoard = view.findViewById(R.id.rbtn_board);
        rbtnTrade = view.findViewById(R.id.rbtn_trade);
        llAE = view.findViewById(R.id.ll_ae);
        checkedTextMe = view.findViewById(R.id.rbtn_me);
        flControl = view.findViewById(R.id.fl_control);
        flPreview = view.findViewById(R.id.fl_preview);
        rgAWB = view.findViewById(R.id.rg_awb);
        flAwb = view.findViewById(R.id.fl_awb);
        rbAwb2 = view.findViewById(R.id.rb_awb2);
        llAWB = view.findViewById(R.id.ll_awb);
        add = view.findViewById(R.id.add);
        ibtnFlash = view.findViewById(R.id.ibtn_flash);
        ibtnLight = view.findViewById(R.id.ibtn_light);
        rootLayout = view.findViewById(R.id.root_layout);
        lightInfoView = View.inflate(mContext, R.layout.laytout_light_info, null);
        tvEV = lightInfoView.findViewById(R.id.tv_ev);
        tvDIM = lightInfoView.findViewById(R.id.tv_dim);
        tvCCT = lightInfoView.findViewById(R.id.tv_cct);
        add = view.findViewById(R.id.add);
        flashControlLayout = view.findViewById(R.id.flashcontrollayout);
        lightControlLayout = view.findViewById(R.id.lightcontrollayout);
        llControl = view.findViewById(R.id.ll_control);
        colorSelectView = view.findViewById(R.id.colorselectview);
        checkView = view.findViewById(R.id.checkview);
        tvDevice = view.findViewById(R.id.tv_device);
        countdownView.setFramControl(flControl);
        focusView = new FocusView(mContext);
        focusView.setVisibility(View.GONE);
        flPreview.addView(focusView);
        checkView.setTitle("Auto", "Manual");
        rgAWB.check(R.id.rb_awb1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        updataNodeInfo();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doBusiness() {
        if (RomUtils.isHuawei()) {
            rbAwb2.setVisibility(View.GONE);
            flAwb.setVisibility(View.GONE);
        }
        spUtils = SPUtils.getInstance();
        verb = spUtils.getInt("verb", 0);
        switch (verb) {
            case 0:
                rbtnBoard.setBackgroundResource(R.mipmap.time0);
                break;
            case 3:
                rbtnBoard.setBackgroundResource(R.mipmap.time3);
                break;
            case 10:
                rbtnBoard.setBackgroundResource(R.mipmap.time10);
                break;
        }

        ArrayList<CameraParam> cameraParamList = CameraParamList.getInstance().getCameraParamList();
        if (mFacing == CameraCharacteristics.LENS_FACING_BACK) {
            cameraParam = cameraParamList.get(0);
        } else {
            cameraParam = cameraParamList.get(1);
        }
        setUpCameraOutput();
        File file = new File(Environment.getExternalStorageDirectory(), "GodoxLight");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFile = new File(SAVEPATH);
        if (!mFile.exists()) {
            mFile.mkdir();
        }
        mOrientationListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_FASTEST) {
            @Override
            public void onOrientationChanged(int orientationn) {
                orientation = orientationn;
                LogUtils.dTag(TAG, "RequestedOrientation = " + mActivity.getRequestedOrientation());
            }
        };
        Range<Integer> sensitivityRange = cameraParam.getSensitivityRange();
        selectView1.setNounTotalValue(sensitivityRange.getUpper() - sensitivityRange.getLower());
        selectView2.setNounTotalValue(1000);
        gestureDetector = new GestureDetector(getContext(), gestureListener);
        focusView.setScope(cameraParam.getAeCompensationRange().getLower(), cameraParam.getAeCompensationRange().getUpper());

        nodeList = new ArrayList<NodeInfo>();
        adapter = new ConnectedNodeAdapter(mContext, nodeList);
    }


    private void updataNodeInfo() {
        nodeList.clear();
        List<NodeInfo> nodes = TelinkMeshApplication.getInstance().getMeshInfo().nodes;
        for (NodeInfo nodeInfo : nodes) {
            if (nodeInfo.getOnOff() == 1) {
                String meshAdress = "0x" + Integer.toHexString(nodeInfo.meshAddress);
                if (meshAdress.length() == 3) {
                    meshAdress = "0x0" + Integer.toHexString(nodeInfo.meshAddress);
                }
                int len = nodeInfo.macAddress.length();
                String name = "GDFLASH_" + nodeInfo.macAddress.substring(len - 5, len - 3) + nodeInfo.macAddress.substring(len - 2, len);
                NodeInfo node = new NodeInfo();
                node.name = name;
                node.meshAddressHex = meshAdress;
                nodeList.add(node);
            }
        }


        if (nodeList.size() > 0) {
            for (int i = 0; i < nodeList.size(); i++) {
                NodeInfo nodeInfo = nodeList.get(i);
                if (nodeInfo.getName() != currentDeviceName || currentDeviceName == null) {
                    currentDeviceMesh = Integer.parseInt(nodeList.get(0).meshAddressHex.substring(2, 4), 16);
                    currentDeviceName = nodeList.get(0).getName();
                }
            }
            MeshNodeList.getInstance().setMeshNodeList(nodeList);
            MeshNodeList.getInstance().setCurrentDeviceMesh(currentDeviceMesh);
            tvDevice.setText(currentDeviceName);
            if(timer!=null){
                timer.cancel();
                timer =null;
            }
            timer = new Timer();
            timer.schedule(new timeTask(),500,1000);
        } else {
            tvDevice.setText("无设备");
            if(timer!=null){
                timer.cancel();
                timer =null;
            }

            MeshNodeList.getInstance().setCurrentDeviceMesh(-1);
        }
    }

    @Override
    public void initListener() {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int selectMesh = Integer.parseInt(nodeList.get(i).meshAddressHex.substring(2, 4), 16);
                currentDeviceMesh = selectMesh;
                currentDeviceName = nodeList.get(i).getName();
                tvDevice.setText(nodeList.get(i).getName());
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
                LightControl.sendSearchLightParamMessage(selectMesh);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ConvertUtils.dp2px(180)
                        , ConvertUtils.dp2px(60));
                layoutParams.gravity = Gravity.CENTER;
                lightInfoView.setLayoutParams(layoutParams);
                flControl.removeView(lightInfoView);
                flControl.addView(lightInfoView);
            }
        };

        tvDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nodeList.size() > 0) {
                    if (mNormalPopup == null || isPopupDismiss) {
                        mNormalPopup = QMUIPopups.listPopup(getContext(),
                                QMUIDisplayHelper.dp2px(getContext(), 100),
                                QMUIDisplayHelper.dp2px(getContext(), 110),
                                adapter,
                                onItemClickListener)
                                .bgColor(android.R.color.black)
                                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                                .radius(5)
                                .shadow(true)
                                .offsetYIfTop(QMUIDisplayHelper.dp2px(getContext(), 5))
                                .skinManager(QMUISkinManager.defaultInstance(getContext()))
                                .onDismiss(new PopupWindow.OnDismissListener() {
                                    @Override
                                    public void onDismiss() {
                                        isPopupDismiss = true;
                                        v.postDelayed(hideLightInfoRunnable, 2000);
                                    }
                                })
                                .show(v);
                    }
                }
            }
        });
        colorSelectView.setOnWheelViewChange(new ColorSelectView.OnWheelViewChange() {
            @Override
            public void change(int index) {
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                colorTemp = 2500 + index * 500;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, PublicUtil.colorTemperature(colorTemp));
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flashControlLayout.setVisibility(View.GONE);
                lightControlLayout.setVisibility(View.GONE);
                llControl.setVisibility(View.VISIBLE);
                return true;
            }
        });

        lightControlLayout.setOnDMChangeListener(new LightControlLayout.OnDMChangeListener() {
            @Override
            public void dmChange(int value) {
                currentDIM = (byte) value;
                if (isSent) {
                    handler.postDelayed(sentRunnable, 300);
                    isSent = false;
                }
            }
        });
        lightControlLayout.setOnCCTChangeListener(new LightControlLayout.OnCCTChangeListener() {
            @Override
            public void cctChange(int value) {
                currentCCT = (byte) (value / 100);
                if (isSent) {
                    handler.postDelayed(sentRunnable, 300);
                    isSent = false;
                }
            }
        });
        lightControlLayout.setOnCloseControlLayoutListener(new LightControlLayout.OnCloseControlLayoutListener() {
            @Override
            public void closeControlLayout() {
                lightControlLayout.setVisibility(View.GONE);
                ibtnLight.setSelected(false);
                llControl.setVisibility(View.VISIBLE);
                LightControl.sendLightStatusMessage(1, currentDeviceMesh);
                light_open = false;
                spUtils.put("light_open", false);
            }
        });
        flashControlLayout.setOnCCTChangeListener(new FlashControlLayout.OnCCTChangeListener() {
            @Override
            public void cctChange(int value) {
                currentCCT = (byte) (value / 100);
                spUtils.put("currentCCT", (int) currentCCT);
                if (isSent) {
                    handler.postDelayed(sentFlashRunnable, 300);
                    isSent = false;
                }
            }
        });
        flashControlLayout.setOnEVChangeListener(new FlashControlLayout.OnEVChangeListener() {
            @Override
            public void evChange(float value) {
                currentEV = value;
                spUtils.put("currentEV", currentEV);
                LightControl.sendFlashEvMeshMessage(LightControl.getEvsend(currentEV), currentCCT, currentDeviceMesh);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ConnectActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        ibtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {//开
                    LogUtils.dTag(TAG, "currentEV = " + currentEV + " currentCCT = " + currentCCT);
                    flashControlLayout.setEV(currentEV);
                    flashControlLayout.setCCT(currentCCT * 100);
                    flashControlLayout.setVisibility(View.VISIBLE);
                    llControl.setVisibility(View.GONE);
                    LightControl.sendFlashEvMeshMessage(LightControl.getEvsend(currentEV), currentCCT, currentDeviceMesh);
                } else { //打开
                    v.setSelected(true);
                    flash_open = true;
                    spUtils.put("flash_open", true);
                }
            }
        });

        ibtnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    lightControlLayout.setDIM(currentDIM);
                    lightControlLayout.setCCT(currentCCT * 100);
                    lightControlLayout.setVisibility(View.VISIBLE);
                    llControl.setVisibility(View.GONE);
                    LightControl.sendFlashEvMeshMessage(LightControl.getEvsend(currentEV), currentCCT, currentDeviceMesh);
                } else {
                    v.setSelected(true);
                    LightControl.sendLightStatusMessage(0, currentDeviceMesh);
                    light_open = true;
                    spUtils.put("light_open", true);
                }
            }
        });


        flashControlLayout.setOnCloseControlLayoutListener(new FlashControlLayout.OnCloseControlLayoutListener() {
            @Override
            public void closeControlLayout() {
                flashControlLayout.setVisibility(View.GONE);
                ibtnFlash.setSelected(false);
                llControl.setVisibility(View.VISIBLE);
                flash_open = false;
                spUtils.put("flash_open", false);
            }
        });

        checkView.setOnSelectedClickListener(new CheckView.OnSelectedClickListener() {
            @Override
            public void onFristClick() {
                isManualAe = false;
                selectView1.setCanTouchEvent(isManualAe);
                selectView2.setCanTouchEvent(isManualAe);
                focusView.showEposureSeek();
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSecondClick() {
                isManualAe = true;
                selectView1.setCanTouchEvent(isManualAe);
                selectView2.setCanTouchEvent(isManualAe);
                focusView.hideEposureSeek();
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_MODE_OFF);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
                selectView1.setCurrentNounValue(iso - cameraParam.getSensitivityRange().getLower());
                selectView2.setCurrentNounValue(getExposureTime(exposure_time));
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        rgAWB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setAwb(checkedId);
            }
        });
        final CountdownView.CountDownViewEndListener listener = new CountdownView.CountDownViewEndListener() {
            @Override
            public void countDownEnd() {
                lockFocus();
            }
        };
        rbtnIso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnIso.setChecked(!rbtnIso.isChecked());
                llAE.setVisibility(rbtnIso.isChecked() ? View.VISIBLE : View.GONE);
                llAWB.setVisibility(View.GONE);
                rbtnTrade.setChecked(false);
            }
        });
        rbtnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAwb(rgAWB.getCheckedRadioButtonId());
                rbtnTrade.setChecked(!rbtnTrade.isChecked());
                llAE.setVisibility(View.GONE);
                llAWB.setVisibility(rbtnTrade.isChecked() ? View.VISIBLE : View.GONE);
                rbtnIso.setChecked(false);
            }
        });
        checkedTextMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextMe.isChecked()) {
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
                } else {
                    if (!RomUtils.isHuawei()) {
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                        colorTemp = 3000;
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, PublicUtil.colorTemperature(colorTemp));
                    }
                }
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                checkedTextMe.setChecked(!checkedTextMe.isChecked());
            }
        });
        ibtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });
        ibtnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTakeComplete) {
                            return;
                        }
                        if (verb != 0) {
                            flControl.setVisibility(View.GONE);
                            countdownView.start(verb, listener);
                        } else {
                            isTakeComplete = false;
                            lockFocus();
                        }
                    }
                });
            }
        });


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                focusView.setVisibility(View.GONE);
            }
        };
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainHandler.removeCallbacks(runnable);
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mainHandler.postDelayed(runnable, 3000);
                }
                return true;
            }
        });

        focusView.setOnExposureChangeListener(new FocusView.OnExposureChangeListener() {
            @Override
            public void onChange(int value) {
                ae_exposure_value = value;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, value);
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        rbtnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (verb) {
                    case 0:
                        rbtnBoard.setBackgroundResource(R.mipmap.time3);
                        spUtils.put("verb", 3);
                        verb = 3;
                        break;
                    case 3:
                        rbtnBoard.setBackgroundResource(R.mipmap.time10);
                        spUtils.put("verb", 10);
                        verb = 10;
                        break;
                    case 10:
                        rbtnBoard.setBackgroundResource(R.mipmap.time0);
                        spUtils.put("verb", 0);
                        verb = 0;
                        break;
                }
            }
        });
        final View decorView = mActivity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        });
        selectView1.setOnWheelViewChange(new SelectView.OnWheelViewChange() {
            @Override
            public void change(int value) {
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                iso = value + cameraParam.getSensitivityRange().getLower();
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                isManualAe = true;
                focusView.hideEposureSeek();
//                checkView.checkManual();
                tvIso1.setText("ISO:" + (value + cameraParam.getSensitivityRange().getLower()));
            }
        });
        selectView2.setOnWheelViewChange(new SelectView.OnWheelViewChange() {
            @Override
            public void change(int value) {
                Range<Long> exposureTimeRange = cameraParam.getExposureTimeRange();
                Long min_exposure_time = exposureTimeRange.getLower();
                Long max_exposure_time = exposureTimeRange.getUpper() / 3;
                double frac = value / (double) selectView2.getNounTotalValue();
                exposure_time = (long) exponentialScaling(frac, min_exposure_time, max_exposure_time);
                if (exposure_time < min_exposure_time)
                    exposure_time = min_exposure_time;
                else if (exposure_time > max_exposure_time)
                    exposure_time = max_exposure_time;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
                LogUtils.dTag(TAG, "exposure_time = " + exposure_time);
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
//                checkView.checkManual();
                isManualAe = true;
                tvIso2.setText(getExposureTimeString(exposure_time));
            }
        });

        ibtnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, PhotoActivity.class));
            }
        });
    }

    private void setAwb(int checkedRadioButtonId) {
        switch (checkedRadioButtonId) {
            case R.id.rb_awb1:
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
                flAwb.setVisibility(View.GONE);
                awbType = 0;
                break;
            case R.id.rb_awb2:
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, PublicUtil.colorTemperature(colorTemp));
                awbType = 1;
                flAwb.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_awb3:
                awbType = 2;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);
                flAwb.setVisibility(View.GONE);
                break;
            case R.id.rb_awb4:
                awbType = 3;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
                flAwb.setVisibility(View.GONE);
                break;
            case R.id.rb_awb5:
                awbType = 4;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
                flAwb.setVisibility(View.GONE);
                break;
            case R.id.rb_awb6:
                awbType = 5;
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);
                flAwb.setVisibility(View.GONE);
                break;
        }
        try {
            mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private String getExposureTimeString(long exposure_time) {
        double exposure_time_s = exposure_time / 1000000000.0;
        String string;
        DecimalFormat decimalFormat = new DecimalFormat("#");
        if (exposure_time >= 500000000) {
            string = decimalFormat.format(exposure_time_s) + "s";
        } else {
            double exposure_time_r = 1.0 / exposure_time_s;
            string = " 1/" + decimalFormat.format(exposure_time_r) + "s";
        }
        return string;
    }

    private static double exponentialScaling(double frac, double min, double max) {
        double s = Math.log(max / min);
        return min * Math.exp(s * frac);
    }

    private static double exponentialScalingInverse(double value, double min, double max) {
        double s = Math.log(max / min);
        return Math.log(value / min) / s;
    }

    private void switchCamera() {

        if (mFacing == CameraCharacteristics.LENS_FACING_BACK) {
            mFacing = CameraCharacteristics.LENS_FACING_FRONT;
        } else {
            mFacing = CameraCharacteristics.LENS_FACING_BACK;
        }
        isRegionAF = false;
        isManualAe = false;
        awbType = 0;
        closeCamera();
        setUpCameraOutput();
        openCamera();
        rgAWB.check(R.id.rb_awb1);
    }

    private void captureStillPicture() {
        try {
            if (null == mActivity || null == mCameraDevice) {
                return;
            }
            CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            captureBuilder.addTarget(mImageSaveReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            if (isRegionAF) {
                captureBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
            }
            if (isManualAe) {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
            } else {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ae_exposure_value);
            }
            switch (awbType) {
                case 0:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                    break;
                case 1:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
                    captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                    captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, PublicUtil.colorTemperature(colorTemp));
                    break;
                case 2:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);
                    break;
                case 3:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
                    break;
                case 4:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
                    break;
                case 5:
                    captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);
                    break;
            }
            CameraCaptureSession.CaptureCallback captureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    shineSound();
                    unlockFocus();
                }

            };
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            lastorientation = orientation;
            mCaptureSession.capture(captureBuilder.build(), captureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            LogUtils.dTag(TAG, "mPreviewSize.getWidth() " + mPreviewSize.getHeight() + " mPreviewSize.getWidth() = " + mPreviewSize.getWidth());
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            surface = new Surface(surfaceTexture);
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageSaveReader.getSurface()), mCameraCaptureSessionStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_DISCONNECTED");
            updataNodeInfo();
            adapter.notifyDataSetChanged();
        } else if (event.getType().equals(NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_NODE_STATUS_CHANGED");
            updataNodeInfo();
            adapter.notifyDataSetChanged();
        } else if (event.getType().equals(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_AUTO_CONNECT_LOGIN");
            AppSettings.ONLINE_STATUS_ENABLE = MeshService.getInstance().getOnlineStatus();
            if (!AppSettings.ONLINE_STATUS_ENABLE) {
                MeshService.getInstance().getOnlineStatus();
                int rspMax = TelinkMeshApplication.getInstance().getMeshInfo().getOnlineCountInAll();
                int appKeyIndex = TelinkMeshApplication.getInstance().getMeshInfo().getDefaultAppKeyIndex();
                OnOffGetMessage message = OnOffGetMessage.getSimple(0xFFFF, appKeyIndex, rspMax);
                MeshService.getInstance().sendMeshMessage(message);
            }
        } else if (event.getType().equals(VendorMessage.class.getName())) {
            VendorMessage vendorMessage = (VendorMessage) ((StatusNotificationEvent) event).getNotificationMessage().getStatusMessage();
            byte[] dataArray = vendorMessage.getDataParam();
            currentCCT = dataArray[2];
            currentDIM = dataArray[3];
            currentEV = LightControl.getEv(dataArray[1]);
            tvCCT.setText("CCT: " + (currentCCT * 100) + "k");
            tvDIM.setText(currentDIM + "%");
            tvEV.setText(currentEV + "");
            if(flashControlLayout.getVisibility() == View.VISIBLE){
                flashControlLayout.setCCT(currentCCT*100);
                flashControlLayout.setEV(currentEV);
            }
            if(lightControlLayout.getVisibility() == View.VISIBLE){
                lightControlLayout.setCCT(currentCCT*100);
                lightControlLayout.setDIM(currentDIM);
            }
            LogUtils.dTag(TAG, "dataArray  = " + PublicUtil.toHexString(dataArray));
        }
    }

    private class ImageSaver implements Runnable {
        private final Image mImage;
        private final File mFile;
        private File outFile;
        private Bitmap new_bitmap;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                outFile = new File(mFile, getPicName());
                outFile.createNewFile();
                output = new FileOutputStream(outFile);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();
                if (lastorientation > 315 || lastorientation < 45) { //0度
                    lastorientation = 0;
                } else if (lastorientation >= 45 && lastorientation <= 135) { //90度
                    lastorientation = 90;
                } else if (orientation > 135 && lastorientation < 225) { //180度
                    lastorientation = 180;
                } else if (lastorientation >= 225 && lastorientation <= 315) { //270度
                    lastorientation = 270;
                }
                switch (lastorientation) {
                    case 0:
                        if (mFacing == CameraCharacteristics.LENS_FACING_BACK) {
                            matrix.postRotate(90);
                        } else {
                            matrix.postRotate(270);
                        }
                        break;
                    case 90:
                        matrix.postRotate(180);
                        break;
                    case 180:
                        if (mFacing == CameraCharacteristics.LENS_FACING_BACK) {
                            matrix.postRotate(270);
                        } else {
                            matrix.postRotate(90);
                        }
                    case 270:
                        matrix.postRotate(0);
                        break;
                }
                if (mFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    matrix.postScale(-1, 1);
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                new_bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                if (bitmap != new_bitmap) {
                    bitmap.recycle();
                }
                new_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outFile));
                getActivity().sendBroadcast(mediaScanIntent);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.flush();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ibtnResult.setImageBitmap(new_bitmap);
                    }
                });
            }

        }
    }


    private ImageReader.OnImageAvailableListener mOnImagePreviewAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (reader != null) {
                Image image = reader.acquireNextImage();
                if (image != null) {
                    image.close();
                }
            }
        }
    };
    private ImageReader.OnImageAvailableListener mOnImageSaveAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }
    };


    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ae_exposure_value = 0;
            flashControlLayout.setVisibility(View.GONE);
            lightControlLayout.setVisibility(View.GONE);
            llControl.setVisibility(View.VISIBLE);
            isRegionAF = true;
            float downx = e.getX();
            float downy = e.getY();
            focusView.setVisibility(View.VISIBLE);
            focusView.initValue();
            if (isManualAe) {
                focusView.hideEposureSeek();
            } else {
                focusView.showEposureSeek();
            }
            int leftMargin = 0;
            int topMargin = 0;
            if (downx > 2 * ScreenUtils.getScreenWidth() / 3) {
                focusView.setExposureLeft();
                leftMargin = (int) (downx - (focusView.getWidth() - focusView.getRectViewWH()[0] + focusView.getRectViewWH()[0] / 2));
            } else {
                focusView.setExposureRight();
                leftMargin = (int) (downx - focusView.getRectViewWH()[0] / 2);
            }
            topMargin = (int) (downy - focusView.getHeight() / 2);
            if (leftMargin < 0) {
                leftMargin = 0;
            }
            if (topMargin < (focusView.getRectViewWH()[1] - focusView.getHeight()) / 2) {
                topMargin = (focusView.getRectViewWH()[1] - focusView.getHeight()) / 2;
            }
            if (leftMargin + focusView.getWidth() > ScreenUtils.getScreenWidth()) {
                leftMargin = ScreenUtils.getScreenWidth() - focusView.getWidth();
            }
            if (topMargin + focusView.getRectViewWH()[1] > textureViewH) {
                topMargin = textureViewH - focusView.getHeight();
            }
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = leftMargin;
            layoutParams.topMargin = topMargin;
            focusView.setLayoutParams(layoutParams);
            int oh = (int) (mOutImageSize.getWidth() / ((float) ScreenUtils.getScreenWidth()) * downx);
            int ow = (int) (mOutImageSize.getWidth() / ((float) ScreenUtils.getScreenWidth()) * downy);

            int totalW = cameraParam.getPixelArraySize().getWidth();
            int totalH = cameraParam.getPixelArraySize().getHeight();
            int x = (totalW - mOutImageSize.getHeight()) / 2 + ow;
            int y = (totalH - mOutImageSize.getWidth()) / 2 + mOutImageSize.getWidth() - oh;
            int egion = 300;
            int left = x - egion;
            int top = y - egion;
            int right = x + egion;
            int bottom = y + egion;
            if (left < 0) {
                left = 0;
                right = egion * 2;
            }
            if (top < 0) {
                top = 0;
                bottom = egion * 2;
            }
            if (right > totalW) {
                right = totalW;
                left = totalW - egion * 2;
            }
            if (bottom > totalH) {
                bottom = totalH;
                top = bottom - egion * 2;
            }
            rect = new Rect(left, top, right, bottom);
            if (mPreviewCaptuRerequestBuilder != null) {
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
                } catch (CameraAccessException ee) {
                    ee.printStackTrace();
                }
            }
            ScaleAnimation animation = new ScaleAnimation(1, 1.3f, 1, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            focusView.setAnimation(animation);
            animation.start();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }


    }

    class SurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }

    class CameraDeviceStateCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            if (null != mActivity) {
                mActivity.finish();
            }
        }
    }

    class CameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            if (null == mCameraDevice) {
                return;
            }
            mCaptureSession = cameraCaptureSession;
            try {
                mPreviewCaptuRerequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewCaptuRerequestBuilder.addTarget(surface);
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                if (isManualAe) {
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
                } else {
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                }
                if (isRegionAF) {
                    mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
                }
                switch (awbType) {
                    case 0:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                        break;
                    case 1:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, PublicUtil.colorTemperature(colorTemp));
                        break;
                    case 2:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT);
                        break;
                    case 3:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
                        break;
                    case 4:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT);
                        break;
                    case 5:
                        mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT);
                        break;
                }
                mPreviewCaptuRerequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, CaptureRequest.CONTROL_AE_MODE_OFF);
                mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

        }
    }

    class CameraCaptureSessionCaptureCallback extends CameraCaptureSession.CaptureCallback {
        private void process(CaptureResult result) {
            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
            if (mState != STATE_PREVIEW) {
                LogUtils.dTag(TAG, "afState = " + afState + " aeState = " + aeState);
            }
            switch (mState) {
                case STATE_PREVIEW: {
                    final int aeMode = result.get(CaptureResult.CONTROL_AE_MODE);
                    iso = result.get(CaptureResult.SENSOR_SENSITIVITY);
                    exposure_time = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (aeMode != CaptureRequest.CONTROL_AE_MODE_OFF) {
                                selectView1.setCurrentNounValue(iso - cameraParam.getSensitivityRange().getLower());
                                selectView2.setCurrentNounValue(getExposureTime(exposure_time));
                            }
                            if (!selectView1.isScroll()) {
                                tvIso1.setText("ISO:" + iso);
                                tvIso2.setText(getExposureTimeString(exposure_time));
                            }
                        }
                    });
                    break;
                }

                case STATE_WAITING_FOCUS_LOCK: {
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                            ||
                            CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState ||
                            CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_INACTIVE == afState
                    ) {
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED
                                || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE
                                || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED || isManualAe) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            try {
                                mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                                        CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
                                mState = STATE_WAITING_EXPOSURE_PRECAPTURE;
                                mCaptureSession.capture(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback,
                                        mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
                case STATE_WAITING_EXPOSURE_PRECAPTURE: {
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_EXPOSURE_NON_PRECAPTURE;
                    } else {
                        captureStillPicture();
                    }
                    break;
                }
                case STATE_WAITING_EXPOSURE_NON_PRECAPTURE: {
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            process(result);
        }

    }

    private int getExposureTime(Long exposure_time) {
        double frac = exponentialScalingInverse(exposure_time, cameraParam.getExposureTimeRange().getLower()
                , cameraParam.getExposureTimeRange().getUpper() / 3);
        int new_value = (int) (frac * 1000 + 0.5);
        if (new_value < 0)
            new_value = 0;
        else if (new_value > 1000)
            new_value = 1000;
        return new_value;
    }


    private void lockFocus() {
        try {
            if (flash_open) {
                LightControl.sendFlashMessage(0, currentDeviceMesh);
            }
            mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_FOCUS_LOCK;
            mCaptureSession.capture(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void unlockFocus() {
        try {
            LightControl.sendFlashMessage(1, currentDeviceMesh);
            mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mCaptureSession.capture(mPreviewCaptuRerequestBuilder.build(), null,
                    mBackgroundHandler);
            isRegionAF = false;
            mState = STATE_PREVIEW;
            mPreviewCaptuRerequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mCaptureSession.setRepeatingRequest(mPreviewCaptuRerequestBuilder.build(), mCameraCaptureSessionCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        isTakeComplete = true;
    }

    private void shineSound() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        textureView.startAnimation(alphaAnimation);
        AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            MediaPlayer shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (shootMP != null)
                shootMP.start();
        }
    }

    private String getPicName() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date(time);
        String t = format.format(date);
        return "GD_" + t + ".jpg";
    }


    private Runnable sentRunnable = new Runnable() {
        @Override
        public void run() {
            LightControl.sendLightMeshMessage(currentDIM, currentCCT, currentDeviceMesh);
            isSent = true;
        }
    };


    private Runnable sentFlashRunnable = new Runnable() {
        @Override
        public void run() {
            LightControl.sendFlashEvMeshMessage(LightControl.getEvsend(currentEV), currentCCT, currentDeviceMesh);
            isSent = true;
        }
    };
    private Runnable hideLightInfoRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.dTag(TAG, "hideLightInfoRunnable " + ThreadUtils.isMainThread());
            flControl.removeView(lightInfoView);
        }
    };

    class timeTask extends TimerTask {

        @Override
        public void run() {
            LightControl.sendSearchLightParamMessage(currentDeviceMesh);
        }
    }

    static {
        MeshStatus.Container.register(0x0211F1, VendorMessage.class);
    }
    private void changeLayoutOrientation(int orientation) {

    }

    //    private void setRotationAnimation(float start, float end) {
//        ValueAnimator rotationAnimation = ValueAnimator.ofFloat(start, end);
//        rotationAnimation.setDuration(300);
//        rotationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float) animation.getAnimatedValue();
//                setRotation(currentValue);
//                //Log.d("TAG", "cuurent value is " + currentValue);
//            }
//        });
//        rotationAnimation.start();
//    }
}
