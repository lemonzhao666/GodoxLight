package com.godox.light;

import com.zlm.base.TelinkMeshApplication;
import com.zlm.base.model.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class MeshDevice {
     static  ArrayList<NodeInfo> getOnLineNodeList() {
         ArrayList<NodeInfo> onLineNodeList = new ArrayList<>();
        List<NodeInfo> nodes = TelinkMeshApplication.getInstance().getMeshInfo().nodes;
        for (NodeInfo nodeInfo : nodes) {
            if (nodeInfo.getOnOff() == 1) {
                String meshAdress = "0x" + Integer.toHexString(nodeInfo.meshAddress);
                if (meshAdress.length() == 3) {
                    meshAdress = "0x0" + Integer.toHexString(nodeInfo.meshAddress);
                }
                int len = nodeInfo.macAddress.length();
                String name = "GDFLASH_" + nodeInfo.macAddress.substring(len - 5, len - 3) + nodeInfo.macAddress.substring(len - 2, len);
                NodeInfo node = new NodeInfo();
                node.name = name;
                node.meshAddressHex = meshAdress;
                onLineNodeList.add(node);
            }
            return onLineNodeList;
        }
        return null;
    }
}
