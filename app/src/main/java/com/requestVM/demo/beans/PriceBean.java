/**
 * Copyright 2022 bejson.com
 */
package com.requestVM.demo.beans;

import java.util.Date;

/**
 * Auto-generated: 2022-06-04 12:53:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class PriceBean {

    private String prov;
    private String p0;
    private String p89;
    private String p92;
    private String p95;
    private String p98;
    private String time;
    private int code;
    private String msg;

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

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getProv() {
        return prov;
    }

    public void setP0(String p0) {
        this.p0 = p0;
    }

    public String getP0() {
        return p0;
    }

    public void setP89(String p89) {
        this.p89 = p89;
    }

    public String getP89() {
        return p89;
    }

    public void setP92(String p92) {
        this.p92 = p92;
    }

    public String getP92() {
        return p92;
    }

    public void setP95(String p95) {
        this.p95 = p95;
    }

    public String getP95() {
        return p95;
    }

    public void setP98(String p98) {
        this.p98 = p98;
    }

    public String getP98() {
        return p98;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "PriceBean{" +
                "prov='" + prov + '\'' +
                ", p0='" + p0 + '\'' +
                ", p89='" + p89 + '\'' +
                ", p92='" + p92 + '\'' +
                ", p95='" + p95 + '\'' +
                ", p98='" + p98 + '\'' +
                ", time=" + time +
                '}';
    }
}