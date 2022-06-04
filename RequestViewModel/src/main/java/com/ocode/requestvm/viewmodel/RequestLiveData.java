package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.ocode.requestvm.request.OnDataLoaded;
import com.ocode.requestvm.request.RequestObj;

/**
 * @author:eric
 * @date:6/2/22
 */
abstract public class RequestLiveData<S, T> extends MutableLiveData<T> implements OnDataLoaded<S> {

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

    private RequestObj requestObj;

    public void setRequestObj(RequestObj requestObj) {
        this.requestObj = requestObj;
    }

    public RequestObj<Object, Object> getRequestObj() {
        return  this.requestObj;
    }

}
