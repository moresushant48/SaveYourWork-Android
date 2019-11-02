package io.moresushant48.saveyourwork.Repository;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.moresushant48.saveyourwork.Model.File;
import com.example.saveyourwork.R;

public class CustomListAdapter extends ArrayAdapter<File> {

    private Activity context;
    private File[] files;

    public CustomListAdapter(Activity context, File[] files){
        super(context,R.layout.list_file_view,files);
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView;
        if(row == null){
            row =  LayoutInflater.from(context).inflate(R.layout.list_file_view, parent, false);
        }
        File file = getItem(position);

        ImageView imgFile = row.findViewById(R.id.imgFile);
        TextView txtMainTitle = row.findViewById(R.id.txtMainTitle);
        TextView txtSubTitle = row.findViewById(R.id.txtSubTitle);

        imgFile.setImageResource(R.drawable.ic_file);
        txtMainTitle.setText(file.getFileName());
        txtSubTitle.setText(file.getFileSize());

        return row;
    }

    @NonNull
    @Override
    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
