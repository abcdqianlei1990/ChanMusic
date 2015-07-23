package cn.chan.com.dataUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MyConstants;

/**
 * table: title artist path album duration
 */
public class DBAction {
    private static final String TAG = "DBAction";
    private SQLiteDatabase mDB;
    private Context mContext;
    public DBAction(Context mContext){
        this.mContext = mContext;
        mDB = DBAdapter.getDBinstance();
    }

    public void addSongs(ArrayList<SongDetailEntity> songs){
        ContentValues values = new ContentValues();
        long ret = 0;
        for(int i = 0;i < songs.size();i++){
            values.clear();
            values.put("title",songs.get(i).getTitle());
            values.put("artist",songs.get(i).getArtist());
            values.put("path",songs.get(i).getPath());
            values.put("album",songs.get(i).getAlbum());
            values.put("duration",songs.get(i).getDuration());
            ret = mDB.insert(MyConstants.DataBase.TABLE_NAME,null,values);
            Log.d(TAG,"addSongs -> 当前插入一条数据的结果 :"+ret);
        }
    }

    public void insertSong(SongDetailEntity song){
        ContentValues values = new ContentValues();
        long ret = 0;
        values.clear();
        values.put("title", song.getTitle());
        values.put("artist",song.getArtist());
        values.put("path",song.getPath());
        values.put("album",song.getAlbum());
        values.put("duration",song.getDuration());
        ret = mDB.insert(MyConstants.DataBase.TABLE_NAME,null,values);
        Log.d(TAG,"insertSong -> 当前插入一条数据的结果 :"+ret);
    }

    public void deleteSong(SongDetailEntity song){
        int ret = mDB.delete(MyConstants.DataBase.TABLE_NAME,"title=? and artist=?",new String [] {song.getTitle(),song.getArtist()});
        Log.d(TAG,"deleteSong ->  :"+ret);
    }

    public void querySong(SongDetailEntity song){
        Cursor c = mDB.query(MyConstants.DataBase.TABLE_NAME,null,"title=? or artist=?",new String[]{song.getTitle(),song.getArtist()},null,null,null);
        if(c != null){
            Log.d(TAG,"querySong ->  :"+c.getCount());
        }else{
            Log.d(TAG,"querySong -> find nothing ! ");
        }
    }
}
