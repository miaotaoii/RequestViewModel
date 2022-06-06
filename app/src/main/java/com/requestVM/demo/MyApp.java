package com.requestVM.demo;

import android.app.Application;

import com.ocode.requestvm.request.RetrofitConfig;
import com.ocode.requestvm.util.Logger;
import com.requestVM.demo.api.RetrofitDataApi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:eric
 * @date:6/6/22
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitDataApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .build())
                .build();
        RetrofitConfig.getInstance(retrofit).init();
        RetrofitConfig.setLogLevel(Logger.LogLevel.INFO);
    }
}
