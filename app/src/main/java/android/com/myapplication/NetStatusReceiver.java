package android.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class NetStatusReceiver extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("xuzhenyue","网络状态改变");
            Intent intents = new Intent(context,MultiFlieUploadService.class);
            mContext.startService(intents);
        }
    }
}
