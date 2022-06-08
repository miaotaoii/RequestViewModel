package com.requestVM.demo.viewmodel;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.flowerroad.requestviewmodel.R;
import com.ocode.requestvm.request.RequestObj;
import com.ocode.requestvm.viewmodel.RequestViewModel;
import com.ocode.requestvm.viewmodel.RequestViewModelProvider;
import com.requestVM.demo.api.RetrofitDataApi;
import com.requestVM.demo.beans.PriceBean;
import com.requestVM.demo.beans.ResponseData;

/**
 * @author:eric
 * @date:6/8/22
 */
public class SecondActivity extends AppCompatActivity {
    private RequestViewModel requestViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        requestViewModel = RequestViewModelProvider.getInstance().get(
                this, RetrofitDataApi.class, RequestViewModel.class
        );
        RequestObj<ResponseData<PriceBean>> requestObj = new RequestObj<ResponseData<PriceBean>>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{"甘肃"};
            }
        };
        requestViewModel.getRequestLiveData(requestObj, OliPriceLiveData2.class).observe(this, new Observer<PriceBean>() {
            @Override
            public void onChanged(PriceBean priceBean) {
                Log.i("SecondActivity ", "释放view model");
                requestViewModel = null;
            }
        });
    }
}
