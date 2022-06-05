package com.ocode.requestvm.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T> api 接口的返回数据类型
 * @param <S> livedata的数据类型
 * @author:eric
 * @date:6/2/22
 */
abstract public class RequestObj<T, S> extends Request<T> {

    private Object[] args;
    private String apiAnnotation;

    public void setApiAnnotation(String apiAnnotation) {
        this.apiAnnotation = apiAnnotation;
    }

    public RequestObj(String apiAnnotation) {
        this.apiAnnotation = apiAnnotation;
    }

    public Type getReturnClsType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    abstract public Object[] getArgs();

    public Object[] getArgsInternal() {
        if (args != null) return args;
        return getArgs();
    }


    public String getRequestKey() {
        return apiAnnotation;
    }

    @Deprecated
    public String getRequestKey_() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getReturnClsType().getClass().getCanonicalName());
        if (args != null && args.length > 0) {
            for (Object arg :
                    args) {
                stringBuilder.append("-" + arg.getClass().getCanonicalName());
            }
        }
        return stringBuilder.toString();
    }
}
