package com.godox.light;

import com.blankj.utilcode.util.LogUtils;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.foundation.MeshService;
import com.zlm.base.PublicUtil;

public class LightControl {
    private static final String TAG = "LightControl";
    static void sendFlashEvMeshMessage(byte currentEVSend, byte currentCCT, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = {(byte) 0xFA, currentEVSend, currentCCT, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
//        LogUtils.dTag(TAG, "send = " + PublicUtil.toHexString(bArr) + " currentDM = " + currentDM + " currentCCT = " + currentCCT);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(currentDeviceMesh);
        meshMessage.setOpcode(135664);
        meshMessage.setParams(bArr);
        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendLightMeshMessage(byte currentDM, byte currentCCT, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = {(byte) 0xF0, currentDM, currentCCT, 50, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        LogUtils.dTag(TAG, "send = " + PublicUtil.toHexString(bArr) + " currentDM = " + currentDM + " currentCCT = " + currentCCT);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(currentDeviceMesh);
        meshMessage.setOpcode(135664);
        meshMessage.setParams(bArr);
        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }


    static void sendFlashMessage(int status, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = new byte[7];
        if (status == 0) {
            bArr2 = new byte[]{(byte) 0xDE, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        } else if (status == 1) {
            bArr2 = new byte[]{(byte) 0xDE, (byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        }
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(currentDeviceMesh);
        meshMessage.setOpcode(135664);
        meshMessage.setParams(bArr);
        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendLightStatusMessage(int status, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = new byte[7];
        if (status == 0) {//开灯
            bArr2 = new byte[]{(byte) 0xFE, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        } else if (status == 1) {
            bArr2 = new byte[]{(byte) 0xFE, (byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        }
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(currentDeviceMesh);
        meshMessage.setOpcode(135664);
        meshMessage.setParams(bArr);

        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendFlashStatusMessage(int status, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = new byte[7];
        if (status == 0) {
            bArr2 = new byte[]{(byte) 0xFE, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        } else if (status == 1) {
            bArr2 = new byte[]{(byte) 0xFE, (byte) 0xFF, (byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        }
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(currentDeviceMesh);
        meshMessage.setOpcode(135664);
        meshMessage.setParams(bArr);
        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }
}
