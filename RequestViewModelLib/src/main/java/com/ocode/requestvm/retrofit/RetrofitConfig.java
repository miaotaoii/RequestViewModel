package com.ocode.requestvm.retrofit;

import com.ocode.requestvm.util.Logger;
import com.ocode.requestvm.util.Utils;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:eric
 * @date:5/27/22
 */
public class RetrofitConfig {
    private static volatile RetrofitConfig ourInstance = null;
    private Retrofit retrofit;
    private ArrayList<Object> services;


    public <U> U getService(Class<U> service) {
        return retrofit.create(service);
    }

    /**
     * 获得单例
     * 单例构造时会创建默认的retrofit实例
     *
     * @param baeUrl base url
     */
    public static RetrofitConfig getInstance(String baeUrl) {
        if (ourInstance == null) {
            synchronized (RetrofitConfig.class) {
                if (ourInstance == null) {
                    ourInstance = new RetrofitConfig(baeUrl);
                }
            }
        }
        return ourInstance;
    }

    public static void setLogLevel(Logger.LogLevel logLevel) {
        Logger.setLogLevel(logLevel);
    }

    public static RetrofitConfig getInstance() {
        Utils.checkNotNull(ourInstance, "RetrofitConfig has not been init");
        return ourInstance;
    }

    public void init() {
    }


    /**
     * 获得单例
     *
     * @param retrofitInstance 可以传入项目已有的retrofit实例,RequestViewModel将使用该实例创建请求
     */
    public static RetrofitConfig getInstance(Retrofit retrofitInstance) {
        if (ourInstance == null) {
            synchronized (RetrofitConfig.class) {
                if (ourInstance == null) {
                    ourInstance = new RetrofitConfig(retrofitInstance);
                }
            }
        }
        return ourInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private RetrofitConfig(Retrofit retrofitInstance) {
        Utils.checkNotNull(retrofitInstance, "retrofitInstance can't be null");

        services = new ArrayList<>();
        this.retrofit = retrofitInstance;
    }

    private RetrofitConfig(String baseUrl) {
        services = new ArrayList<>();
        retrofit = createDefaultRetrofitInstance(baseUrl);
    }

    /**
     * 创建默认的retrofit实例
     */
    private Retrofit createDefaultRetrofitInstance(String baseUrl) {
        Utils.checkNotNull(baseUrl, "base url can't be null while create default retrofit instance");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .build())
                .build();
    }
}
