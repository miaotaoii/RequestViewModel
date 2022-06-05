package com.ocode.requestvm.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:eric
 * @date:5/27/22
 */
public class RetrofitUtil {
    private static volatile RetrofitUtil ourInstance = null;
    public static String baseUrl;
    private Retrofit retrofit;
    private ArrayList<Object> services;

    public void createApi(Class[] service) {
//        for (Class cls :
//                service) {
//            services.add(retrofit.create(cls));
//        }
    }


    public <U> U getService(Class<U> service) {
        return retrofit.create(service);
    }



    public static RetrofitUtil getInstance() {
        if (ourInstance == null) {
            synchronized (RetrofitUtil.class) {
                if (ourInstance == null) {
                    ourInstance = new RetrofitUtil();
                }
            }
        }
        return ourInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private RetrofitUtil() {
        services = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .build())
                .build();
    }
}
