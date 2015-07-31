package cn.chan.com.dataUtil;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/7/23.
 */
public class DBAdapter {
    private static SQLiteDatabase db;
    private static DataHelper helper;

    public static SQLiteDatabase getDBInstance() {
        if(db == null){
            //helper = new DataHelper()
            //db = helper.getReadableDatabase();
        }
        return db;
    }
    public static void destroyDB(){
        if(db != null){
            db.close();
            helper.close();
        }
    }
}
