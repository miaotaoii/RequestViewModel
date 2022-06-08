package com.ocode.requestvm.request;

/**
 * @author:eric
 * @date:6/8/22
 */
public class TestTask<T> implements Runnable {
    TypedRequestImpl.HandleResponseCallBack<T> handleResponseCallBack;

    public TestTask(TypedRequestImpl.HandleResponseCallBack handleResponseCallBack) {
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
