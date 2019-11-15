package io.moresushant48.saveyourwork.Repository;

import java.util.ArrayList;

import io.moresushant48.saveyourwork.Model.File;
import io.moresushant48.saveyourwork.Model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Repository {

    @GET("{id}")
    Call<User> showUser(@Path("id") String id);

    @POST("login")
    Call<Integer> login(@Query("username") String username, @Query("password") String password);

    @POST("register")
    Call<String> register(@Query("email") String email, @Query("username") String username, @Query("password") String password);

    @GET("list-files/{id}")
    Call<ArrayList<File>> listAllFiles(@Path("id") String id);

    @Multipart
    @POST("upload-file/{id}")
    Call<Boolean> uploadFile(@Path("id") String id, @Part MultipartBody.Part filePart);

    @GET("delete-file/{fileId}")
    Call<Boolean> deleteFile(@Path("fileId") Long fileId, @Query("fileName") String fileName);
}
