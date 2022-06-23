package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.ViewModel;

import com.ocode.requestvm.request.impl.RequestObj;
import com.ocode.requestvm.request.impl.TypedRequestImpl;
import com.ocode.requestvm.util.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author:eric
 * @date:6/2/22
 */
public class RequestViewModel extends ViewModel {
    @Override
    protected void onCleared() {
        super.onCleared();
        //  考虑屏幕旋转时 此处的处理，旋转时viewmodel 不会被销毁，livedata不应该被销毁
        //在activity彻底脱离LifeCycle声明周期时，才会执行onCleared ，解除对ViewModel的引用
        for (String key : map.keySet()) {
            RequestDataWrapper wrapper = map.get(key);
            if (wrapper != null) {
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
     * @param <S>              api请求返回的数据类型
     * @param <T>              livedata的数据类型
     * @param <V>              RequestLiveData的类型
     * @param requestFirstAuto 首次获取创建时是否自动请求一次
     */
    public <S, T, V extends RequestLiveData<S, T>> V getRequestLiveData(RequestObj<S> requestObj, Class<V> liveDataCls, boolean requestFirstAuto) {
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
            if (requestFirstAuto) {
                liveData.refresh();
            }
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
