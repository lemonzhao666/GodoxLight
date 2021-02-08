package com.zlm.phototable.api;

import android.content.Context;
import android.net.Uri;

public abstract class AbsPhotoTable {

    public interface PhotoTableListener {
        void onClickEditBtn(Uri uri);
        void exit();
    }

    public interface FinishListener {
        void finish();
    }

    public abstract void startPhotoTable(Context context);
    public abstract void finish();


    private static PhotoTableListener photoTableListener = null;
    private static FinishListener staticPhotoTableFinishListener = null;

    public void setEditBtnListener(PhotoTableListener photoTableListener){
        AbsPhotoTable.photoTableListener = photoTableListener;
    }

    public static PhotoTableListener getPhotoTableListener() {
        return photoTableListener;
    }

    public static void setPhotoTableFinishListener(FinishListener photoTableFinishListener) {
        staticPhotoTableFinishListener = photoTableFinishListener;
    }

    public static FinishListener getPhotoTableFinishListener() {
        return staticPhotoTableFinishListener;
    }
    public   abstract  void videoComplete(Context context);
}
