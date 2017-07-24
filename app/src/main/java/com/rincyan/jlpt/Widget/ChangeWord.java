package com.rincyan.jlpt.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.rincyan.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class ChangeWord {
    private SharedPreferences preferences;
    private SQLiteDatabase db;
    private Context context;

    public ChangeWord(Context context) {
        this.context = context;
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, MyWidget.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        preferences = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE);
        String pos = preferences.getString("pos", "1");
        String level = preferences.getString("level", "n5");
        SharedPreferences.Editor editor = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE).edit();
        editor.putString("pos", String.valueOf(Integer.parseInt(pos) + 1));
        editor.apply();

        datacheck(level, pos);

        db = context.openOrCreateDatabase(level + ".db", context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("select * from jlpt where _id =" + pos, null);

        //设置要显示的TextView，及显示的内容'
        if (c != null) {
            if (c.moveToNext()) {
                views.setTextViewText(R.id.kanji, c.getString(c.getColumnIndex("kanji")));
                views.setTextViewText(R.id.hiragana, c.getString(c.getColumnIndex("hiragana")));
                views.setTextViewText(R.id.meaning, c.getString(c.getColumnIndex("simplified_chinese")));
            } else {
                editor.putString("pos", "1");
                editor.apply();
            }
            // 发送一个系统广播
            manager.updateAppWidget(provider, views);
        }
        db.close();
    }

    private void datacheck(String level, String pos) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        preferences = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE);
        String date = preferences.getString("date", "1");
        db = context.openOrCreateDatabase("recent.db", context.MODE_PRIVATE, null);
        if (!Objects.equals(date, str)) {
            SharedPreferences.Editor editor = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE).edit();
            editor.putString("date", str);
            editor.apply();
            db.execSQL("delete from jlpt;");
        }

        SQLiteDatabase dbtemp = context.openOrCreateDatabase(level + ".db", context.MODE_PRIVATE, null);
        Cursor c = dbtemp.rawQuery("select * from jlpt where _id = "+pos, null);
        if (c != null) {
            while (c.moveToNext()) {
                db.execSQL("insert into jlpt(level,kanji,hiragana,simplified_chinese,traditional_chinese,english,checked) values(\"" + c.getString(c.getColumnIndex("level")) + "\",\"" + c.getString(c.getColumnIndex("kanji")) + "\",\"" + c.getString(c.getColumnIndex("hiragana")) + "\",\"" + c.getString(c.getColumnIndex("simplified_chinese")) + "\",\"" + c.getString(c.getColumnIndex("traditional_chinese")) + "\",\"" + c.getString(c.getColumnIndex("english")) + "\",0);");
            }
            c.close();
        }
        dbtemp.close();
        db.close();
    }
}
