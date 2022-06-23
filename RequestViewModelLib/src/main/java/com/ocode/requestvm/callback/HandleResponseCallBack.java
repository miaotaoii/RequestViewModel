package com.ocode.requestvm.callback;

import com.ocode.requestvm.exception.OwnerDestroyedException;
import com.ocode.requestvm.request.impl.TypedRequestImpl;
import com.ocode.requestvm.util.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author:eric
 * @date:2022/6/23
 */
public final class HandleResponseCallBack<T> implements Callback<T> {
    private OnDataLoaded<T> callBack;//actually is a livedata
    private TypedRequestImpl typedRequest;
    private volatile boolean hasOwnerDestroyed = false;

    public HandleResponseCallBack(OnDataLoaded<T> callBack, TypedRequestImpl typedRequest) {
        this.callBack = callBack;
        this.typedRequest = typedRequest;
    }

    public void onClearHandleCallback() {
        hasOwnerDestroyed = true;
        if (typedRequest != null)
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
        } finally {
            onClearHandleCallback();
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
        } finally {
            onClearHandleCallback();
        }
    }
}
