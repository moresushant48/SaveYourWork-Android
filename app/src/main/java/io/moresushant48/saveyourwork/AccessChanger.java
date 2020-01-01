package io.moresushant48.saveyourwork;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessChanger extends JobIntentService {

    private RetrofitConfig retrofitConfig = new RetrofitConfig();
    private Repository repository;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        repository = retrofitConfig.getRetrofit().create(Repository.class);

        int accessId = intent.getIntExtra("accessId", 1);
        long fileId = intent.getLongExtra("fileId", 0);

        Call<Boolean> accessChanged = repository.changeAccess(fileId, accessId);

        accessChanged.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (Objects.equals(response.body(), true))
                    Toast.makeText(AccessChanger.this, "Modified Access.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AccessChanger.this, "Couldn't modify Access.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(AccessChanger.this, "Service Unreachable.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}