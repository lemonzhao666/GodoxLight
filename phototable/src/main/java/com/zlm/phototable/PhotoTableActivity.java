package com.zlm.phototable;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zlm.base.BaseBackActivity;
import com.zlm.phototable.fragment.ImageFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotoTableActivity extends BaseBackActivity {

    private FragmentManager supportFragmentManager;
    private ImageFragment imageFragment;
    private RelativeLayout rlBottm;
    private TextView ivDelete;
    private PickerAdapter pickerAdapter;
    private List<FileItem> fileItemList;
    private TextView ivShare;


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


    public void showBottmDialog(@LayoutRes int resource) {
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
                pickerAdapter.setShowAllCb(false);
                pickerAdapter.concelSelect();
                tvRight.setText(R.string.select);
                rlBottm.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
                ivShare.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Map<Integer, Boolean> checkStatus = pickerAdapter.getCheckStatus();
                PhotoTableActivity photoTableActivity = PhotoTableActivity.this;
                photoTableActivity.fileItemList = photoTableActivity.pickerAdapter.getFileItemList();
                final ArrayList<FileItem> deleteList = new ArrayList<>();
                for (Map.Entry entry : checkStatus.entrySet()) {
                    if (((Boolean) entry.getValue()).booleanValue()) {
                        deleteList.add(fileItemList.get(((Integer) entry.getKey()).intValue()));
                    }
                }
                fileItemList.removeAll(deleteList);
                new Thread() {
                    public void run() {
                        super.run();
                        Iterator it = deleteList.iterator();
                        while (it.hasNext()) {
                            deleteFileItem((FileItem) it.next());
                        }
                        pickerAdapter.setFileItemList(fileItemList);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                pickerAdapter.setShowAllCb(false);
                                pickerAdapter.concelSelect();
                                rlBottm.setVisibility(View.GONE);
                                ivDelete.setVisibility(View.GONE);
                                ivShare.setVisibility(View.GONE);
                                ivDelete.setVisibility(View.GONE);
                                ivShare.setVisibility(View.GONE);
                                tvRight.setText(R.string.select);
                            }
                        });
                    }
                }.start();
            }
        });
    }

    @Override
    public void initData(Bundle bundle) {
        setFullWindow();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_phototable;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(getString(R.string.photo_gallery));
        setRightText(getString(R.string.select));
        setReturnIcon(R.mipmap.left);
        rlBottm = findViewById(R.id.rl_bottom);
        ivDelete = findViewById(R.id.iv_delete);
        ivShare = findViewById(R.id.iv_share);
    }

    @Override
    public void doBusiness() {
        supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        imageFragment = new ImageFragment();
        imageFragment.setTextSelectView(tvRight);
        fragmentTransaction.add(R.id.fl_container, imageFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void initListener() {
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerAdapter = imageFragment.getPickerAdapter();
                pickerAdapter.setDeleteShareView(ivShare, ivDelete,rlBottm);
                pickerAdapter.concelSelect();
                if (tvRight.getText().toString().equals(getString(R.string.select))) {
                    DataList.getInstance().setEnter(false);
                    tvRight.setText(R.string.concel);
                    pickerAdapter.setShowAllCb(true);
                } else {
                    DataList.getInstance().setEnter(true);
                    tvRight.setText(R.string.select);
                    pickerAdapter.setShowAllCb(false);
                    rlBottm.setVisibility(View.GONE);
                }
            }
        });
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottmDialog(R.layout.bottm_item);
            }

        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Integer, Boolean> checkStatus = pickerAdapter.getCheckStatus();
                PhotoTableActivity photoTableActivity = PhotoTableActivity.this;
                photoTableActivity.fileItemList = photoTableActivity.pickerAdapter.getFileItemList();
                ArrayList<Uri> shareList = new ArrayList<>();
                int imageNum = 0;
                int videoNum = 0;
                for (Map.Entry entry : checkStatus.entrySet()) {
                    if (((Boolean) entry.getValue()).booleanValue()) {
                        FileItem shareItem = fileItemList.get(((Integer) entry.getKey()).intValue());
                        if (shareItem.getType() == FileItem.Type.Image) {
                            imageNum++;
                        } else {
                            videoNum++;
                        }
                        shareList.add(Util.getContentUri(mActivity,shareItem));
                    }
                }
                if (imageNum <= 0 || videoNum <= 0) {
                    String str = getString(R.string.share);
                    String str2 = "android.intent.extra.STREAM";
                    if (imageNum > 0 && imageNum <= 8) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
                        shareIntent.putParcelableArrayListExtra(str2, shareList);
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, str));
                    } else if (imageNum > 8) {
                        Toast.makeText(PhotoTableActivity.this, R.string.share_too_mush, Toast.LENGTH_SHORT).show();
                    } else if (videoNum > 1)
                        Toast.makeText(PhotoTableActivity.this, R.string.sharesingle, Toast.LENGTH_SHORT).show();
                    else {
                        Intent shareIntent2 = new Intent();
                        shareIntent2.setAction("android.intent.action.SEND");
                        shareIntent2.putExtra(str2, shareList.get(0));
                        shareIntent2.setType("video/*");
                        startActivity(Intent.createChooser(shareIntent2, str));
                    }
                } else {
                    Toast.makeText(PhotoTableActivity.this, R.string.only_one_share, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
