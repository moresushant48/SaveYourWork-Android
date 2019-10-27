package com.example.saveyourwork.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.saveyourwork.Repository.CustomListAdapter;
import com.example.saveyourwork.Repository.Repository;
import com.facebook.shimmer.ShimmerFrameLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;

    private CustomListAdapter adapter;
    private Call<File[]> files;
    private File[] retrievedFiles;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_files, container, false);

        listView = view.findViewById(R.id.list_files);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        refreshLayout = view.findViewById(R.id.refresh_files);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        onRefresh();
        refreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        files = repository.listAllFiles("1");

        files.enqueue(new Callback<File[]>() {
            @Override
            public void onResponse(Call<File[]> call, Response<File[]> response) {
                retrievedFiles =  response.body();

                adapter = new CustomListAdapter(getActivity(), retrievedFiles);

                listView.setAdapter(adapter);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }, 1200);
            }

            @Override
            public void onFailure(Call<File[]> call, Throwable t) {
                Toast.makeText(view.getContext(), "Service unreachable.", Toast.LENGTH_LONG).show();
            }
        });
        refreshLayout.setRefreshing(false);

    }
}
