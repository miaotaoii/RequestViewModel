package com.requestVM.demo.viewmodel;

import com.ocode.requestvm.util.Logger;
import com.ocode.requestvm.viewmodel.RequestLiveData;
import com.requestVM.demo.beans.PriceBean;
import com.requestVM.demo.beans.ResponseData;

/**
 * @author:eric
 * @date:6/4/22
 */
public class OliPriceLiveData2 extends RequestLiveData<ResponseData<PriceBean>, PriceBean> {

    @Override
    protected void onActive() {
        super.onActive();
        Logger.logI("OliPriceLiveData2 onActive");
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Logger.logI("OliPriceLiveData2 onInactive");

    }

    @Override
    public void onLoadSuccess(ResponseData<PriceBean> data) {
        Logger.logI("OliPriceLiveData2 onLoadSuccess");

        postValue(data.data);
    }

    @Override
    public void onLoadFailed(int code, String msg) {
        Logger.logI("OliPriceLiveData2 onLoadFailed ");

        PriceBean priceBean = new PriceBean();
        priceBean.setCode(code);
        priceBean.setMsg(msg);
        postValue(priceBean);
    }
}
