package com.zlm.phototable;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PickerAdapter extends BaseQuickAdapter<FileItem, BaseViewHolder> {
    private static final String TAG = "PickerAdapter";
    public boolean isShowAllCb;
    private Map<Integer, Boolean> checkStatus;
    public int selectedItem;
//    public TextView tvSelectItem;
    private TextView ivDelete;
    private TextView ivShare;
    private RelativeLayout rlBottm;
    private Context context;
    private List<FileItem> fileItemList;

    public PickerAdapter(int layoutResId, List<FileItem> data, Context context) {
        super(layoutResId, data);
        this.context = context;
        checkStatus = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            checkStatus.put(i, false);
        }
        this.fileItemList = data;
    }

    public void concelSelect() {
        Iterator iter = checkStatus.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            entry.setValue(false);
        }
        selectedItem = 0;
//        tvSelectItem.setText(R.string.select_project);
        notifyDataSetChanged();
        DataList.getInstance().setEnter(true);
    }

    @Override
    protected void convert(final BaseViewHolder helper, FileItem fileItem) {
        Uri fileUri = Uri.fromFile(new File(fileItem.getFilePath()));
        Glide.with(mContext).load(fileUri).into((ImageView) helper.getView(R.id.iv_album));
        FileItem.Type type = fileItem.getType();
        FrameLayout flRoot = helper.getView(R.id.fl_item_root);
        ViewGroup.LayoutParams layoutParams = flRoot.getLayoutParams();
        int width = Util.getDisplay(context).getWidth();
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        if(mConfiguration.orientation ==  Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.width = width / 5;
            layoutParams.height = width / 5;
        }else{
            layoutParams.width = width / 3;
            layoutParams.height = width / 3;
        }
        flRoot.setLayoutParams(layoutParams);
        if (type == FileItem.Type.Image) {
            helper.setGone(R.id.tv_video, false);
        } else {
            helper.setVisible(R.id.tv_video, true);
            long seconds = fileItem.getmDuration() / 1000;
            long minute = seconds / 60;
            seconds %= 60;
            String secondsStr = seconds < 10 ? "0" + seconds : seconds + "";
            String minuteStr = minute < 10 ? "0" + minute : minute + "";
            helper.setText(R.id.tv_video, minuteStr + ":" + secondsStr);
        }
        helper.setOnCheckedChangeListener(R.id.cb_selected, null);
        final int position = helper.getAdapterPosition();
        helper.setChecked(R.id.cb_selected, checkStatus.get(position));
        if (checkStatus.get(position)) {
            helper.setBackgroundColor(R.id.rl_select, context.getResources().getColor(R.color.transparent_88));
            helper.setVisible(R.id.cb_selected, true);
        } else {
            helper.setBackgroundColor(R.id.rl_select, context.getResources().getColor(R.color.transparent));
            helper.setVisible(R.id.cb_selected, false);
        }
        helper.addOnClickListener(R.id.rl_select);
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (checkStatus.get(position) == true) {
                    checkStatus.put(position, false);
                    selectedItem--;
                } else {
                    checkStatus.put(position, true);
                    selectedItem++;
                }
                notifyItemChanged(position);
                if (selectedItem == 0) {
                    rlBottm.setVisibility(View.GONE);
                } else {
                    rlBottm.setVisibility(View.VISIBLE);
                }
                ivShare.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }
        });
        if (isShowAllCb) {
            helper.setVisible(R.id.rl_select, true);
        } else {
            helper.setVisible(R.id.rl_select, false);
        }
    }

    public void setShowAllCb(boolean isShowAllCb) {
        this.isShowAllCb = isShowAllCb;
    }


    public void setDeleteShareView(TextView ivShare, TextView ivDelte, RelativeLayout rlBottm) {
        this.ivShare = ivShare;
        this.ivDelete = ivDelte;
        this.rlBottm = rlBottm;
    }

    public Map<Integer, Boolean> getCheckStatus() {
        return checkStatus;
    }

    public List<FileItem> getFileItemList() {
        return fileItemList;
    }

    public void setFileItemList(List<FileItem> fileItemList) {
        this.fileItemList = fileItemList;
    }
}

