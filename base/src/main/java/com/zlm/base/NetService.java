package com.zlm.base;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetService {

    @POST("user/login")
    Call<ResponseBody> login(@Query("userName") String  userName, @Query("userPwd") String  userPwd);

    @POST("user/validateCode")
    Call<ResponseBody> validateCode(@Query("mail") String mail);

    @POST("user/getBackPwd")
    Call<ResponseBody> getBackPwd(@Query("mail") String mail,@Query("newPwd") String newPwd,@Query("verificationCode") String verificationCode);

    @POST("user/register")
    Call<ResponseBody> register(@Query("email") String email, @Query("name") String name, @Query("tmpPwd") String tmpPwd, @Query("verificationCode") String verificationCode);

}
