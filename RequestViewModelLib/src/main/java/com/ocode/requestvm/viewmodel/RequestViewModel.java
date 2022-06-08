package com.ocode.requestvm.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.ocode.requestvm.request.RequestObj;
import com.ocode.requestvm.request.TypedRequestImpl;
import com.ocode.requestvm.util.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author:eric
 * @date:6/2/22
 */
public class RequestViewModel extends ViewModel implements LifecycleEventObserver {
    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            for (String key : map.keySet()) {
                RequestDataWrapper wrapper = map.get(key);
                wrapper.getTypedRequest().onDestroyed();
            }
        }
    }

    private HashMap<String, RequestDataWrapper<?>> map = new HashMap<>();

    /**
     * 保存每个api请求的 livedata和request对象的结构
     *
     * @param <T> :viewmodel的数据类型
     *            //     * @param <S> api请求返回的数据类型
     *            //     * @param <U> retrofit 声明api接口的所在类型
     */
    static class RequestDataWrapper<T> {
        private RequestLiveData<?, T> requestLiveData;
        private TypedRequestImpl typedRequest;

        public RequestLiveData<?, T> getRequestLiveData() {
            return requestLiveData;
        }

        public void setRequestLiveData(RequestLiveData<?, T> requestLiveData) {
            this.requestLiveData = requestLiveData;
        }

        public void setTypedRequest(TypedRequestImpl typedRequest) {
            this.typedRequest = typedRequest;
        }

        public TypedRequestImpl getTypedRequest() {
            return typedRequest;
        }
    }

    /**
     * @param <S> api请求返回的数据类型
     * @param <T> livedata的数据类型
     * @param <V> RequestLiveData的类型
     */
    public <S, T, V extends RequestLiveData<S, T>> V getRequestLiveData(RequestObj<S> requestObj, Class<V> liveDataCls) {
        String key = requestObj.getRequestKey();
        RequestLiveData<S, T> liveData;
        if (!map.containsKey(key)) {
            try {
                liveData = liveDataCls.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return null;
            }
            liveData.setRequestViewModel(this);
            liveData.setRequestKey(key);
            liveData.setRequestObj(requestObj);

            TypedRequestImpl<S, ?> typedRequest = new TypedRequestImpl<>();
            typedRequest.setRequestObj(requestObj);
            typedRequest.setDataApiClass(dataApi);
            typedRequest.setCallBack(liveData);

            RequestDataWrapper<T> requestData = new RequestDataWrapper<>();
            requestData.setRequestLiveData(liveData);
            requestData.setTypedRequest(typedRequest);
            Logger.logI("create and save LiveData for key " + key);
            map.put(key, requestData);

            liveData.refresh();
        } else {
            liveData = (RequestLiveData<S, T>) map.get(key).getRequestLiveData();
        }
        return (V) liveData;
    }

    public <T> void request(RequestLiveData<?, T> liveData) {
        map.get(liveData.getRequestKey()).getTypedRequest().request();
    }


    private Class<?> dataApi;

    public void setDataApi(Class<?> dataApi) {
        this.dataApi = dataApi;
    }

    private Type getDataApiType() {
        return dataApi;
    }
}
