package com.requestVM.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.flowerroad.requestviewmodel.R;
import com.flowerroad.requestviewmodel.databinding.ActivityMainBinding;
import com.ocode.requestvm.request.RequestObj;
import com.ocode.requestvm.request.RetrofitConfig;
import com.ocode.requestvm.viewmodel.RequestViewModel;
import com.ocode.requestvm.viewmodel.RequestViewModelProvider;
import com.requestVM.demo.api.ResponseJsonBean;
import com.requestVM.demo.api.RetrofitDataApi;
import com.requestVM.demo.beans.PriceBean;
import com.requestVM.demo.viewmodel.OliPriceLiveData;


public class MainActivity extends AppCompatActivity {

    private RequestViewModel requestViewModel;
    private ActivityMainBinding mainBinding;
    private OliPriceLiveData liveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //使用databinding绑定view
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        requestViewModel = RequestViewModelProvider.getInstance().get(this,
                RetrofitDataApi.class, RequestViewModel.class);

        //构建请求对象，设置请求api注解和参数,设置api返回对象类型和livedata数据类型
        RequestObj<ResponseJsonBean, PriceBean> requestObj = new RequestObj<ResponseJsonBean, PriceBean>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{formatInputArg()};
            }
        };


        //livedata绑定view
        liveData = requestViewModel.getRequestLiveData(requestObj, OliPriceLiveData.class);
        mainBinding.setOilData(liveData);

        //生成api对应的livedata并观察数据返回，首次生成livedata时，会主动请求一次数据
        //不同的api注解生成唯一对应的livedata
        liveData.observe(this, new Observer<PriceBean>() {
            @Override
            public void onChanged(PriceBean priceBean) {
                if (priceBean.getCode() != 200) {
                    Toast.makeText(MainActivity.this, "请求失败 code =" + priceBean.getCode() + " msg = " + priceBean.getMsg()
                            , Toast.LENGTH_SHORT).show();
                } else {
                    //更新ui ，此处使用dataBinding 自动更新到ui
                    Log.i("MainActivity", "price bean onchanged " + priceBean.toString());
                }
            }
        });


        mainBinding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //主动刷新livedata数据
                //根据requestObj的api注解，复用已存在的livedata
                requestObj.setArgs(new String[]{formatInputArg()});
                liveData.refresh();
            }
        });


    }

    private String formatInputArg() {
        return mainBinding.etProv.getText().toString().replace("省", "").replace("市", "");
    }


}