package com.zlm.base;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static Retrofit mRetrofit = null;
    private String uriHead = "http://app.godox.net.cn:8989/godox/base/";
    private static RetrofitHelper getInstance() {
        return RetrofitHelperHolder.retrofitHelper;
    }

    private static class RetrofitHelperHolder {
        private static RetrofitHelper retrofitHelper = new RetrofitHelper();
    }
    private RetrofitHelper() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(uriHead)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    public static NetService getService() {
        return getInstance().mRetrofit.create(NetService.class);
    }
}
