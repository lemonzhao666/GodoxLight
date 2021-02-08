package com.zlm.phototable.api;

import android.content.Context;
import android.content.Intent;

import com.zlm.phototable.Event;
import com.zlm.phototable.PhotoTableActivity;
import org.greenrobot.eventbus.EventBus;

public class PhotoTable extends AbsPhotoTable {
    @Override
    public  void startPhotoTable(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoTableActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void finish() {

    }

    @Override
    public void videoComplete(Context context) {
        Intent intent = new Intent(context, PhotoTableActivity.class);
        EventBus.getDefault().postSticky(new Event.CompleteVideo(true));
        context.startActivity(intent);
    }


}
