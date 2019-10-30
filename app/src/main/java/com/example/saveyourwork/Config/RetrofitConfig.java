package com.example.saveyourwork.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitConfig {

    private Retrofit retrofit;
    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public Retrofit getRetrofit() {

         retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.110:8888/rest/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


}
