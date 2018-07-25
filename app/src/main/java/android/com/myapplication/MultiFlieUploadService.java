package android.com.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class MultiFlieUploadService extends Service {
    Context mContext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = this;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("xuzhenyue","网络连接成功");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkOnline()) {
                        FTP ftp = new FTP();
                        try {
                            Log.d("xuzhenyue", "批量上传");
                            ftp.uploadMultiFile(getFiles(), "picture/2018_07_25/001/");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //获取网络状态
    public boolean getNetStatus(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                if(isNetworkOnline()) {
                    return true;
                }
                return false;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                if(isNetworkOnline()) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;

    }
    //判断是否成功连接上了网络
    public boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:"+exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    private LinkedList<File> getFiles(){
        LinkedList<File> list = new LinkedList<File>();
        File[] allFile = new File("/storage/emulated/legacy/DCIM/Camera/").listFiles();
        for (int i = 0; i < allFile.length;i++){
            File file = allFile[i];
            if(file.isFile()){
                list.add(file);
            }
        }
        return list;
    }
}
