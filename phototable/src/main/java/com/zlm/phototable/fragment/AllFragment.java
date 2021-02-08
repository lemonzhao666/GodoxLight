package com.zlm.phototable.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zlm.phototable.DataList;
import com.zlm.phototable.PickerAdapter;
import com.zlm.phototable.PvShowActivity;
import com.zlm.phototable.R;

public class AllFragment extends MyFragment {
    @Override
    public void doBusiness() {
        super.doBusiness();
        if (this.tvSelect != null) {
            if (allNum > 0) {
                tvSelect.setVisibility(View.VISIBLE);
            } else {
                tvSelect.setVisibility(View.GONE);
            }
        }
        pickerAdapter = new PickerAdapter(R.layout.recycleview_item_album, dataList.getAllList(), mContext);
        pickerAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        ryAlbum.setLayoutManager(this.layoutManager);
        pickerAdapter.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.empty_view_no_photo_video, null, false));
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
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

}
