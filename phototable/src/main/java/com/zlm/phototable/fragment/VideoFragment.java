package com.zlm.phototable.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zlm.phototable.DataList;
import com.zlm.phototable.PickerAdapter;
import com.zlm.phototable.PvShowActivity;
import com.zlm.phototable.R;

public class VideoFragment extends MyFragment {
    @Override
    public void doBusiness() {
        super.doBusiness();
        if (tvSelect != null) {
            if (videoNum > 0) {
                tvSelect.setVisibility(View.VISIBLE);
            } else {
                tvSelect.setVisibility(View.GONE);
            }
        }
        pickerAdapter = new PickerAdapter(R.layout.recycleview_item_album, dataList.getVideoList(), mContext);
        pickerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        ryAlbum.setLayoutManager(layoutManager);
        View empty = LayoutInflater.from(mContext).inflate(R.layout.empty_view_no_video, null, false);
        pickerAdapter.setEmptyView(empty);
        ryAlbum.setAdapter(pickerAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        pickerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (DataList.getInstance().isEnter()) {
                    Intent intent = new Intent(mContext, PvShowActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("type", 2);
                    startActivityForResult(intent, 2);
                }
            }
        });
    }
}
