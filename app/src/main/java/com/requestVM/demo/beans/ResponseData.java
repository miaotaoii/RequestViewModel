package com.requestVM.demo.beans;

/**
 * @author:eric
 * @date:6/8/22
 */
public class ResponseData<T> {
    public T data;
    int code;
    String msg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

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
}
