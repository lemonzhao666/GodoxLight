package com.godox.light;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.foundation.MeshService;
import com.zlm.base.PublicUtil;
import com.zlm.base.TelinkMeshApplication;

public class LightControl {
    private static final String TAG = "LightControl";

    static void sendFlashEvMeshMessage(byte currentEVSend, byte currentCCT, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = {(byte) 0xFA, currentEVSend, currentCCT, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        LogUtils.dTag(TAG, "currentDeviceMesh = " + currentDeviceMesh + "  sendFlashEvMeshMessage = " + PublicUtil.toHexString(bArr));
        MeshMessage meshMessage = createMessage(currentDeviceMesh, bArr);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendLightMeshMessage(byte currentDM, byte currentCCT, int currentDeviceMesh) {
        byte[] bArr = new byte[8];
        byte[] bArr2 = {(byte) 0xF0, currentDM, currentCCT, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        System.arraycopy(bArr2, 0, bArr, 0, 7);
        bArr[7] = (byte) PublicUtil.getCheckCode(bArr2);
        LogUtils.dTag(TAG, "currentDeviceMesh = " + currentDeviceMesh + "  sendLightMeshMessage = " + PublicUtil.toHexString(bArr));
        MeshMessage meshMessage = createMessage(currentDeviceMesh, bArr);
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
        LogUtils.dTag(TAG, "currentDeviceMesh = " + currentDeviceMesh + "  sendFlashMessage = " + PublicUtil.toHexString(bArr));
        MeshMessage meshMessage = createMessage(currentDeviceMesh, bArr);
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
        LogUtils.dTag(TAG, "currentDeviceMesh = " + currentDeviceMesh + "  sendLightStatusMessage = " + PublicUtil.toHexString(bArr));
        MeshMessage meshMessage = createMessage(currentDeviceMesh, bArr);
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
        LogUtils.dTag(TAG, "currentDeviceMesh = " + currentDeviceMesh + "  sendFlashStatusMessage = " + PublicUtil.toHexString(bArr));
        MeshMessage meshMessage = createMessage(currentDeviceMesh, bArr);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendSearchDeviceVersionMessage(int deviceMesh) {
        byte[] sendData = new byte[8];
        byte[] checkData = new byte[7];
        checkData[0] = (byte) 0xFD;
        checkData[1] = (byte) 0x02;
        checkData[2] = (byte) 0xFF;
        checkData[3] = (byte) 0xFF;
        checkData[4] = (byte) 0xFF;
        checkData[5] = (byte) 0xFF;
        checkData[6] = (byte) 0xFF;
        System.arraycopy(checkData, 0, sendData, 0, checkData.length);
        byte checksum = (byte) PublicUtil.getCheckCode(checkData);
        sendData[7] = checksum;
        Log.i("test", "data:" + PublicUtil.toHexString(sendData));
        MeshMessage meshMessage = createMessage(deviceMesh, sendData);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    static void sendSearchLightParamMessage(int deviceMesh) {
        byte[] sendData = new byte[8];
        byte[] checkData = new byte[7];
        checkData[0] = (byte) 0xFD;
        checkData[1] = (byte) 0x01;
        checkData[2] = (byte) 0xFF;
        checkData[3] = (byte) 0xFF;
        checkData[4] = (byte) 0xFF;
        checkData[5] = (byte) 0xFF;
        checkData[6] = (byte) 0x03;
        System.arraycopy(checkData, 0, sendData, 0, checkData.length);
        byte checksum = (byte) PublicUtil.getCheckCode(checkData);
        sendData[7] = checksum;
        Log.i("test", "data:" + PublicUtil.toHexString(sendData));
        MeshMessage meshMessage = createMessage(deviceMesh, sendData);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }


    private static MeshMessage createMessage(int address, byte[] params) {
        MeshMessage meshMessage = new MeshMessage();
        if (address == -1) {
            meshMessage.setDestinationAddress(0xFFFF);
        } else {
            meshMessage.setDestinationAddress(address);
        }
        meshMessage.setOpcode(0x0211F0);
        meshMessage.setParams(params);
        meshMessage.setResponseMax(0);
        meshMessage.setRetryCnt(0);
        meshMessage.setTtl(10);
        meshMessage.setTidPosition(11);
        return meshMessage;
    }

    static byte getEvsend(float ev) {
        byte evSend = 0;
        if (ev == 2) {
            evSend = 100;
        } else if (ev == 1.5f) {
            evSend = 90;
        } else if (ev == 1f) {
            evSend = 80;
        } else if (ev == 0.5f) {
            evSend = 70;
        } else if (ev == 0f) {
            evSend = 55;
        } else if (ev == -0.5f) {
            evSend = 40;
        } else if (ev == -1) {
            evSend = 30;
        } else if (ev == -1.5) {
            evSend = 20;
        } else if (ev == -2) {
            evSend = 0;
        }
        return evSend;
    }

    static float getEv(int evReceive) {
        if (evReceive == 0) {
            return -2f;
        } else if (evReceive >= 12.5 && evReceive < 25) {
            return -1.5f;
        } else if (evReceive >= 25 && evReceive < 37.5) {
            return -1f;
        } else if (evReceive >= 37.5 && evReceive < 50) {
            return -0.5f;
        } else if (evReceive >= 50 && evReceive < 62.5) {
            return 0;
        } else if (evReceive >= 62.5 && evReceive < 70) {
            return 0.5f;
        } else if (evReceive >= 75 && evReceive < 87.5) {
            return 1f;
        } else if (evReceive >= 87.5 && evReceive < 100) {
            return 1.5f;
        } else if (evReceive == 100) {
            return 2f;
        }
        return 0;
    }
}