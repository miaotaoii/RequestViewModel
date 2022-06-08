package com.ocode.requestvm.request;

import androidx.annotation.NonNull;

import com.ocode.requestvm.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;


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

    private RequestObj<T> requestObj;

    public void setRequestObj(RequestObj<T> requestObj) {
        this.requestObj = requestObj;
    }

    private boolean checkArgs(Method method, Object[] args) {
        Class<?>[] paraCls = method.getParameterTypes();
        if (args == null || args.length == 0) {//没有请求参数
            return paraCls == null || paraCls.length == 0;
        } else {
            if (paraCls.length != args.length) return false;
            //挨个检查请求参数的类型 如果都一致及就返回true
            for (int i = 0; i < paraCls.length; i++) {
                if (paraCls[i] != args[i].getClass()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Method getMethodInApi(String apiAnnotation) {
        Method[] methods = dataApi.getClass().getInterfaces()[0].getDeclaredMethods();
        ArrayList<Method> sameAnnotationMethod = new ArrayList<>();
        for (Method m :
                methods) {
            if (checkMethodAnnotation(apiAnnotation, m)) {
                sameAnnotationMethod.add(m);
            }
        }
        if (sameAnnotationMethod.size() == 0) return null;
        if (sameAnnotationMethod.size() == 1) return sameAnnotationMethod.get(0);

        //具有多个相同注解的api接口方法时，检查参数和返回值类型
        Method[] methodsArr = new Method[sameAnnotationMethod.size()];
        return checkMethodArgsAndReturnType(requestObj.getReturnClsType(), requestObj.getArgsInternal(), (Method[]) sameAnnotationMethod.toArray(methodsArr));
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
            } else if (a instanceof OPTIONS) {
                return ((OPTIONS) a).value().equals(anno);
            } else if (a instanceof PATCH) {
                return ((PATCH) a).value().equals(anno);
            } else if (a instanceof HEAD) {
                return ((HEAD) a).value().equals(anno);
            }
        }
        return false;
    }


    private Method checkMethodArgsAndReturnType(Type returnCls, Object[] args, Method[] methods) {
        for (Method m :
                methods) {
            Type type = m.getGenericReturnType();
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getActualTypeArguments()[0].equals(returnCls)) {
                    if (checkArgs(m, args)) {
                        return m;
                    }
                }
            } else {
                if (type.equals(returnCls)) {
                    if (checkArgs(m, args)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    public void request() {
        Type[] types = new Type[]{requestObj.getReturnClsType(), dataApiClass};
        Logger.logI("try create request with requestObj:" + requestObj.toString());
        request(types, callBack, requestObj.getRequestKey(), requestObj.getArgsInternal());
    }


    public static class HandleResponseCallBack<T> implements Callback<T> {
        private OnDataLoaded<T> callBack;
        private TypedRequestImpl typedRequest;
        private volatile boolean hasOwnerDestroyed = false;

        public HandleResponseCallBack(OnDataLoaded<T> callBack, TypedRequestImpl typedRequest) {
            this.callBack = callBack;
            this.typedRequest = typedRequest;
        }

        public void onLifecycleOwnerDestroyed() {
            hasOwnerDestroyed = true;
            typedRequest.removeResponseHandler(this);
            callBack = null;
            this.typedRequest = null;
        }

        public void checkOwnerState() throws OwnerDestroyedException {
            if (hasOwnerDestroyed) {
                Logger.logI("owner has destroyed!! ");
                throw new OwnerDestroyedException("owner has destroyed");
            }
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            try {
                checkOwnerState();
                if (!response.isSuccessful()) {
                    //网络请求失败的处理
                    Logger.logI("TypedRequest[" + this + "]HandleResponseCallBack onResponse false " + response.code());
                    Logger.logI("TypedRequest[" + this + "]msg =  " + response.message());
                    if (callBack == null) return;
                    callBack.onLoadFailed(response.code(), response.message());
                    return;
                }
                //数据响应的解析

                T body = response.body();
                int code = response.code();
                Logger.logI("TypedRequest[" + this + " ]onResponse call return success code=" + code);
                switch (code) {
                    case 200://有数据
                        if (callBack == null) return;
                        callBack.onLoadSuccess(body);
                        break;
                    default://数据有异常
                        if (callBack == null) return;
                        callBack.onLoadFailed(response.code(), response.message());
                        break;
                }
            } catch (OwnerDestroyedException e) {
                Logger.logI(e.getMessage());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Logger.logE("TypedRequest[" + this + "]HandleResponseCallBack onFailure call " + t.getLocalizedMessage());
            try {
                checkOwnerState();
                if (callBack == null) return;
                callBack.onLoadFailed(0, t.getLocalizedMessage());
            } catch (OwnerDestroyedException e) {
                Logger.logI(e.getMessage());
            }
        }
    }

    private void request(Type[] types, @NonNull OnDataLoaded<T> callback, String apiAnnotation, Object... args) {
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

    private int requestingCount;

    //释放这个请求对象
    public void onDestroyed() {
        Logger.logI("TypedRequest["+this+"]onDestroyed");
        cancelAllCalling();
        destroyAllHandler();
    }

    void destroyAllHandler() {
        for (HandleResponseCallBack<T> handler :
                callBacks) {
            handler.onLifecycleOwnerDestroyed();
        }
    }

    //todo 取消所有未完成的call
    void cancelAllCalling() {

    }

    private CopyOnWriteArrayList<HandleResponseCallBack<T>> callBacks = new CopyOnWriteArrayList<>();

    public void removeResponseHandler(HandleResponseCallBack handler) {
        callBacks.remove(handler);
    }

    private void invokeApiMethod(Method method, @NonNull OnDataLoaded<T> callback, Object... args) {
        callBack = callback;
        Call<T> call = null;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object arg : args) {
                stringBuilder.append(arg.toString() + ",");
            }
            call = (Call<T>) method.invoke(dataApi, args);
            HandleResponseCallBack<T> handleResponseCallBack = new HandleResponseCallBack<>(callBack, this);
            callBacks.add(handleResponseCallBack);
            call.enqueue(handleResponseCallBack);
//            new Thread(new TestTask<T>(handleResponseCallBack)).start();

            Logger.logI("TypedRequest[" + this + "] ------>[" + dataApiClass + "]" + "  (" + method.toGenericString() + ")" + " args{" + stringBuilder.toString() + "}");
        } catch (Exception e) {
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
