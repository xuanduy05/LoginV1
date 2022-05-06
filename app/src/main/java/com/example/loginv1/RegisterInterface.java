package com.example.loginv1;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {
    String REGIURL = "http://192.168.1.8:8081/loginandroid/";
    @FormUrlEncoded
    @POST("simpleregister.php")
    Call<String> getUserRegi(
            @Field("name") String name,
            @Field("hobby") String hobby,
            @Field("username") String uname,
            @Field("password") String password
    );
}
