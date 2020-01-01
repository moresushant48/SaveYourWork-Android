package io.moresushant48.saveyourwork.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.saveyourwork.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Download;
import io.moresushant48.saveyourwork.Model.File;
import io.moresushant48.saveyourwork.Repository.CustomListAdapter;
import io.moresushant48.saveyourwork.Repository.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicFilesFragment extends Fragment implements CustomListAdapter.OnFileListener, CustomListAdapter.OnFileLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int DOWNLOAD_JOB_ID = 1000;
    private Context context;
    private AlertDialog.Builder onFileLongClickDialog;

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout linearLayout;
    private SearchView searchUsername;
    private Snackbar serviceUnreachable;

    private ArrayList<File> retrievedFiles;
    private CustomListAdapter adapter;
    private int retrievedUserId;
    private String queriedUsername;
    private String[] dialogItems = {"Download", "Share"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_files, container, false);

        TextView toolbarTitle = Objects.requireNonNull(container).getRootView().findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.fragPublicSearch);

        context = view.getContext();
        onFileLongClickDialog = new AlertDialog.Builder(context);

        recyclerView = view.findViewById(R.id.recyclerViewPublicFiles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CustomListAdapter(new ArrayList<File>(), PublicFilesFragment.this, PublicFilesFragment.this));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        searchUsername = view.findViewById(R.id.searchUsername);
        refreshLayout = view.findViewById(R.id.refreshPublicFiles);
        linearLayout = view.findViewById(R.id.publicFilesLinearLayout);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        searchUsername.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queriedUsername = searchUsername.getQuery().toString().trim();
                getUserIdFromDB();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        refreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        getPublicFiles(retrievedUserId);
    }

    private void getUserIdFromDB() {

        final Call<Integer> getUserId = repository.getUserId(queriedUsername);
        getUserId.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (Objects.equals(response.body(), 0))
                    Snackbar.make(linearLayout, "No such user exists.", Snackbar.LENGTH_LONG).show();
                else {
                    retrievedUserId = Objects.requireNonNull(response.body());
                    Log.e("Retrived User Id : ", String.valueOf(retrievedUserId));
                    getPublicFiles(retrievedUserId);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Snackbar.make(linearLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).setAction("Refresh",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRefresh();
                            }
                        }).show();
            }
        });
    }

    private void getPublicFiles(int userId) {

        Call<ArrayList<File>> listPublicFiles = repository.listPublicFiles(userId);
        listPublicFiles.enqueue(new Callback<ArrayList<File>>() {
            @Override
            public void onResponse(Call<ArrayList<File>> call, Response<ArrayList<File>> response) {
                retrievedFiles = response.body();
                adapter = new CustomListAdapter(retrievedFiles, PublicFilesFragment.this, PublicFilesFragment.this);
                recyclerView.setAdapter(adapter);
                if (adapter.getItemCount() == 0)
                    Snackbar.make(linearLayout, queriedUsername + " has no public share.", Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onFailure(Call<ArrayList<File>> call, Throwable t) {
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
    public void onFileClick(int position) {

        Download.enqueueWork(context, Download.class, DOWNLOAD_JOB_ID, new Intent().putExtra("fileName", retrievedFiles.get(position).getFileName()));
        Snackbar.make(linearLayout, "Downloading File(s).", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFileLongClick(final int position) {
        onFileLongClickDialog.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        onFileClick(position);
                        break;
                    case 1:
                        getSharableLink(position);
                        break;
                }
            }
        }).create().show();
    }

    private void getSharableLink(int position) {

        String link = getString(R.string.source_heroku) + "uploads/" + retrievedFiles.get(position).getFileName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "File Name : " + retrievedFiles.get(position).getFileName());
        i.putExtra(Intent.EXTRA_TEXT, "File Name : " + retrievedFiles.get(position).getFileName() + "\nDownload : " + link);
        startActivity(Intent.createChooser(i, "Choose"));
    }
}
