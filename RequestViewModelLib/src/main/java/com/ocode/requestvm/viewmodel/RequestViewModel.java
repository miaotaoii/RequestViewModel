package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.ViewModel;

import com.ocode.requestvm.request.RequestObj;
import com.ocode.requestvm.request.TypedRequestImpl;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author:eric
 * @date:6/2/22
 */
public class RequestViewModel extends ViewModel {

    private HashMap<String, RequestData<?>> map = new HashMap<>();

    /**
     * 保存每个api请求的 livedata和request对象的结构
     *
     * @param <T> :viewmodel的数据类型
     *            //     * @param <S> api请求返回的数据类型
     *            //     * @param <U> retrofit 声明api接口的所在类型
     */
    static class RequestData<T> {
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
     * @param <T> viewmodel的数据类型
     * @param <V> RequestLiveData的类型
     */
    public <S, T, V extends RequestLiveData<S, T>> V getRequestLiveData(RequestObj<S, T> requestObj, Class<V> liveDataCls) {
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

            RequestData<T> requestData = new RequestData<>();
            requestData.setRequestLiveData(liveData);
            requestData.setTypedRequest(typedRequest);

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
