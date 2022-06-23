package com.ocode.requestvm.request;

import com.ocode.requestvm.callback.HandleResponseCallBack;

/**
 * @author:eric
 * @date:6/8/22
 */
class TestTask<T> implements Runnable {
    HandleResponseCallBack<T> handleResponseCallBack;

    public TestTask(HandleResponseCallBack handleResponseCallBack) {
        this.handleResponseCallBack = handleResponseCallBack;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handleResponseCallBack.onResponse(null, null);
    }
}
