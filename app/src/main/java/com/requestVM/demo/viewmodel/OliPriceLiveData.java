package com.requestVM.demo.viewmodel;

import com.ocode.requestvm.util.Logger;
import com.ocode.requestvm.viewmodel.RequestLiveData;
import com.requestVM.demo.api.ResponseJsonBean;
import com.requestVM.demo.beans.PriceBean;

/**
 * @author:eric
 * @date:6/4/22
 */
public class OliPriceLiveData extends RequestLiveData<ResponseJsonBean, PriceBean> {
    @Override
    public void onLoadSuccess(ResponseJsonBean data) {
        if (data.getCode() == 200) {
            PriceBean priceBean = data.getNewslist().get(0);
            priceBean.setCode(200);
            postValue(priceBean);
        } else {
            PriceBean priceBean = new PriceBean();
            priceBean.setCode(data.getCode());
            priceBean.setMsg(data.getMsg());
            postValue(priceBean);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        Logger.logI("OliPriceLiveData onActive");

    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Logger.logI("OliPriceLiveData onInactive");

    }

    @Override
    public void onLoadFailed(int code, String msg) {
        Logger.logI("OliPriceLiveData onLoadFailed ");
        PriceBean priceBean = new PriceBean();
        priceBean.setCode(code);
        priceBean.setMsg(msg);
        postValue(priceBean);
    }
}
