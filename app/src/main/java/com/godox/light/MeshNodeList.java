package com.godox.light;

import com.zlm.base.model.NodeInfo;

import java.util.ArrayList;

public class MeshNodeList {
    private ArrayList<NodeInfo> meshNodeList;
    private int currentDeviceMesh = -1;
    public  static MeshNodeList getInstance() {
        return MeshNodeListHolder.meshNodeList;
    }

    private static class MeshNodeListHolder {
        private static MeshNodeList meshNodeList = new MeshNodeList();
    }

    public void setMeshNodeList(ArrayList<NodeInfo> meshNodeList) {
        this.meshNodeList = meshNodeList;
    }

    ArrayList<NodeInfo> getMeshNodeList(){
        return meshNodeList;
    }

    public int getCurrentDeviceMesh() {
        return currentDeviceMesh;
    }

    public void setCurrentDeviceMesh(int currentDeviceMesh) {
        this.currentDeviceMesh = currentDeviceMesh;
    }
}
