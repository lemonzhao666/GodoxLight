package com.zlm.base;

import com.zlm.base.model.UpdateInfo;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface NetService {

    @GET("user/login")
    Call<ResponseBody> login(@Query("userName") String userName, @Query("userPwd") String userPwd);

    @GET("user/validateCode")
    Call<ResponseBody> validateCode(@Query("mail") String mail);

    @GET("user/getBackPwd")
    Call<ResponseBody> getBackPwd(@Query("mail") String mail, @Query("newPwd") String newPwd, @Query("verificationCode") String verificationCode);

    @GET("user/register")
    Call<ResponseBody> register(@Query("email") String email, @Query("name") String name, @Query("tmpPwd") String tmpPwd, @Query("verificationCode") String verificationCode);

    @GET
    Flowable<UpdateInfo> getDeviceUpdateInfo(@Url String url);

    @Streaming
    @GET
    Call<ResponseBody> downloadDevcieBin(@Url String url);
}
