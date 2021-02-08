package com.zlm.phototable;

import java.util.ArrayList;

public class DataList {
    private ArrayList<FileItem> allList = new ArrayList<>();
    private ArrayList<FileItem> imageList = new ArrayList<>();
    private boolean isEnter = true;
    private ArrayList<FileItem> videoList = new ArrayList<>();

    private static class DataListHolder {
        public static DataList dataList = new DataList();
    }

    public static DataList getInstance() {
        return DataListHolder.dataList;
    }

    public ArrayList<FileItem> getImageList() {
        return this.imageList;
    }

    public void setImageList(ArrayList<FileItem> imageList2) {
        this.imageList = imageList2;
    }

    public ArrayList<FileItem> getVideoList() {
        return this.videoList;
    }

    public void setVideoList(ArrayList<FileItem> videoList2) {
        this.videoList = videoList2;
    }

    public ArrayList<FileItem> getAllList() {
        return this.allList;
    }

    public void setAllList(ArrayList<FileItem> allList2) {
        this.allList = allList2;
    }

    public boolean isEnter() {
        return this.isEnter;
    }

    public void setEnter(boolean enter) {
        this.isEnter = enter;
    }

}
