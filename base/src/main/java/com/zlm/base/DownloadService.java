package com.zlm.base;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService {

    /**
     * 是否正在下载
     */
    private boolean isDownloading=false;
    public static int progress;
    private String dowUrl = null;
   //下载状态
    private int status = -1;
    private int fileSize;
    private int readSize;
    private int downSize;
    private File downFile;
    private static  DownloadService mInstance = null;
    private DownlaodProgressCallBack mDownlaodProgressCallBack;
    /**下载完成状态*/
    public static final int DOWNLOAD_COMPLETE=0;
    /**下载完成状态*/
    public static final int DOWNLOAD_ERROR=-1;
    public static String down_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bxtx/";
    public static final String FEATURE_PATH =  down_file+"LK8620_mesh_GD.bin";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                      //更新下载进度
                    if(mDownlaodProgressCallBack!=null){
                        int progress = (int) ((double) downSize / (double) fileSize * 100);
                        mDownlaodProgressCallBack.onDownlaodProgress(progress);
                    }
                    break;
                case 1:
                    //下载完成进度
                    mDownlaodProgressCallBack.onDownlaodStatus(DOWNLOAD_COMPLETE);
                    break;
                case 2:
                    //下载异常
                    mDownlaodProgressCallBack.onDownlaodStatus(DOWNLOAD_ERROR);
                    break;
            }


        }
    };

    private DownloadService(){
    }

    public static DownloadService initDownloadService(){
        if(mInstance ==null){
            mInstance = new DownloadService();
        }
        return mInstance;
    }


    public static DownloadService getInstance(){
        return mInstance;
    }


    public void initDownLoadUrl(String downloadUrl){
        dowUrl = downloadUrl;
    }

    public void setDownlaodProgressCallBack(DownlaodProgressCallBack downlaodProgressCallBack){
        this.mDownlaodProgressCallBack = downlaodProgressCallBack;
    }


    public void startService(){
        if(isDownloading){
            return;
        }
        new Thread(startDownload).start();
    }

    public void stopService(){
        isDownloading = true;
    }

    /**
     * 下载模块
     */
    private Runnable startDownload = new Runnable() {

        @Override
        public void run() {
            fileSize = 0;
            readSize = 0;
            downSize = 0;
            progress = 0;

            InputStream is = null;
            FileOutputStream fos = null;
            Log.e("downUrl", dowUrl);
            try {
                URL myURL = new URL(dowUrl);
                URLConnection conn = myURL.openConnection();
                conn.connect();
                fileSize = conn.getContentLength();
                is = conn.getInputStream();

                if (is == null) {
                    Log.d("tag", "error");
                    throw new RuntimeException("stream is null");
                }

                File dir = new File(down_file);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                downFile = new File(FEATURE_PATH);
                fos = new FileOutputStream(downFile);
                byte buf[] = new byte[1024 * 1024];
                isDownloading = true;
                while ((readSize = is.read(buf)) > 0) {
                    if(!isDownloading){
                        return;
                    }
                    fos.write(buf, 0, readSize);
                    downSize += readSize;
                    Log.e("downSize", downSize+"");
                    sendMessage(0,downSize);
                }
                sendMessage(1);
                isDownloading = false;

            } catch (Exception e) {
                sendMessage(2);
            } finally {
                try {
                    if (null != fos) fos.close();
                    if (null != is) is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private void sendMessage(int code){
       handler.sendEmptyMessage(code);
    }

    private void sendMessage(int code,int progress){
        Message message = new Message();
        message.what=code;
        message.arg1 = progress;
        handler.sendMessage(message);
    }




    /**
     * 获取进度
     */
    public int getProgress() {
        return progress;
    }

    public interface DownlaodProgressCallBack{

        public void onDownlaodProgress(int progress);
        public void onDownlaodStatus(int status);

    }


}
