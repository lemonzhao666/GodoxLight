package com.godox.light;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.telink.ble.mesh.core.message.config.NodeResetMessage;
import com.telink.ble.mesh.core.message.config.NodeResetStatusMessage;
import com.telink.ble.mesh.core.message.generic.OnOffGetMessage;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.AutoConnectEvent;
import com.telink.ble.mesh.foundation.event.MeshEvent;
import com.telink.ble.mesh.foundation.parameter.AutoConnectParameters;
import com.zlm.base.BaseFragment;
import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.AppSettings;
import com.zlm.base.model.NodeInfo;
import com.zlm.base.model.NodeStatusChangedEvent;

import java.util.ArrayList;
import java.util.List;


public class AddDeviceFragment extends BaseFragment implements EventListener<String> {
    private ImageButton ibtnRight;
    private List<NodeInfo> nodeInfos;
    private AddDeviceAdapter addDeviceAdapter;
    private NodeInfo currentNodeInfo;
    private Handler handler = new Handler();
    private boolean kickDirect;
    private QMUITipDialog tipDialog;

    @Override
    public void initData(Bundle bundle) {}

    @Override
    public int bindLayout() {
        return R.layout.fragment_add_device;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        ibtnRight = ((ConnectActivity) getActivity()).ivRight;
        RecyclerView rvDevice = view.findViewById(R.id.rv_connected_device);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvDevice.setLayoutManager(layoutManager);
        rvDevice.setItemAnimator(new DefaultItemAnimator());
        nodeInfos = new ArrayList<>();
        nodeInfos = TelinkMeshApplication.getInstance().getMeshInfo().nodes;
        addDeviceAdapter = new AddDeviceAdapter(R.layout.recycleview_item_device, nodeInfos);
        rvDevice.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        rvDevice.setAdapter(addDeviceAdapter);
    }

    @Override
    public void doBusiness() {
        TelinkMeshApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        TelinkMeshApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_RESET, this);
        TelinkMeshApplication.getInstance().addEventListener(NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED, this);
        TelinkMeshApplication.getInstance().addEventListener(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);

    }

    @Override
    public void initListener() {
        addDeviceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                currentNodeInfo = nodeInfos.get(position);
                kickOut();
            }
        });
        ibtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int appKeyIndex = TelinkMeshApplication.getInstance().getMeshInfo().getDefaultAppKeyIndex();
                OnOffGetMessage message = OnOffGetMessage.getSimple(0xFFFF, appKeyIndex, /*rspMax*/ 0);
                boolean cmdSent = MeshService.getInstance().sendMeshMessage(message);
                if (cmdSent) {
                    for (NodeInfo deviceInfo : nodeInfos) {
                        deviceInfo.setOnOff(-1);
                    }
                    addDeviceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MeshService.getInstance().autoConnect(new AutoConnectParameters());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(this);
    }

    private void kickOut() {
        boolean cmdSent = MeshService.getInstance().sendMeshMessage(new NodeResetMessage(currentNodeInfo.meshAddress));
        kickDirect = currentNodeInfo.meshAddress == (MeshService.getInstance().getDirectConnectedNodeAddress());
        tipDialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getString(R.string.removing))
                .create();
        tipDialog.show();
        if (!cmdSent || !kickDirect) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onKickOutFinish();
                }
            }, 3 * 1000);
        }
    }

    private void onKickOutFinish() {
        handler.removeCallbacksAndMessages(null);
        MeshService.getInstance().removeDevice(currentNodeInfo.meshAddress);
        TelinkMeshApplication.getInstance().getMeshInfo().removeDeviceByMeshAddress(currentNodeInfo.meshAddress);
        TelinkMeshApplication.getInstance().getMeshInfo().saveOrUpdate(mContext);
        tipDialog.dismiss();
        addDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void performed(final Event<String> event) {
        if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED)) {
            LogUtils.dTag(TAG, "EVENT_TYPE_DISCONNECTED");
            if (kickDirect) {
                onKickOutFinish();
            }
            addDeviceAdapter.resetDevices(TelinkMeshApplication.getInstance().getMeshInfo().nodes);
        } else if (event.getType().equals(NodeResetStatusMessage.class.getName())) {
            LogUtils.dTag(TAG, "NodeResetStatusMessage");
            if (!kickDirect) {
                onKickOutFinish();
            }
        } else if (event.getType().equals(NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED)
                || event.getType().equals(MeshEvent.EVENT_TYPE_MESH_RESET)) {
            LogUtils.dTag(TAG, "NODE_STATUS_CHANGED");
            addDeviceAdapter.resetDevices(TelinkMeshApplication.getInstance().getMeshInfo().nodes);
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
        }
    }

}
