package cc.tachi.jlpt.Function;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tachi on 2017-03-22.
 *
 */

public class DBOperate {
    private SQLiteDatabase db;
    private Context context;
    public DBOperate(Context context){
        this.context = context;
        Activity a = (Activity) context;
        db = context.openOrCreateDatabase(a.getTitle().toString() + ".db", context.MODE_PRIVATE, null);

    }

    public boolean setChecked(String vocab,int type){//不传id防止出错
        try {
            db.execSQL("UPDATE jlpt SET checked=" + type + " WHERE \"kanji\" = \"" + vocab+"\"");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean setAllChecked(){
        try {
            db.execSQL("UPDATE jlpt SET checked=0");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }
}
