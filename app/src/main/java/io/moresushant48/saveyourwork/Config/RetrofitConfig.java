package io.moresushant48.saveyourwork.Config;

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
                .baseUrl("https://saveyourwork.herokuapp.com/rest/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


}
