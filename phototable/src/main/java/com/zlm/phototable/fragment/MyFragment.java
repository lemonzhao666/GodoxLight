package com.zlm.phototable.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zlm.base.BaseFragment;
import com.zlm.phototable.DataList;
import com.zlm.phototable.FileItem;
import com.zlm.phototable.GridItemDecoration;
import com.zlm.phototable.PickerAdapter;
import com.zlm.phototable.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyFragment extends BaseFragment {
    protected ArrayList<FileItem> imageList = new ArrayList<>();
    protected ArrayList<FileItem> videoList = new ArrayList<>();
    protected ArrayList<FileItem> allList = new ArrayList<>();
    RecyclerView ryAlbum;
    protected int imageNum;
    protected int videoNum;
    protected int allNum;
    protected PickerAdapter pickerAdapter;
    protected TextView tvSelect;
    protected GridLayoutManager layoutManager;
    protected DataList dataList;
    private TextView tvTime;

    @Override
    public void initData(Bundle bundle) {

    }

    public int bindLayout() {
        return R.layout.fragment_base;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        ryAlbum = view.findViewById(R.id.ry_album);
        tvTime = view.findViewById(R.id.tv_time);
        GridItemDecoration gridItemDecoration = new GridItemDecoration.Builder(mContext)
                .setHorizontalSpan(R.dimen.lay_1)
                .setVerticalSpan(R.dimen.lay_1)
                .setColorResource(R.color.white)
                .setShowLastLine(true)
                .build();
        ryAlbum.addItemDecoration(gridItemDecoration);
    }

    @Override
    public void doBusiness() {
        getPhotos();
//        getVideos();

        if (imageList.size() > 0) {
            Collections.sort(imageList);
        }
        if (videoList.size() > 0) {
            Collections.sort(videoList);
        }
        if (imageList.size() > 0 || videoList.size() > 0) {
            allList.addAll(imageList);
            allList.addAll(videoList);
            Collections.sort(allList);
        }
        dataList = DataList.getInstance();
        dataList.setAllList(allList);
        dataList.setImageList(imageList);
        dataList.setVideoList(videoList);
        allNum = dataList.getAllList().size();
        imageNum = dataList.getImageList().size();
        videoNum = dataList.getVideoList().size();
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(mContext, 5);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(mContext, 3);
        }
    }

    @Override
    public void initListener() {
        ryAlbum.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    tvTime.setVisibility(View.GONE);
                } else {
                    setTime();
                }
            }
        });
    }

    protected void setTime() {
        RecyclerView.LayoutManager layoutManager = ryAlbum.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int lastItemPosition = gridLayoutManager.findLastVisibleItemPosition();
            int firstItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            List<FileItem> fileItemList = ((PickerAdapter) ryAlbum.getAdapter()).getData();
            if (fileItemList == null || fileItemList.size() == 0) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                String fristTime = formatter.format(new File(fileItemList.get(firstItemPosition).getFilePath()).lastModified());
                String lastTime = formatter.format(new File(fileItemList.get(lastItemPosition).getFilePath()).lastModified());
                if (fristTime.equals(lastTime)) {
                    tvTime.setText(fristTime);
                } else {
                    tvTime.setText(lastTime + getString(R.string.go) + "\r\n" + fristTime);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) ryAlbum.getLayoutManager();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.setSpanCount(5);
        } else {
            gridLayoutManager.setSpanCount(3);
        }
        ryAlbum.getAdapter().notifyDataSetChanged();
    }

    private boolean getPhotos() {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContext().getContentResolver();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED
        };
        Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " desc");

        if (cursor == null) {
            return false;
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                FileItem item = new FileItem(path, fileName, size, date, 0);
                item.setType(FileItem.Type.Image);
                if(Long.valueOf(size)>=100000){
                    imageList.add(item);
                }
            }
        }
        cursor.close();
        return true;
    }

    private boolean getVideos() {
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = getContext().getContentResolver();
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED
        };
        Cursor cursor = cr.query(videoUri, projection, null, null, null);
        if (cursor == null) {
            return false;
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                FileItem item = new FileItem(path, name, null, date, duration);
                item.setType(FileItem.Type.Video);
                if (duration > 0) {
                    videoList.add(item);
                }
            }
        }
        cursor.close();
        return true;
    }

    public PickerAdapter getPickerAdapter() {
        return pickerAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickerAdapter.notifyDataSetChanged();
    }

    public void setTextSelectView(TextView tvSelect) {
        this.tvSelect = tvSelect;
    }
}

