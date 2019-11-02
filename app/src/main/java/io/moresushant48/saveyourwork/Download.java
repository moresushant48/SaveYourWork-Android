package io.moresushant48.saveyourwork;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;


public class Download extends JobIntentService {

    private String BASE_URL;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        String selectedFileName = intent.getStringExtra("fileName");

        BASE_URL = getSharedPreferences("app",MODE_PRIVATE).getString("BASE_URL",null);

        DownloadManager.Request downloadrequest = new DownloadManager.Request(Uri.parse(BASE_URL + "uploads/" + selectedFileName));

        downloadrequest.allowScanningByMediaScanner();
        downloadrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadrequest.setTitle(selectedFileName);
        downloadrequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadrequest.setMimeType(selectedFileName);
        downloadrequest.setVisibleInDownloadsUi(true);
        downloadrequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, selectedFileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(downloadrequest);
    }
}
