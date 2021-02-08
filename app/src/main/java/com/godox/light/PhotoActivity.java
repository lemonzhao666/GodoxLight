package com.godox.light;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.zlm.base.BaseBackActivity;
import com.zlm.phototable.MyLayoutManager;
import com.zlm.phototable.OnViewPagerListener;
import com.zlm.phototable.PhotoTableActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PhotoActivity extends BaseBackActivity {
    RecyclerView rvContainer;
    TextView ivDelete;
    RelativeLayout rlBottom;
    FrameLayout flRoot;
    private int currentPosition;
    private String SAVEPATH = Environment.getExternalStorageDirectory().toString() + "/DCIM/GodoxLight";
    private ArrayList<File> fileList;
    private PhotoAdapter photoAdapter;
    private MyLayoutManager myLayoutManager;

    @Override
    public int bindLayout() {
        return R.layout.activity_photo;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        rvContainer = findViewById(R.id.rv_container);
        ivDelete = findViewById(R.id.iv_delete);
        rlBottom = findViewById(R.id.rl_bottom);
        flRoot = findViewById(R.id.fl_root);
        tvTitle.setTextSize(14f);
        setRightText(getString(R.string.all_photo));
        myLayoutManager = new MyLayoutManager(this, 0, false);
        rvContainer.setLayoutManager(myLayoutManager);
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(int i) {

            }

            @Override
            public void onPageSelected(int i, boolean z) {
                currentPosition = i;
                setTitle(getDate(fileList.get(currentPosition).lastModified()));
            }

            @Override
            public void onChildViewAttachedToWindow() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        File[] images = new File(SAVEPATH).listFiles();
        if(images.length>0){
            for (int i = 0; i < images.length - 1; i++) {
                for (int j = 0; j < images.length - i - 1; j++) {
                    if (images[j].lastModified() < images[j + 1].lastModified()) {
                        File temp = images[j];
                        images[j] = images[j + 1];
                        images[j + 1] = temp;
                    }
                }
            }
            fileList =  new ArrayList<>(Arrays.asList(images));
            photoAdapter = new PhotoAdapter(R.layout.vp_item_file, fileList, this);
            rvContainer.scrollToPosition(0);
            rvContainer.setAdapter(photoAdapter);
            rlBottom.setVisibility(View.VISIBLE);
            rvContainer.setVisibility(View.VISIBLE);
        }else{
            rlBottom.setVisibility(View.GONE);
            rvContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottmDialog(com.zlm.phototable.R.layout.bottm_item);
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, PhotoTableActivity.class));
            }
        });
    }

    private String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return format.format(date);

    }

    private void showBottmDialog(@LayoutRes int resource) {
        View view = View.inflate(this, resource, null);
        final Dialog dialog = new Dialog(this, com.zlm.phototable.R.style.dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(-1, -2);
        window.setGravity(80);
        window.setWindowAnimations(com.zlm.phototable.R.style.AnimBottom);
        dialog.show();
        view.findViewById(com.zlm.phototable.R.id.tv_concel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                File fileItem = fileList.get(currentPosition);
                deleteFileItem(fileItem);
                fileList.remove(fileItem);
                if (fileList.size() == 0) {
                    finish();
                    return;
                }
                photoAdapter.notifyItemRemoved(currentPosition);
                photoAdapter.notifyItemRangeChanged(currentPosition, fileList.size() - currentPosition);
                photoAdapter.notifyDataSetChanged();
                if (currentPosition == fileList.size()) {
                    setTitle(getDate(new File(fileList.get(currentPosition-1).getAbsolutePath()).lastModified()));
                    rvContainer.scrollToPosition(currentPosition - 1);
                } else {
                    setTitle(getDate(new File(fileList.get(currentPosition).getAbsolutePath()).lastModified()));
                    rvContainer.scrollToPosition(currentPosition);
                }
                dialog.dismiss();
            }
        });
    }

    private void deleteFileItem(File fileitem) {
        Uri uri = null;
        String where = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        where = MediaStore.Images.Media.DATA + "='" + fileitem.getAbsolutePath() + "'";

        ContentResolver mContentResolver = getContentResolver();
        mContentResolver.delete(uri, where, null);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(fileitem.getAbsolutePath());
        Uri urii = Uri.fromFile(file);
        intent.setData(urii);
        sendBroadcast(intent);
    }
}
