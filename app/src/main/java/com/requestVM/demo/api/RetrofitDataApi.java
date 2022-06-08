package com.requestVM.demo.api;

import com.requestVM.demo.beans.PriceBean;
import com.requestVM.demo.beans.ResponseData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author:eric
 * @date:6/4/22
 */
public interface RetrofitDataApi {

    public static final String requestOilprice = "/oilprice/index?key=3c5ee42145c852de4147264f25b858dc";
    public static final String baseUrl = "http://api.tianapi.com";

    @GET(requestOilprice)
    Call<ResponseJsonBean> getOliPrice(@Query("prov") String prov);

    //测试相同注解不同方法签名时的调用
    @GET(requestOilprice)
    Call<ResponseJsonBean> getOliPrice(@Query("prov") String prov, @Query("name") String name);

    @GET(requestOilprice)
    Call<ResponseData<PriceBean>> getOliPriceData(@Query("prov") String prov);
}
