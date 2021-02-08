package com.godox.light;

import com.zlm.base.model.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class NodeList {
    private static List<NodeInfo> nodeList = new ArrayList<NodeInfo>();
    public static NodeList getInstance(){
        return NodeListHolder.nodeList;
    }
   private static class  NodeListHolder{
         static NodeList nodeList = new NodeList();
    }
    public List<NodeInfo> getNodeList(){
        return nodeList;
    }
}
