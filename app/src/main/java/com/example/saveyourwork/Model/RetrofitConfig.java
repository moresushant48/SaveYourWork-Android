package com.example.saveyourwork.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public Retrofit getRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.110:8888/rest/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


}
