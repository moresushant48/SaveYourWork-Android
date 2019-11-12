package io.moresushant48.saveyourwork.Repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.moresushant48.saveyourwork.Model.File;
import com.example.saveyourwork.R;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CustomListAdapterViewHolder> {

    private File[] files;

    public CustomListAdapter(File[] files) {
        this.files = files;
    }

    public class CustomListAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFile;
        TextView txtMainTitle;
        TextView txtSubTitle;

        public CustomListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFile = itemView.findViewById(R.id.imgFile);
            txtMainTitle = itemView.findViewById(R.id.txtMainTitle);
            txtSubTitle = itemView.findViewById(R.id.txtSubTitle);
        }
    }

    @NonNull
    @Override
    public CustomListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_file_view, parent, false);
        return new CustomListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomListAdapterViewHolder holder, int position) {

        holder.txtMainTitle.setText(files[position].getFileName());
        holder.txtSubTitle.setText(files[position].getFileSize());
        holder.imgFile.setImageResource(R.drawable.ic_file);
    }

    @Override
    public int getItemCount() {
        return files.length;
    }
}