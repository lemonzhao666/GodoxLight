package com.zlm.phototable;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.zlm.base.BaseBackActivity;
import com.zlm.base.media.PlayView;
import com.zlm.phototable.api.AbsPhotoTable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PvShowActivity extends BaseBackActivity implements View.OnClickListener {

    RecyclerView rvContainer;
    TextView ivDelete;
    RelativeLayout rlBottom;
    FrameLayout flRoot;
    ArrayList<FileItem> fileItems = null;
    private PvShowPagerAdapter pvShowPagerAdapter;
    private int currentPosition;
    private PlayView videoView;

    public void playVideo(int position) {
        View itemView = rvContainer.getLayoutManager().findViewByPosition(position);
        videoView = itemView.findViewById(R.id.playview);
        LogUtils.dTag(TAG, "FilePath = " + fileItems.get(position).getFilePath());
        videoView.setDataPath(fileItems.get(position).getFilePath());
    }

    public void releaseVideo(int position) {
        View itemView = rvContainer.getLayoutManager().findViewByPosition(position);
        PlayView videoView = itemView.findViewById(R.id.playview);
        videoView.releasePlayer();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_delete) {
            showBottmDialog(R.layout.bottm_item);
        } else if (i == R.id.iv_share) {
            FileItem fileItem = fileItems.get(currentPosition);
            Intent shareIntent = new Intent();
            shareIntent.setAction("android.intent.action.SEND");
            shareIntent.putExtra("android.intent.extra.STREAM", Util.getContentUri(this, fileItem));
            if (fileItem.getType() == FileItem.Type.Video) {
                shareIntent.setType("video/*");
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            } else {
                shareIntent.setType("image/*");
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        } else if (i == R.id.tv_right) {
//            VideoEdit.init(this);
//            FileItem fileItem = fileItems.get(currentPosition);
//            File file = new File(fileItem.getFilePath());
//            Intent intent = new Intent(this, VideoEditActivity.class);
//            intent.putExtra("EditVideoPath", file.getAbsolutePath());
//            startActivity(intent);
//            videoView.pause();
        }
    }

    private void showBottmDialog(@LayoutRes int resource) {
        View view = View.inflate(this, resource, null);
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(-1, -2);
        window.setGravity(80);
        window.setWindowAnimations(R.style.AnimBottom);
        dialog.show();
        view.findViewById(R.id.tv_concel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileItem fileItem = fileItems.get(currentPosition);
                deleteFileItem(fileItem);
                fileItems.remove(fileItem);
                if (fileItems.size() == 0) {
                    finish();
                    return;
                }
                pvShowPagerAdapter.notifyItemRemoved(currentPosition);
                pvShowPagerAdapter.notifyItemRangeChanged(currentPosition, fileItems.size() - currentPosition);
                pvShowPagerAdapter.notifyDataSetChanged();
                if (currentPosition == fileItems.size()) {
                    setTitle(getDate(new File(fileItems.get(currentPosition-1).getFilePath()).lastModified()));
                    rvContainer.scrollToPosition(currentPosition - 1);
                } else {
                    setTitle(getDate(new File(fileItems.get(currentPosition).getFilePath()).lastModified()));
                    rvContainer.scrollToPosition(currentPosition);
                }
                dialog.dismiss();
            }
        });
    }


    private void deleteFileItem(FileItem deleteItem) {
        Uri uri = null;
        String where = null;
        if (deleteItem.getType() == FileItem.Type.Image) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Images.Media.DATA + "='" + deleteItem.getFilePath() + "'";
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Video.Media.DATA + "='" + deleteItem.getFilePath() + "'";
        }
        ContentResolver mContentResolver = getContentResolver();
        mContentResolver.delete(uri, where, null);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(deleteItem.getFilePath());
        Uri urii = Uri.fromFile(file);
        intent.setData(urii);
        sendBroadcast(intent);
    }

    //yink add
    @Override
    protected void onResume() {
        super.onResume();
        // yink add
        AbsPhotoTable.setPhotoTableFinishListener(new AbsPhotoTable.FinishListener() {
            @Override
            public void finish() {
                close();
            }
        });
    }

    private void close() {
        finish();
    }


    @Override
    public void initData(Bundle bundle) {
        setFullWindow();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_pv_show;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        rvContainer = findViewById(R.id.rv_container);
        ivDelete = findViewById(R.id.iv_delete);
        rlBottom = findViewById(R.id.rl_bottom);
        flRoot = findViewById(R.id.fl_root);
        tvTitle.setTextSize(14f);
        setRightText(getString(R.string.edit));
    }

    @Override
    public void doBusiness() {
        currentPosition = getIntent().getIntExtra("position", 0);
        int type = getIntent().getIntExtra("type", 0);

        switch (type) {
            case 0:
                fileItems = DataList.getInstance().getAllList();
                break;
            case 1:
                fileItems = DataList.getInstance().getImageList();
                break;
            case 2:
                fileItems = DataList.getInstance().getVideoList();
                break;
        }
        setTitle(getDate(new File(fileItems.get(currentPosition).getFilePath()).lastModified()));
        MyLayoutManager myLayoutManager = new MyLayoutManager(this, 0, false);
        pvShowPagerAdapter = new PvShowPagerAdapter(R.layout.vp_item_fileitem, fileItems, this);
        rvContainer.setLayoutManager(myLayoutManager);
        rvContainer.setAdapter(pvShowPagerAdapter);
        rvContainer.scrollToPosition(currentPosition);
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            public void onPageRelease(int position) {
                try {
                    if ((pvShowPagerAdapter.getFileItems().get(position)).getType() == FileItem.Type.Video) {
                        releaseVideo(position);
                    }
                } catch (Exception e) {
                }
            }

            public void onPageSelected(int position, boolean isOver) {
                currentPosition = position;
                setTitle(getDate(new File(fileItems.get(currentPosition).getFilePath()).lastModified()));
                boolean isVideo = (pvShowPagerAdapter.getFileItems().get(position)).getType() == FileItem.Type.Video;
                tvRight.setVisibility(isVideo ? View.VISIBLE : View.GONE);
                if (isVideo) {
                    playVideo(position);
                }
            }

            @Override
            public void onChildViewAttachedToWindow() {
                if (videoView != null) {
                    videoView.pause();
                }
            }
        });

    }

    @Override
    public void initListener() {
        rvContainer.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        rlBottom.setOnClickListener(this);
        flRoot.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        try {
            if ((fileItems.get(currentPosition)).getType() == FileItem.Type.Video) {
                View itemView = rvContainer.getLayoutManager().findViewByPosition(currentPosition);
                PlayView videoView = itemView.findViewById(R.id.playview);
                videoView.releasePlayer();
            }

        } catch (Exception e) {

        } finally {
            super.onDestroy();
        }

    }

    private String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return format.format(date);
    }

}
