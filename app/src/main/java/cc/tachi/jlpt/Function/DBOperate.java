package cc.tachi.jlpt.Function;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    public boolean setChecked(int position,int type){
        try {
            db.execSQL("UPDATE jlpt SET checked=" + type + " WHERE _id = " + position);
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
