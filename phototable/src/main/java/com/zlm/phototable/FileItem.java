package com.zlm.phototable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.NumberFormat;

public class FileItem implements  Comparable<FileItem>, Serializable {

    private String mFilePath;
    private String mFileName;
    private String mSize;
    private long mDuration;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public enum Type {
        Image, Video
    }
    private String mDate;

    private Type mType;

    public FileItem(@NonNull String path, String name, String size, String date, long duration) {
        this.mFilePath = path;
        this.mFileName = name;
        this.mSize = size;
        this.mDate = date;
        this.mDuration = duration;
    }

    public FileItem(){}
    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public String getFileSize() {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);

        String sizeDisplay;
        long size = Long.valueOf(mSize);
        if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + "M";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + "K";
        } else {
            sizeDisplay = ddf1.format(size) + "B";
        }
        return sizeDisplay;
    }

    public long getLongFileSize() {
        return Long.valueOf(mSize);
    }

    public String getDate() {
        return mDate;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    @Override
    public int compareTo(@NonNull FileItem fileItem) {
        return (int) (Long.valueOf(fileItem.getDate()) - Long.valueOf(mDate));
    }
}

