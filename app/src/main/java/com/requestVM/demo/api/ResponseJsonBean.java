/**
 * Copyright 2022 bejson.com
 */
package com.requestVM.demo.api;

import com.requestVM.demo.beans.PriceBean;

import java.util.List;

/**
 * Auto-generated: 2022-06-04 12:53:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ResponseJsonBean {

    private int code;
    private String msg;
    private List<PriceBean> newslist;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setNewslist(List<PriceBean> newslist) {
        this.newslist = newslist;
    }

    public List<PriceBean> getNewslist() {
        return newslist;
    }

}