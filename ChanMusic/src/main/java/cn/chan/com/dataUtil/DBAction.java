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
        mDB = new DataHelper(mContext).getReadableDatabase();
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
            values.put("progress",songs.get(i).getProgress());
            values.put("bitmap","");
            values.put("favor",songs.get(i).getFavor());
            values.put("inQueue",songs.get(i).getInQueue());
            ret = mDB.insert(MyConstants.DataBase.TABLE_NAME,null,values);
            Log.d("chan","addSongs -> 当前插入一条数据的结果 :"+ret);
        }
        Log.d("chan","addSongs success !");
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
        values.put("progress",song.getProgress());
        values.put("bitmap","");
        values.put("favor",song.getFavor());
        values.put("inQueue",song.getInQueue());
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

    public ArrayList<SongDetailEntity> querySongs(){
        ArrayList<SongDetailEntity> list = new ArrayList<SongDetailEntity>();
        Cursor c = mDB.query(MyConstants.DataBase.TABLE_NAME,null,null,null,null,null,null);
        while(c.moveToNext()){
            String title = c.getString(c.getColumnIndex("title"));
            String path = c.getString(c.getColumnIndex("path"));
            String artist = c.getString(c.getColumnIndex("artist"));
            int duration = c.getInt(c.getColumnIndex("duration"));
            String album = c.getString(c.getColumnIndex("album"));
            int progress = c.getInt(c.getColumnIndex("progress"));
            String array = c.getString(c.getColumnIndex("bitmap"));
            int favor = c.getInt(c.getColumnIndex("favor"));
            int inQueue = c.getInt(c.getColumnIndex("inQueue"));
            list.add(new SongDetailEntity(title,path,artist,duration,album,progress,null,favor,inQueue));
        }
        return list;
    }

    public void resetTable() {
        int ret = mDB.delete(MyConstants.DataBase.TABLE_NAME,null,null);
        Log.d("chan","成功删除"+ret+"条数据。");
    }
}
