package io.moresushant48.saveyourwork.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Download;
import io.moresushant48.saveyourwork.Model.File;
import com.example.saveyourwork.R;
import io.moresushant48.saveyourwork.Repository.CustomListAdapter;
import io.moresushant48.saveyourwork.Repository.Repository;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import io.moresushant48.saveyourwork.Upload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ListView.OnItemClickListener, FloatingActionButton.OnClickListener {

    private static final int PICKFILE_REQUEST_CODE = 100;
    private static final int DOWNLOAD_JOB_ID = 1000;
    private static final int UPLOAD_JOB_ID = 1001;

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private ListView listView;
    private FloatingActionButton fabUploadFiles;
    private SwipeRefreshLayout refreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CoordinatorLayout coordinatorLayout;

    private CustomListAdapter adapter;
    private Call<File[]> files;
    private File[] retrievedFiles;

    private String BASE_URL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_files, container, false);

        listView = view.findViewById(R.id.list_files);
        fabUploadFiles = view.findViewById(R.id.fabUploadFiles);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        coordinatorLayout = view.findViewById(R.id.files_layout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        refreshLayout = view.findViewById(R.id.refresh_files);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        onRefresh();
        refreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener(this);
        fabUploadFiles.setOnClickListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        listView.setVisibility(View.INVISIBLE);
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
                        listView.setVisibility(View.VISIBLE);
                    }
                }, 1200);
            }

            @Override
            public void onFailure(Call<File[]> call, Throwable t) {
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                Snackbar.make(coordinatorLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).setAction("Refresh",
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

        Download.enqueueWork(getContext(), Download.class, DOWNLOAD_JOB_ID, new Intent().putExtra("fileName",retrievedFiles[position].getFileName()));
    }

    @Override
    public void onClick(View v) {

        Intent chooseFiles = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFiles.setType("*/*");
        chooseFiles.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent i = Intent.createChooser(chooseFiles, "Choose file explorer.");
        startActivityForResult(i, PICKFILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICKFILE_REQUEST_CODE && data != null){

            Upload.enqueueWork(getContext(), Upload.class, UPLOAD_JOB_ID, data);
            Snackbar.make(coordinatorLayout, "Uploading the file(s).", Snackbar.LENGTH_LONG).show();
        }
    }
}
