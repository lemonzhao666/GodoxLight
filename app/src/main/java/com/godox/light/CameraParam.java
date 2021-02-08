package com.godox.light;

import android.util.Range;
import android.util.Size;


class CameraParam {
    private int facing;
    private HardwareLevel supportedHardwareLevel;
    private int sensorOrientation;
    private String id;
    private Size screenSize;
    private Size[] previewSizes;
    private Size[] pictureSizes;

    private Size max4To3PicSize;
    private Size max16To9PicSize;
    private Size max1To1PicSize;

    private Size max4To3PreviewSize;
    private Size max16To9PreviewSize;
    private Size max1To1PreviewSize;
    private Range<Integer> sensitivityRange;
    private Range<Long> exposureTimeRange;
    private Range<Integer> aeCompensationRange;
    private Size pixelArraySize;
    private boolean isHW980;

    public Range<Long> getExposureTimeRange() {
        return exposureTimeRange;
    }

    public void setExposureTimeRange(Range<Long> exposureTimeRange) {
        this.exposureTimeRange = exposureTimeRange;
    }

    public Range<Integer> getAeCompensationRange() {
        return aeCompensationRange;
    }

    public void setAeCompensationRange(Range<Integer> aeCompensationRange) {
        this.aeCompensationRange = aeCompensationRange;
    }

    public Range<Integer> getSensitivityRange() {
        return sensitivityRange;
    }

    public void setSensitivityRange(Range<Integer> sensitivityRange) {
        this.sensitivityRange = sensitivityRange;
    }

    public static CameraParam getInstance() {
        return BackCameraParamHolder.backCameraParam;
    }

    private static class BackCameraParamHolder {
        private static CameraParam backCameraParam = new CameraParam();
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public Size getPixelArraySize() {
        return pixelArraySize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPixelArraySize(Size pixelArraySize) {
        this.pixelArraySize = pixelArraySize;
    }

    public void setSupportedHardwareLevel(int hardwareLevel) {
        switch (hardwareLevel) {
            case 0:
                supportedHardwareLevel = HardwareLevel.LEGACY;
                break;
            case 1:
                supportedHardwareLevel = HardwareLevel.LIMITED;
                break;
            case 2:
                supportedHardwareLevel = HardwareLevel.FULL;
                break;
            case 3:
                supportedHardwareLevel = HardwareLevel.LEVEL_3;
                break;
        }
    }

    public void setSensorOrientation(int sensorOrientation) {
        this.sensorOrientation = sensorOrientation;
    }

    public void setDeviceSize(Size deviceSize) {
        this.screenSize = deviceSize;
    }

    public void setPreviewSizes(Size[] previewSizes) {
        this.previewSizes = previewSizes;
    }

    public void setPictureSizes(Size[] pictureSizes) {
        this.pictureSizes = pictureSizes;
    }

    public String toControlParamString() {
        return "CameraParam{" +
                "aeCompensationRange=" + aeCompensationRange +
                ", sensitivityRange=" + sensitivityRange +
                ", exposureTimeRange=" + exposureTimeRange +
                '}';
    }

    @Override
    public String toString() {
        return "CameraParam{" +
                "facing=" + facing +
                ", supportedHardwareLevel=" + supportedHardwareLevel +
                ", sensorOrientation=" + sensorOrientation +
                ", deviceSize=" + screenSize.toString() +
                ", previewSizes=" + toSString(previewSizes);
    }


    public String toSString() {
        return ",pictureSizes=" + toSString(pictureSizes) +
                '}';
    }


    public String toSString(Size[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            int ad = getGCD(a[i].getWidth(), a[i].getHeight());
            String add = a[i] + "  =  " + (a[i].getWidth() / ad) + " : " + (a[i].getHeight() / ad);
            b.append(add);
            b.append("\n");
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public int getGCD(int num1, int num2) {
        // 先获得绝对值，保证负数也可以求
        num1 = Math.abs(num1);
        num2 = Math.abs(num2);
        // 找到小的那个数
        int min = num1 > num2 ? num2 : num1;
        // 初始最大公约数为 1
        int gcd = 1;
        // 穷举, 直接从 2 开始
        for (int i = 2; i <= min; i++) {
            // 如果 i 能被两个数同时约分，则是它们的公约数，但不一定是最大的
            if (num1 % i == 0 && num2 % i == 0) {
                // gcd 从最小的公约数，一直到最大的公约数
                gcd = i;
            }
        } // for
        return gcd;
    }

    public Size getMax4To3PicSize() {
        return max4To3PicSize;
    }

    public void setMax4To3PicSize(Size max4To3PicSize) {
        this.max4To3PicSize = max4To3PicSize;
    }

    public Size getMax16To9PicSize() {
        return max16To9PicSize;
    }

    public void setMax16To9PicSize(Size max16To9PicSize) {
        this.max16To9PicSize = max16To9PicSize;
    }

    public Size getMax1To1PicSize() {
        return max1To1PicSize;
    }

    public void setMax1To1PicSize(Size max1To1PicSize) {
        this.max1To1PicSize = max1To1PicSize;
    }

    public Size getMax4To3PreviewSize() {
        return max4To3PreviewSize;
    }

    public void setMax4To3PreviewSize(Size max4To3PreviewSize) {
        this.max4To3PreviewSize = max4To3PreviewSize;
    }

    public Size getMax16To9PreviewSize() {
        return max16To9PreviewSize;
    }

    public void setMax16To9PreviewSize(Size max16To9PreviewSize) {
        this.max16To9PreviewSize = max16To9PreviewSize;
    }

    public Size getMax1To1PreviewSize() {
        return max1To1PreviewSize;
    }

    public void setMax1To1PreviewSize(Size max1To1PreviewSize) {
        this.max1To1PreviewSize = max1To1PreviewSize;
    }

    public boolean isHW980() {
        return isHW980;
    }

    public void setHW980(boolean HW980) {
        isHW980 = HW980;
    }
}
