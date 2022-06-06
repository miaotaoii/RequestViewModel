package com.ocode.requestvm.request;

import androidx.annotation.NonNull;

import com.ocode.requestvm.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


/**
 * @param <T> api接口返回的数据类型
 * @param <U> retrofit api声明接口所在的类型
 * @author:eric
 * @date:6/1/22
 */
public class TypedRequestImpl<T, U> extends TypedRequest<T, U> {
    private OnDataLoaded<T> callBack;
    private U dataApi;
    private Class<U> dataApiClass;

    public void setCallBack(OnDataLoaded<T> callBack) {
        this.callBack = callBack;
    }

    public void setDataApiClass(Class<?> dataApiClass) {
        this.dataApiClass = (Class<U>) dataApiClass;
    }

    private RequestObj<T, ?> requestObj;

    public void setRequestObj(RequestObj<T, ?> requestObj) {
        this.requestObj = requestObj;
    }


    private Method getMethodInApi(String apiAnnotation) {
        Method[] methods = dataApi.getClass().getInterfaces()[0].getDeclaredMethods();
        for (Method m :
                methods) {
            if (checkMethodAnnotation(apiAnnotation, m)) {
                return m;
            }
        }
        return null;
    }

    private boolean checkMethodAnnotation(String anno, Method method) {
        for (Annotation a :
                method.getAnnotations()) {
            if (a instanceof PUT) {
                return ((PUT) a).value().equals(anno);
            } else if (a instanceof GET) {
                return ((GET) a).value().equals(anno);
            } else if (a instanceof POST) {
                return ((POST) a).value().equals(anno);
            } else if (a instanceof DELETE) {
                return ((DELETE) a).value().equals(anno);
            } else if (a instanceof Query) {
                return ((Query) a).value().equals(anno);
            }
        }
        return false;
    }

    public void request() {
        Type[] types = new Type[]{requestObj.getReturnClsType(), dataApiClass};
        request(types, callBack, requestObj.getRequestKey(), requestObj.getArgsInternal());
    }

    private class HandleResponseCallBack implements Callback<T> {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!response.isSuccessful()) {
                //网络请求失败的处理
                Logger.logI("TypedRequest[" + TypedRequestImpl.this + "]HandleResponseCallBack onResponse false " + response.code());
                Logger.logI("TypedRequest[" + TypedRequestImpl.this + "]msg =  " + response.message());
                callBack.onLoadFailed(response.code(), response.message());
                return;
            }
            //数据响应的解析

            T body = response.body();
            int code = response.code();
            Logger.logI("TypedRequest[" + TypedRequestImpl.this + " ]onResponse call return success code=" + code);
            switch (code) {
                case 200://有数据
                    callBack.onLoadSuccess(body);
                    break;
                default://数据有异常
                    callBack.onLoadFailed(response.code(), response.message());
                    break;
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Logger.logE("TypedRequest[" +  TypedRequestImpl.this + "]HandleResponseCallBack onFailure call " + t.getLocalizedMessage());

            callBack.onLoadFailed(0, t.getLocalizedMessage());
        }
    }

    public void request(Type[] types, @NonNull OnDataLoaded<T> callback, String apiAnnotation, Object... args) {
        this.callBack = callback;
        initApiClass((Class<U>) types[1]);
        Method method = getMethodInApi(apiAnnotation);
        if (method == null) {
            Logger.logE("can not find api method ,please check retrofit api return type and args");
            callback.onLoadFailed(0, "can not find api method");
            return;
        }
        invokeApiMethod(method, callback, args);
    }

    private void invokeApiMethod(Method method, @NonNull OnDataLoaded<T> callback, Object... args) {
        callBack = callback;
        Call<T> call = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object arg : args) {
                stringBuilder.append(arg + ",");
            }
            call = (Call<T>) method.invoke(dataApi, args);
            call.enqueue(new HandleResponseCallBack());

            Logger.logI("TypedRequest[" + this + "] ------>[" + dataApiClass + "]" + "(" + method.getName() + ")" + " args{" + stringBuilder.toString() + "}");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Logger.logE("invoke api method err! \n" + e.getLocalizedMessage());

            callBack.onLoadFailed(0, "invoke api method err! \n" + e.getLocalizedMessage());
        }
    }

    private void initApiClass(Class<U> serviceType) {
        if (dataApi == null) {
            dataApi = RetrofitConfig.getInstance().getService(serviceType);

            if (dataApi == null) {
                Logger.logE("can not find api  type:" + serviceType);
                callBack.onLoadFailed(0, "can not serviceType api method");
                return;
            }
        }
    }


}
