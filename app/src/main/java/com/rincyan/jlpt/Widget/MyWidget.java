package com.rincyan.jlpt.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.rincyan.jlpt.R;

/**
 * Created by tachi on 2017-03-05.
 *
 */

public class MyWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //给Button绑定一个PendingIntent，当点击按钮是发送给Service发广播
        //当点击Button时，触发PendingIntent,发广播给MyService
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context,MyWidget.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent numberIntent = new Intent("ACTION_CHANGE_WORD");
        views.setOnClickPendingIntent(R.id.hiragana, PendingIntent.getBroadcast(context, 0, numberIntent , PendingIntent.FLAG_UPDATE_CURRENT));
        manager.updateAppWidget(provider, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
//        启动MyService
        Intent intent = new Intent(context,MyService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
//        停止MyService
        Intent intent = new Intent(context,MyService.class);
        context.stopService(intent);
    }

}