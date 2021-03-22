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
        int len = item.macAddress.length();
        helper.setText(R.id.tv_name,"GDFLASH_"+item.macAddress.substring(len - 5, len-3)+ item.macAddress.substring(len - 2, len));
        helper.setText(R.id.btn_connect, mContext.getString(R.string.add));
        helper.addOnClickListener(R.id.btn_connect);
    }
}
