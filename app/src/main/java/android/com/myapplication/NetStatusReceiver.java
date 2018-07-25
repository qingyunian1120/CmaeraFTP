package android.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;


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
