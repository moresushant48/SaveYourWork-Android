package com.example.saveyourwork.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserRepository {

    @GET("{id}")
    Call<User> showUser(@Path("id") String id);

    @POST("login")
    Call<Boolean> login(@Query("username") String username, @Query("password") String password);

    @POST("register")
    Call<User> register(@Query("email") String email, @Query("username") String username, @Query("password") String password);

    @GET("success")
    Call<String> msg();
}
