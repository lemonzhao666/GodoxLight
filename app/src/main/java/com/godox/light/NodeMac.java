package com.godox.light;

import java.util.HashSet;

public class NodeMac {
    static HashSet<String> getInstance(){
        return NodeInfoListHolder.nodeMac;
    }
    static class NodeInfoListHolder{
        static HashSet<String> nodeMac = new HashSet<String>();
    }


}
