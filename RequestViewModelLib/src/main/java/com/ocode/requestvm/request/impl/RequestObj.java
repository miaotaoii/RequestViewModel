package com.ocode.requestvm.request.impl;

import android.text.TextUtils;

import com.ocode.requestvm.request.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @param <T> api 接口的返回数据类型
 * @author:eric
 * @date:6/2/22
 */
abstract public class RequestObj<T> extends Request<T> {

    private Object[] args;
    private String apiAnnotation;

    public void setApiAnnotation(String apiAnnotation) {
        this.apiAnnotation = apiAnnotation;
    }

    public RequestObj(String apiAnnotation) {
        this.apiAnnotation = apiAnnotation;
    }

    public RequestObj(String apiAnnotation, String requestKey) {
        this.apiAnnotation = apiAnnotation;
        this.requestKey = requestKey;
    }

    public Type getReturnClsType() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return type;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    abstract public Object[] getArgs();

    public Object[] getArgsInternal() {
        if (args != null) return args;
        return getArgs();
    }

    private String requestKey;

    public String getApiAnnotation() {
        return apiAnnotation;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getRequestKey() {
        if (TextUtils.isEmpty(requestKey)) {
            return getApiAnnotation();
        }
        return requestKey;
    }

    @Override
    public String toString() {
        return "RequestObj{" +
                "args=" + Arrays.toString(getArgsInternal()) +
                ", apiAnnotation='" + apiAnnotation + '\'' +
                ", ReturnClsType='" + getReturnClsType() + '\'' +
                '}';
    }
}
