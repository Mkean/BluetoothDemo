package com.mkoyu.bluetoothdemo;

import java.io.Serializable;

public class ActionInfo implements Serializable {

    /**
     * 操作码 1 扫描操作 2 停止扫描 3 连接操作 4 断开所有连接 5 测温度 6 测振动
     */
    private int code;

    /**
     * 需要连接的设备的mac地址，或者需要测量的设备的mac地址
     */
    private String mac;

    public ActionInfo() {
    }

    public ActionInfo(int code, String mac) {
        this.code = code;
        this.mac = mac;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
