package cn.chan.com.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.chan.com.activity.LocalMusicActivity;
import cn.chan.com.entity.SongDetailEntity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongScannerImpl implements ISongScanner {
    private Context context;
    private ArrayList<SongDetailEntity> mSongs = new ArrayList<SongDetailEntity>();


    public  SongScannerImpl(Context context){
        this.context = context;

    }
    @Override
    public ArrayList<SongDetailEntity> getAllSongs() {
        final Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                while(cursor.moveToNext()){
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    mSongs.add(new SongDetailEntity(title,path,artist,duration,album));
                }

//            }
//        }).start();
        //Toast.makeText(context,"SCANNER OVER ! count = "+mSongs.size(),Toast.LENGTH_SHORT);
        return mSongs;
    }
}
