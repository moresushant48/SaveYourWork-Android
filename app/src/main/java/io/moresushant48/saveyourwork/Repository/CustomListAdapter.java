package io.moresushant48.saveyourwork.Repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveyourwork.R;

import java.util.ArrayList;

import io.moresushant48.saveyourwork.Model.File;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CustomListAdapterViewHolder> implements Filterable {

    private ArrayList<File> files;
    private ArrayList<File> filesFull;
    private OnFileListener onFileListener;
    private OnFileLongClickListener onFileLongClickListener;

    public CustomListAdapter(ArrayList<File> files, OnFileListener onFileListener, OnFileLongClickListener onFileLongClickListener) {
        this.files = files;
        filesFull = new ArrayList<>(files);
        this.onFileListener = onFileListener;
        this.onFileLongClickListener = onFileLongClickListener;
    }

    @NonNull
    @Override
    public CustomListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_file_view, parent, false);
        return new CustomListAdapterViewHolder(view, onFileListener, onFileLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomListAdapterViewHolder holder, int position) {

        holder.txtMainTitle.setText(files.get(position).getFileName());
        holder.txtSubTitle.setText(files.get(position).getFileSize());
        holder.txtAccess.setText(files.get(position).getAccess().getAccess());
        holder.imgFile.setImageResource(R.drawable.ic_file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                ArrayList<File> filesFiltered = new ArrayList<>();
                String query = constraint.toString().toLowerCase().trim();
                if (query.isEmpty()) {
                    filesFiltered.addAll(filesFull);
                } else {

                    for (File file : filesFull) {
                        if (file.getFileName().toLowerCase().contains(query)) {
                            filesFiltered.add(file);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                files.clear();
                files.addAll((ArrayList<File>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public interface OnFileListener {
        void onFileClick(int position);
    }

    public interface OnFileLongClickListener {
        void onFileLongClick(int position);
    }

    public class CustomListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imgFile;
        TextView txtMainTitle;
        TextView txtSubTitle;
        TextView txtAccess;
        OnFileListener onFileListener;
        OnFileLongClickListener onFileLongClickListener;

        private CustomListAdapterViewHolder(@NonNull View itemView, OnFileListener onFileListener, OnFileLongClickListener onFileLongClickListener) {
            super(itemView);
            imgFile = itemView.findViewById(R.id.imgFile);
            txtMainTitle = itemView.findViewById(R.id.txtMainTitle);
            txtSubTitle = itemView.findViewById(R.id.txtSubTitle);
            txtAccess = itemView.findViewById(R.id.txtAccess);
            this.onFileListener = onFileListener;
            this.onFileLongClickListener = onFileLongClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFileListener.onFileClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onFileLongClickListener.onFileLongClick(getAdapterPosition());
            return true;
        }
    }
}