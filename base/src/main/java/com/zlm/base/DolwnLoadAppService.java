package com.zlm.base;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DolwnLoadAppService extends Service {
    private final String FILE_PATH = Environment.getExternalStorageDirectory()
            + "/godoxlight/";
    private String filename;
    private static final int UPDARE_TOKEN = 1;
    private static final int INSTALL_TOKEN = 2;
    private AlertDialog progressBarDialog;
    private ProgressBar progressBar;
    private int curProgress;
    private String spec;
    private String newVerString = "new";
    private static Context mcontext;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static void setContent(Context context) {
        mcontext = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            newVerString = intent.getStringExtra("newVerString");
            spec = intent.getStringExtra("spec");
            filename = FILE_PATH + "godoxlight" + newVerString + ".apk";
            if (spec != null) {
                showDownloadDialog();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showDownloadDialog() {
        if (mcontext != null) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.view_downloadprogress_dialog, null);
            progressBar = view
                    .findViewById(R.id.pb_download);
            progressBar.setProgress(curProgress);
            progressBarDialog = new AlertDialog.Builder(mcontext,
                    R.style.dialog_nostroke).show();
            progressBarDialog.addContentView(view, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            progressBarDialog.setCancelable(false);
            progressBarDialog.setCanceledOnTouchOutside(false);
        }
        downloadApp();
    }

    private void downloadApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(spec);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                    }
                    out = new FileOutputStream(new File(filename));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLength = 0l;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                        handler.sendEmptyMessage(UPDARE_TOKEN);
                        if (readedLength >= fileLength) {
                            if (progressBarDialog != null) {
                                progressBarDialog.dismiss();
                            }
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    private void installApp() {
        File appFile = new File(filename);
        if (!appFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", appFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + appFile.toString()),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
        stopSelf();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    if (progressBar != null) {
                        progressBar.setProgress(curProgress);
                    }
                    break;
                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };

}
