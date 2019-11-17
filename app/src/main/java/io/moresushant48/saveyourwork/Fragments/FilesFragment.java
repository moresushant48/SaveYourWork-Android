package io.moresushant48.saveyourwork.Fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.moresushant48.saveyourwork.Config.RetrofitConfig;
import io.moresushant48.saveyourwork.Delete;
import io.moresushant48.saveyourwork.Download;
import io.moresushant48.saveyourwork.Model.File;
import com.example.saveyourwork.R;
import io.moresushant48.saveyourwork.Repository.CustomListAdapter;
import io.moresushant48.saveyourwork.Repository.Repository;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import io.moresushant48.saveyourwork.Upload;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class FilesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CustomListAdapter.OnFileListener, FloatingActionButton.OnClickListener {

    private static final int PICKFILE_REQUEST_CODE = 100;
    private static final int DOWNLOAD_JOB_ID = 1000;
    private static final int UPLOAD_JOB_ID = 1001;
    private static final int DELETE_JOB_ID = 1002;

    private RetrofitConfig retrofitConfig;
    private Repository repository;

    private RecyclerView recyclerView;
    private FloatingActionButton fabUploadFiles;
    private SwipeRefreshLayout refreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CoordinatorLayout coordinatorLayout;

    private CustomListAdapter adapter;
    private Call<ArrayList<File>> files;
    private ArrayList<File> retrievedFiles;
    private Delete delete;


    private Paint mClearPaint;
    private ColorDrawable mBackground;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;

    // Constructor
    public FilesFragment() {
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor("#b80f0a");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_files, container, false);

        TextView toolbarTitle = Objects.requireNonNull(container).getRootView().findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.fragFiles);

        // Initialize some variables to use in Deletion Animation.
        deleteDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CustomListAdapter(new ArrayList<File>(), FilesFragment.this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabUploadFiles = view.findViewById(R.id.fabUploadFiles);

        retrofitConfig = new RetrofitConfig();
        repository = retrofitConfig.getRetrofit().create(Repository.class);

        coordinatorLayout = view.findViewById(R.id.files_layout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        refreshLayout = view.findViewById(R.id.refresh_files);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        onRefresh();

        refreshLayout.setOnRefreshListener(this);
        fabUploadFiles.setOnClickListener(this);

        return view;
    }

    @Override
    public void onRefresh() {

        recyclerView.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        int id = getActivity().getSharedPreferences("user", MODE_PRIVATE).getInt("id", -1);

        files = repository.listAllFiles(String.valueOf(id));

        files.enqueue(new Callback<ArrayList<File>>() {
            @Override
            public void onResponse(Call<ArrayList<File>> call, Response<ArrayList<File>> response) {
                retrievedFiles =  response.body();

                adapter = new CustomListAdapter(retrievedFiles, FilesFragment.this);
                recyclerView.setAdapter(adapter);

                new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, 1200);
            }

            @Override
            public void onFailure(Call<ArrayList<File>> call, Throwable t) {
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

    @Override
    public void onFileClick(int position) {

        Download.enqueueWork(getContext(), Download.class, DOWNLOAD_JOB_ID, new Intent().putExtra("fileName",retrievedFiles.get(position).getFileName()));
        Snackbar.make(coordinatorLayout, "Downloading File(s).", Snackbar.LENGTH_LONG).show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            Log.e("File Deleted: ", String.valueOf(position));
            Snackbar.make(coordinatorLayout, "Deleting File(s).", Snackbar.LENGTH_SHORT).show();

            Delete.enqueueWork(getContext(), Delete.class, DELETE_JOB_ID,
                    new Intent().putExtra("deleteFileId", retrievedFiles.get(position).getId())
                    .putExtra("deleteFileName", retrievedFiles.get(position).getFileName())
            );

            retrievedFiles.remove(position);
            adapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(backgroundColor);
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;

            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);

        }

        private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
            c.drawRect(left, top, right, bottom, mClearPaint);

        }
    };
}