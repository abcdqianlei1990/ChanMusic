package cn.chan.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MyConstants;

/**
 * Created by Administrator on 2015/7/23.
 */
public class MediaPlayService extends Service{
    private static final String TAG = "MediaPlayService";
    private MediaPlayer mediaPlayer;
    private SongDetailEntity mSong;
    @Override
    public void onCreate() {
        super.onCreate();
        initParams();
    }

    public void initParams(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG,"********complete*********");
                Toast.makeText(MediaPlayService.this,"********complete*********",Toast.LENGTH_SHORT).show();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG,"********prepare*********");
                Toast.makeText(MediaPlayService.this,"********prepare*********",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        });

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }

    public void play(SongDetailEntity song){

        if(mediaPlayer != null){
            try {
//                if(mediaPlayer.isPlaying()){
//                    mediaPlayer.reset();
//                }
                //mediaPlayer = new MediaPlayer();
                //   storage/emulated/0/qqmusic/song/爱我久久.mp3
                Log.d(TAG,"will play :"+song.getPath());
                Toast.makeText(this,"will play "+song.getPath(),Toast.LENGTH_SHORT).show();
                mediaPlayer.setDataSource(song.getPath());
                mediaPlayer.prepare();
                //mediaPlayer.prepareAsync();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            mediaPlayer = new MediaPlayer();
        }
    }
   public class MyBinder extends Binder {
       /**
        * 获取当前Service的实例
        * @return
        */
       public MediaPlayService getService(){
           return MediaPlayService.this;
       }
   }
}
