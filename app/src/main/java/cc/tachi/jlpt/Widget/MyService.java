package cc.tachi.jlpt.Widget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by tachi on 2017-03-05.
 *
 */

public class MyService extends Service {
    MyReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_MAKE_NUMBER");
        registerReceiver(receiver, filter);
    }
    @Override
    public void onDestroy() {
        //注销广播接收器
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 广播接收器
     */
    private class MyReceiver extends BroadcastReceiver {
        // 接收到Widget发送的广播
        @Override
        public void onReceive(Context context, Intent intent) {
//            if ("ACTION_MAKE_NUMBER".equals(intent.getAction())) {
//
//            }
        }
    }

}