package com.ocode.requestvm.callback;

/**
 * @author:eric
 * @date:6/2/22
 */
public interface OnDataLoaded<T> {
    void onLoadSuccess(T data);

    void onLoadFailed(int code, String msg);
}
