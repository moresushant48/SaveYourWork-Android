package com.example.saveyourwork.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.saveyourwork.Config.RetrofitConfig;
import com.example.saveyourwork.Model.File;
import com.example.saveyourwork.R;
import com.example.saveyourwork.Repository.Repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private Animation animFadeIn;

    private ArrayAdapter<String> adapter;
    private Call<File[]> files;
    private File[] retrievedFiles;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_files, container, false);

        animFadeIn = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);

        listView = view.findViewById(R.id.list_files);
        adapter = new ArrayAdapter<>(view.getContext(), R.layout.list_file_view);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        refreshLayout = view.findViewById(R.id.refresh_files);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        onRefresh();
        refreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {
        files = repository.listAllFiles("1");

        files.enqueue(new Callback<File[]>() {
            @Override
            public void onResponse(Call<File[]> call, Response<File[]> response) {
                retrievedFiles =  response.body();
                adapter.clear();

                for(File file : retrievedFiles){
                    adapter.add(file.getFileName());
                }

                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
                listView.startAnimation(animFadeIn);
            }

            @Override
            public void onFailure(Call<File[]> call, Throwable t) {
                Toast.makeText(view.getContext(), "Service unreachable.", Toast.LENGTH_LONG).show();
            }
        });
        refreshLayout.setRefreshing(false);

    }



}
