package com.godox.light;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zlm.base.model.NodeInfo;

import java.util.List;


public class UnAddDeviceAdapter extends BaseQuickAdapter<NodeInfo, BaseViewHolder> {
    public UnAddDeviceAdapter(int layoutResId, List<NodeInfo> deviceList) {
        super(layoutResId, deviceList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NodeInfo item) {
        helper.setText(R.id.tv_name, "0x"+Integer.toHexString(item.meshAddress) + "  " + item.macAddress);
        helper.setText(R.id.btn_connect,mContext.getString(R.string.add));
        helper.addOnClickListener(R.id.btn_connect);
    }
}
