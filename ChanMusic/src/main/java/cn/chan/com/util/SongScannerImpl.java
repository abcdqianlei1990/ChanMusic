package cn.chan.com.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.chan.com.activity.LocalMusicActivity;
import cn.chan.com.entity.SongDetailEntity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongScannerImpl implements ISongScanner {
    private static final String TAG = "SongScannerImpl";
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
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)).trim().toString();
                    //String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    //Uri uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
                    Log.d(TAG,title+" | "+path);
                    //此处歌曲的总长度duration和播放进度，初始化为0
                    mSongs.add(new SongDetailEntity(title, path, artist, 0, album,0));
                }

//            }
//        }).start();
        //Toast.makeText(context,"SCANNER OVER ! count = "+mSongs.size(),Toast.LENGTH_SHORT);

        return mSongs;
    }
}
