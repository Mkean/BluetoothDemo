package com.mkoyu.bluetoothdemo;

import java.io.Serializable;

public class ActionResult implements Serializable {
    // 0 成功 其他失败
    private int code;
    private String msg;
    private String result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
