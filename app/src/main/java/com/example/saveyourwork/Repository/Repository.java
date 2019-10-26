package com.example.saveyourwork.Repository;

import com.example.saveyourwork.Model.File;
import com.example.saveyourwork.Model.User;

import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Repository {

    @GET("{id}")
    Call<User> showUser(@Path("id") String id);

    @POST("login")
    Call<Boolean> login(@Query("username") String username, @Query("password") String password);

    @POST("register")
    Call<String> register(@Query("email") String email, @Query("username") String username, @Query("password") String password);

    @GET("list-files/{id}")
    Call<File[]> listAllFiles(@Path("id") String id);
}
