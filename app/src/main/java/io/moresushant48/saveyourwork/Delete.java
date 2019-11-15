package io.moresushant48.saveyourwork;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delete extends JobIntentService {

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private Call<Boolean> deleteFile;

    private Long fileId;
    private String fileName;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        fileId = intent.getLongExtra("deleteFileId", -1);
        fileName = intent.getStringExtra("deleteFileName");

        deleteFileFromDatabase();

    }

    private void deleteFileFromDatabase() {

        deleteFile = repository.deleteFile(fileId, fileName);

        deleteFile.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast.makeText(Delete.this, "Deleted " + fileName , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Delete : ", t.getMessage());
                Toast.makeText(Delete.this, "Failed to delete file.", Toast.LENGTH_SHORT).show();
            }
        });

    }


}