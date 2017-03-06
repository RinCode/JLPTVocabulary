package cc.tachi.jlpt.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class ChangeWord {
    private SharedPreferences preferences;
    private SQLiteDatabase db;

    public ChangeWord(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, MyWidget.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        preferences = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE);
        String pos = preferences.getString("pos", "1");
        String level = preferences.getString("level", "n5");
        SharedPreferences.Editor editor = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE).edit();
        editor.putString("pos", String.valueOf(Integer.parseInt(pos) + 1));
        editor.apply();

        db = context.openOrCreateDatabase(level + ".db", context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("select * from jlpt where _id =" + pos, null);
        //设置要显示的TextView，及显示的内容'
        if (c != null) {
            while (c.moveToNext()) {
                views.setTextViewText(R.id.kanji, c.getString(c.getColumnIndex("kanji")));
                views.setTextViewText(R.id.hiragana, c.getString(c.getColumnIndex("hiragana")));
                views.setTextViewText(R.id.meaning, c.getString(c.getColumnIndex("simplified_chinese")));
            }
            // 发送一个系统广播
            manager.updateAppWidget(provider, views);
        } else {
            editor.putString("position", "1");
            editor.apply();
        }
        db.close();
    }
}
