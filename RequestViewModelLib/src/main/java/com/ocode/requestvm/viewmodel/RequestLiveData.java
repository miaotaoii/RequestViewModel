package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.ocode.requestvm.request.OnDataLoaded;
import com.ocode.requestvm.request.RequestObj;

/**
 * @author:eric
 * @date:6/2/22 关于资源的释放：在livedata onInactive时，告诉持有它的ViewModel 来释放对它的引用，
 * 同时还要处理它对应的request的释放
 */
abstract public class RequestLiveData<S, T> extends MutableLiveData<T> implements OnDataLoaded<S>{

    private String requestKey;
    private RequestViewModel requestViewModel;

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public void setRequestViewModel(RequestViewModel requestViewModel) {
        this.requestViewModel = requestViewModel;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void refresh() {
        requestViewModel.request(this);
    }

    private RequestObj<S> requestObj;

    void setRequestObj(RequestObj<S> requestObj) {
        this.requestObj = requestObj;
    }

    public RequestObj<S> getRequestObj() {
        return requestObj;
    }
}
