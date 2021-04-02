package com.godox.light;

import java.util.ArrayList;

public class CameraParamList {
    private  ArrayList<CameraParam> cameraParamList = new ArrayList<CameraParam>();

    public  static CameraParamList getInstance() {
        return CameraParamListHolder.cameraParamList;
    }

    private static class CameraParamListHolder {
        private static CameraParamList cameraParamList = new CameraParamList();
    }

     ArrayList<CameraParam> getCameraParamList(){
        return cameraParamList;
    }
}
