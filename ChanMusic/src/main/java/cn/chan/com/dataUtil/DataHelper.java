package cn.chan.com.dataUtil;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/7/22.
 */
public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chanMusic.db";
    private static final int VERSION = 1;
    private String song_t = "create table if not exists song_t(_id integer primary key autoincrement,title text,artist text,path " +
            "text,album text,duration text) ";

    public DataHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(song_t);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
