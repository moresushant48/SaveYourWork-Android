package io.moresushant48.saveyourwork.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.saveyourwork.R;
import com.facebook.shimmer.ShimmerFrameLayout;
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

public class SharedFilesFragment extends Fragment implements CustomListAdapter.OnFileListener, CustomListAdapter.OnFileLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int DOWNLOAD_JOB_ID = 1000;
    private Context context;
    private AlertDialog.Builder onFileLongClickDialog;

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout linearLayout;
    private ShimmerFrameLayout shimmerFrameLayout;

    private ArrayList<File> retrievedFiles;
    private CustomListAdapter adapter;
    private int retrievedUserId;
    private String queriedUsername;
    private String[] dialogItems = {"Download"};
    private View loadingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_files, container, false);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(getString(R.string.fragSharedSearch));

        context = view.getContext();
        onFileLongClickDialog = new AlertDialog.Builder(context);

        recyclerView = view.findViewById(R.id.recyclerViewSharedFiles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CustomListAdapter(new ArrayList<>(), SharedFilesFragment.this, SharedFilesFragment.this));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        refreshLayout = view.findViewById(R.id.refreshSharedFiles);
        linearLayout = view.findViewById(R.id.sharedFilesLinearLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        loadingView = view.findViewById(R.id.loadingRelLayout);

        refreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        getSharedFiles(retrievedUserId);
    }

    private void getUserIdFromDB() {

        loadingView.setVisibility(View.VISIBLE);

        final Call<Integer> getUserId = repository.getUserId(queriedUsername);
        getUserId.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (Objects.equals(response.body(), 0)) {
                    Snackbar.make(linearLayout, "No such user exists.", Snackbar.LENGTH_LONG).show();
                    loadingView.setVisibility(View.GONE);
                }
                else {
                    retrievedUserId = Objects.requireNonNull(response.body());
                    Log.e("Retrived User Id : ", String.valueOf(retrievedUserId));
                    getSharedKeyFromDB(response.body()); // @Param 1 - userId retrived from the db.
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                loadingView.setVisibility(View.VISIBLE);
                Snackbar.make(linearLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).setAction("Refresh",
                        v -> onRefresh()).show();
            }
        });
    }

    private void getSharedKeyFromDB(int userId) {

        Call<String> getSharedKey = repository.getSharedKey(userId);
        getSharedKey.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(Objects.requireNonNull(response.body()).length() == 6) {
                    isSharedKeyRight(response.body().trim(), userId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Snackbar.make(linearLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).show();
            }
        });

    }

    private void isSharedKeyRight(String retrievedKey, int userId) {

        loadingView.setVisibility(View.GONE);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.key_input, null);
        EditText inputKey = view.findViewById(R.id.edKey);

        new AlertDialog.Builder(getContext())
                .setTitle("Key")
                .setMessage("Enter Shared Password / Key.")
                .setView(view)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    if(inputKey.getText().toString().trim().equals(retrievedKey)) {
                        getSharedFiles(userId);
                    }
                    else {
                        Toast.makeText(getContext(), "Wrong Password/Key.", Toast.LENGTH_LONG).show();
                    }
                })
                .setCancelable(true).create().show();

    }

    private void getSharedFiles(int userId) {

        refreshLayout.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        Call<ArrayList<File>> listSharedFiles = repository.listSharedFiles(userId);
        listSharedFiles.enqueue(new Callback<ArrayList<File>>() {
            @Override
            public void onResponse(Call<ArrayList<File>> call, Response<ArrayList<File>> response) {
                retrievedFiles = response.body();
                adapter = new CustomListAdapter(retrievedFiles, SharedFilesFragment.this, SharedFilesFragment.this);
                recyclerView.setAdapter(adapter);
                if (adapter.getItemCount() == 0)
                    Snackbar.make(linearLayout, queriedUsername + " has no Shared files.", Snackbar.LENGTH_INDEFINITE).show();

                new Handler().postDelayed(() -> {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }, 1200);

            }

            @Override
            public void onFailure(Call<ArrayList<File>> call, Throwable t) {
                Snackbar.make(linearLayout, "Service Unavailable", Snackbar.LENGTH_INDEFINITE).setAction("Refresh",
                        v -> onRefresh()).show();
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
        onFileLongClickDialog.setItems(dialogItems, (dialog, which) -> {

                switch (which) {
                    case 0:
                        onFileClick(position);
                        break;
                }
        }).create().show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menuSearch);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter an Username");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queriedUsername = query.trim();
                getUserIdFromDB();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty())
                    refreshLayout.setVisibility(View.GONE);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}
