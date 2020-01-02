package io.moresushant48.saveyourwork;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Repository.Repository;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Upload extends JobIntentService {

    private RetrofitConfig retrofitConfig;
    private Repository repository;


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        String id = String.valueOf(getSharedPreferences("user", MODE_PRIVATE).getInt("id", -1));

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        if (intent.getClipData() != null) {

            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {

                uploadFile(id, intent.getClipData().getItemAt(i).getUri());
                System.out.println("Multiple  : " + intent.getClipData().getItemAt(i).getUri());
            }
        } else {

            uploadFile(id, intent.getData());
            System.out.println("Single : " + intent.getDataString());
        }

    }

    private void uploadFile(String id, Uri uri) {

        File file = null;
        OutputStream outStream = null;
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);

            Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + displayName);
                }
            } finally {
                cursor.close();
            }

            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("*/*"), file));

        Call<Boolean> upload = repository.uploadFile(id, filePart);

        upload.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body().equals(true))
                    Toast.makeText(Upload.this, "Uploaded file(s).", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Upload.this, "Failed to upload file(s).", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(Upload.this, "Service Unavailable." + t, Toast.LENGTH_LONG).show();
                System.out.println("Error : " + t);
            }
        });
    }

}
