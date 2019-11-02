package io.moresushant48.saveyourwork.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Download;
import io.moresushant48.saveyourwork.Model.File;
import com.example.saveyourwork.R;
import io.moresushant48.saveyourwork.Repository.CustomListAdapter;
import io.moresushant48.saveyourwork.Repository.Repository;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ListView.OnItemClickListener {

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout linearLayout;

    private CustomListAdapter adapter;
    private Call<File[]> files;
    private File[] retrievedFiles;

    private String BASE_URL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_files, container, false);

        listView = view.findViewById(R.id.list_files);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        linearLayout = view.findViewById(R.id.files_layout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        refreshLayout = view.findViewById(R.id.refresh_files);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        onRefresh();
        refreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        int id = getActivity().getSharedPreferences("user", MODE_PRIVATE).getInt("id", -1);

        files = repository.listAllFiles(String.valueOf(id));

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
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                Snackbar.make(linearLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).setAction("Refresh",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRefresh();
                            }
                        }).show();
            }
        });
        refreshLayout.setRefreshing(false);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Download.enqueueWork(getContext(), Download.class, 1000, new Intent().putExtra("fileName",retrievedFiles[position].getFileName()));

    }
}
