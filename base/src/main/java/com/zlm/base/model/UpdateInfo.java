package com.zlm.base.model;

public class UpdateInfo {

    /**
     * btFirmwareVersion : 000006
     * btFirmwareUrl : http://www.egodox.com/apps/update/ios/rf_led_flash/file/LK8620_MESH_GDFLASH_V05_20210325.bin
     * isOpen : 1
     * isOpenDesc : 0
     * descInfo : 1.鏉ヨ嚜鍚庡彴鏇存柊淇℃伅xxxxxxxxxx
     2.鎯婁笉鎯婂枩锛屾剰涓嶆剰澶朶n3.杩欐槸鎴戜滑婊℃弧鐨勮瘹鎰忥紝璇锋帴涓嬫垜浠殑鑶濈洊
     */

    private String btFirmwareVersion;
    private String btFirmwareUrl;
    private int isOpen;
    private int isOpenDesc;
    private String descInfo;

    public String getBtFirmwareVersion() {
        return btFirmwareVersion;
    }

    public void setBtFirmwareVersion(String btFirmwareVersion) {
        this.btFirmwareVersion = btFirmwareVersion;
    }

    public String getBtFirmwareUrl() {
        return btFirmwareUrl;
    }

    public void setBtFirmwareUrl(String btFirmwareUrl) {
        this.btFirmwareUrl = btFirmwareUrl;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getIsOpenDesc() {
        return isOpenDesc;
    }

    public void setIsOpenDesc(int isOpenDesc) {
        this.isOpenDesc = isOpenDesc;
    }

    public String getDescInfo() {
        return descInfo;
    }

    public void setDescInfo(String descInfo) {
        this.descInfo = descInfo;
    }
}
