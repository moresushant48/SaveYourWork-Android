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
    Call<User> getUser(@Path("id") int id);

    @GET("user/getUserId")
    Call<Integer> getUserId(@Query("username") String username);

    @POST("login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @POST("register")
    Call<String> register(@Query("email") String email, @Query("username") String username, @Query("password") String password);

    @GET("list-files/{id}")
    Call<ArrayList<File>> listAllFiles(@Path("id") String id);

    @GET("list-files/public/{userId}")
    Call<ArrayList<File>> listPublicFiles(@Path("userId") int id);

    @GET("list-files/shared/{userId}")
    Call<ArrayList<File>> listSharedFiles(@Path("userId") int id);

    @GET("user/account/getKey/{userId}")
    Call<String> getSharedKey(@Path("userId") int id);

    @GET("user/account/genKey/{userId}")
    Call<String> genSharedKey(@Path("userId") int id);

    @Multipart
    @POST("upload-file/{id}")
    Call<Boolean> uploadFile(@Path("id") String id, @Part MultipartBody.Part filePart, @Query("accessId") int accessId);

    @GET("delete-file/{fileId}")
    Call<Boolean> deleteFile(@Path("fileId") Long fileId, @Query("fileName") String fileName);

    @POST("user/resetPassword/{userId}")
    Call<Boolean> resetPassword(@Path("userId") int userId, @Query("password") String password);

    @POST("changeAccess/{fileId}")
    Call<Boolean> changeAccess(@Path("fileId") Long fileId, @Query("accessId") int accessId);
}
