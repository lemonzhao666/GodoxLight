package com.godox.light;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.List;

public class PhotoAdapter extends BaseQuickAdapter<File, BaseViewHolder> {
    private Context context;

    public PhotoAdapter(int layoutResId, @Nullable List<File> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, File item) {
        Glide.with(context).load(item.getAbsolutePath()).into((ImageView) helper.getView(R.id.iv_image));
    }
}
