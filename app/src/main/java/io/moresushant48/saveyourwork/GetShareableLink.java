package io.moresushant48.saveyourwork;

import android.content.Context;
import android.content.Intent;

import com.example.saveyourwork.R;

public class GetShareableLink {

    Context context;

    public GetShareableLink(Context context) {
        this.context = context;
    }

    public void shareLink(String fileName) {

        String link = context.getString(R.string.source_heroku) + "uploads/" + fileName;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "File Name : " + fileName + "\nDownload : " + link);
        context.startActivity(Intent.createChooser(i, "Choose"));
    }
}
