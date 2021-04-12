package com.godox.light;

import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.telink.ble.mesh.core.MeshUtils;
import com.telink.ble.mesh.entity.AdvertisingDevice;
import com.telink.ble.mesh.entity.BindingDevice;
import com.telink.ble.mesh.entity.CompositionData;
import com.telink.ble.mesh.entity.ProvisioningDevice;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.BindingEvent;
import com.telink.ble.mesh.foundation.event.ProvisioningEvent;
import com.telink.ble.mesh.foundation.event.ScanEvent;
import com.telink.ble.mesh.foundation.parameter.BindingParameters;
import com.telink.ble.mesh.foundation.parameter.ProvisioningParameters;
import com.telink.ble.mesh.foundation.parameter.ScanParameters;
import com.zlm.base.BaseFragment;
import com.zlm.base.SharedPreferenceHelper;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.MeshInfo;
import com.zlm.base.model.NodeInfo;
import com.zlm.base.model.PrivateDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnAddDeviceFragment extends BaseFragment implements EventListener<String> {
    private ImageButton ibtnRight;
    private List<NodeInfo> nodeInfos;
    private UnAddDeviceAdapter unAddDeviceAdapter;
    private MeshInfo mesh;
    private NodeInfo currentNodeInfo;
    private Map<String, AdvertisingDevice> advertisingDeviceMap = new HashMap<>();
    private int elementCnt;
    private QMUITipDialog tipDialog;
    private RotateAnimation rotateAnimation;
    private boolean isScanning;
    private QMUITipDialog scanDialog;

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_un_add_device;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        ibtnRight = ((ConnectActivity) getActivity()).ivRight;
        RecyclerView recycleview = view.findViewById(R.id.recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recycleview.setLayoutManager(layoutManager);
        recycleview.setItemAnimator(new DefaultItemAnimator());
        nodeInfos = new ArrayList<>();
        unAddDeviceAdapter = new UnAddDeviceAdapter(R.layout.recycleview_item_device2, nodeInfos);
        recycleview.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recycleview.setAdapter(unAddDeviceAdapter);
        ibtnRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void doBusiness() {
        TelinkMeshApplication.getInstance().addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_SUCCESS, this);
        TelinkMeshApplication.getInstance().addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_FAIL, this);
        TelinkMeshApplication.getInstance().addEventListener(BindingEvent.EVENT_TYPE_BIND_SUCCESS, this);
        TelinkMeshApplication.getInstance().addEventListener(BindingEvent.EVENT_TYPE_BIND_FAIL, this);
        TelinkMeshApplication.getInstance().addEventListener(ScanEvent.EVENT_TYPE_SCAN_TIMEOUT, this);
        TelinkMeshApplication.getInstance().addEventListener(ScanEvent.EVENT_TYPE_DEVICE_FOUND, this);
        mesh = TelinkMeshApplication.getInstance().getMeshInfo();
        startScan();
    }

    @Override
    public void initListener() {
        ibtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        unAddDeviceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (TelinkMeshApplication.getInstance().getMeshInfo().nodes.size() == 3) {
                    ToastUtils.showShort("最多连接3个设备!");
                    return;
                }
                currentNodeInfo = nodeInfos.get(position);
                tipDialog = new QMUITipDialog.Builder(mActivity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getString(R.string.adding))
                        .create();
                tipDialog.setCancelable(false);
                tipDialog.show();
                MeshService.getInstance().startProvisioning(getProvisioningParameters(advertisingDeviceMap.get(currentNodeInfo.macAddress)));
            }
        });
    }

    private ProvisioningParameters getProvisioningParameters(AdvertisingDevice advertisingDevice) {
        byte[] serviceData = MeshUtils.getMeshServiceData(advertisingDevice.scanRecord, true);
        if (serviceData == null || serviceData.length < 16) {
            return null;
        }
        final int uuidLen = 16;
        byte[] deviceUUID = new byte[uuidLen];
        System.arraycopy(serviceData, 0, deviceUUID, 0, uuidLen);
        int address = mesh.provisionIndex;
        currentNodeInfo.meshAddress = address;
        ProvisioningDevice provisioningDevice = new ProvisioningDevice(advertisingDevice.device, deviceUUID, address);
        byte[] oob = TelinkMeshApplication.getInstance().getMeshInfo().getOOBByDeviceUUID(deviceUUID);
        if (oob != null) {
            provisioningDevice.setAuthValue(oob);
        } else {
            final boolean autoUseNoOOB = SharedPreferenceHelper.isNoOOBEnable(mContext);
            provisioningDevice.setAutoUseNoOOB(autoUseNoOOB);
        }
        return new ProvisioningParameters(provisioningDevice);
    }

    private void startScan() {
        if (isScanning) {
            return;
        }
        isScanning = true;
        enableUI(true);
        nodeInfos.clear();
        ScanParameters parameters = ScanParameters.getDefault(false, false);
        parameters.setScanTimeout(2 * 1000);
        MeshService.getInstance().startScan(parameters);
    }

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(ProvisioningEvent.EVENT_TYPE_PROVISION_SUCCESS)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_PROVISION_SUCCESS");
            onProvisionSuccess((ProvisioningEvent) event);
        } else if (event.getType().equals(ProvisioningEvent.EVENT_TYPE_PROVISION_FAIL)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_PROVISION_FAIL");
            onProvisionFail((ProvisioningEvent) event);
        } else if (event.getType().equals(ScanEvent.EVENT_TYPE_SCAN_TIMEOUT)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_SCAN_TIMEOUT");
            isScanning = false;
            enableUI(false);
            LogUtils.dTag(TAG, "EVENT_TYPE_SCAN_TIMEOUT");
        } else if (event.getType().equals(BindingEvent.EVENT_TYPE_BIND_SUCCESS)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_BIND_SUCCESS");
            onKeyBindSuccess((BindingEvent) event);
            ToastUtils.showShort(getString(R.string.add_success));
            tipDialog.dismiss();
            nodeInfos.remove(currentNodeInfo);
            unAddDeviceAdapter.notifyDataSetChanged();
        } else if (event.getType().equals(BindingEvent.EVENT_TYPE_BIND_FAIL)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_BIND_FAIL");
            onKeyBindFail((BindingEvent) event);
        } else if (event.getType().equals(ScanEvent.EVENT_TYPE_DEVICE_FOUND)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_DEVICE_FOUND");
            onDeviceFound(((ScanEvent) event).getAdvertisingDevice());
        }
    }

    @Override
    public void onDestroy() {
        enableUI(false);
        MeshService.getInstance().stopScan();
        ibtnRight.clearAnimation();
        TelinkMeshApplication.getInstance().removeEventListener(this);
        super.onDestroy();
    }

    private void onDeviceFound(AdvertisingDevice advertisingDevice) {
        byte[] serviceData = MeshUtils.getMeshServiceData(advertisingDevice.scanRecord, true);
        if (serviceData == null || serviceData.length < 16) {
            return;
        }
        final int uuidLen = 16;
        byte[] deviceUUID = new byte[uuidLen];
        System.arraycopy(serviceData, 0, deviceUUID, 0, uuidLen);
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.deviceUUID = deviceUUID;
        nodeInfo.macAddress = advertisingDevice.device.getAddress();
        nodeInfo.name = advertisingDevice.device.getName();
        advertisingDeviceMap.put(nodeInfo.macAddress, advertisingDevice);
        nodeInfos.add(nodeInfo);
        unAddDeviceAdapter.notifyDataSetChanged();
    }

    private void onProvisionFail(ProvisioningEvent event) {
        ToastUtils.showShort(getString(R.string.add_failed));
        tipDialog.dismiss();
    }

    private void onKeyBindFail(BindingEvent event) {
        ToastUtils.showShort(getString(R.string.add_failed));
        tipDialog.dismiss();
    }

    private void onProvisionSuccess(ProvisioningEvent event) {
        ProvisioningDevice remote = event.getProvisioningDevice();
        currentNodeInfo.state = NodeInfo.STATE_BINDING;
        elementCnt = remote.getDeviceCapability().eleNum;
        currentNodeInfo.elementCnt = elementCnt;
        currentNodeInfo.deviceKey = remote.getDeviceKey();
        mesh.provisionIndex += elementCnt;
        mesh.insertDevice(currentNodeInfo);
        mesh.saveOrUpdate(mContext);
        final boolean privateMode = SharedPreferenceHelper.isPrivateMode(mContext);
        boolean defaultBound = false;
        if (privateMode && remote.getDeviceUUID() != null) {
            PrivateDevice device = PrivateDevice.filter(remote.getDeviceUUID());
            if (device != null) {
                final byte[] cpsData = device.getCpsData();
                currentNodeInfo.compositionData = CompositionData.from(cpsData);
                defaultBound = true;
            }
        }
        currentNodeInfo.setDefaultBind(defaultBound);
        int appKeyIndex = mesh.getDefaultAppKeyIndex();
        BindingDevice bindingDevice = new BindingDevice(currentNodeInfo.meshAddress, currentNodeInfo.deviceUUID, appKeyIndex);
        bindingDevice.setDefaultBound(defaultBound);
        MeshService.getInstance().startBinding(new BindingParameters(bindingDevice));
    }

    private void onKeyBindSuccess(BindingEvent event) {
        BindingDevice remote = event.getBindingDevice();
        currentNodeInfo.state = NodeInfo.STATE_BIND_SUCCESS;
        if (!remote.isDefaultBound()) {
            currentNodeInfo.compositionData = remote.getCompositionData();
        }
        mesh.saveOrUpdate(mContext);
        currentNodeInfo.state = NodeInfo.STATE_BIND_SUCCESS;
        currentNodeInfo.meshAddress = mesh.provisionIndex - elementCnt;
    }

    private void enableUI(boolean show) {
        if (!show) {
//            if (rotateAnimation != null) {
//                rotateAnimation.cancel();
//                scanDialog.dismiss();
//            }
            if (scanDialog != null) {
                scanDialog.dismiss();
            }
        } else {
            scanDialog = new QMUITipDialog.Builder(mActivity)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getString(R.string.scanning))
                    .create();
            scanDialog.setCancelable(false);
            scanDialog.show();
//            rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotateAnimation.setDuration(500);
//            rotateAnimation.setFillAfter(true);
//            rotateAnimation.setRepeatCount(-1);
//            ibtnRight.startAnimation(rotateAnimation);
        }
    }

}
