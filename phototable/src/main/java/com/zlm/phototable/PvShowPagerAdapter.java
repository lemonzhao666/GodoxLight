package com.zlm.phototable;

import android.content.Context;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.List;

public class PvShowPagerAdapter  extends BaseQuickAdapter<FileItem, BaseViewHolder> {
    private Context context;
    private List<FileItem> fileItems;

    public PvShowPagerAdapter(int layoutResId, @Nullable List<FileItem> data, Context context) {
        super(layoutResId, data);
        this.fileItems = data;
        this.context = context;
    }

    public void convert(@NonNull BaseViewHolder helper, FileItem fileItem) {
        LogUtils.dTag("PvShowPagerAdapter","start");
        for (int i = 0; i <fileItems.size() ; i++) {
            LogUtils.dTag("PvShowPagerAdapter", "fileItems.get("+i+") = "+fileItems.get(i).getType()+" fileItems.size() = "+fileItems.size());
            LogUtils.dTag("PvShowPagerAdapter", "path = "+fileItems.get(i).getFilePath());
        }
        LogUtils.dTag("PvShowPagerAdapter","end");

        if (fileItem.getType() == FileItem.Type.Image) {
            helper.setVisible(R.id.playview, false);
            helper.setVisible(R.id.iv_image, true);
            Glide.with(context).load(new File(fileItem.getFilePath())).into((ImageView) helper.getView(R.id.iv_image));
            return;
        }
        helper.setVisible(R.id.playview, true);
        helper.setVisible(R.id.iv_image, false);

    }


    public List<FileItem> getFileItems() {
        return fileItems;
    }


}
