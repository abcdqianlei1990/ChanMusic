package cn.chan.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MyConstants;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/23.
 */
public class MediaPlayService extends Service{
    private static final String TAG = "MediaPlayService";
    private MediaPlayer mediaPlayer;
    private SongDetailEntity mSong;
    private EventBus mBus;
    @Override
    public void onCreate() {
        super.onCreate();
        initParams();
    }

    public void initParams(){
        mediaPlayer = new MediaPlayer();
        mBus = EventBus.getDefault();
        //mBus.register(this);
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
        //mBus.unregister(this);
    }

    public void play(SongDetailEntity song){

        if(mediaPlayer != null){
            try {
                //如果当前正在播放
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.reset();
                }
                Log.d(TAG,"will play :"+song.getPath());
                File file = new File(song.getPath());
                if(file.exists()){
                    //发布事件，为了在activity下显示歌曲信息（title,artist,progress）
                    mBus.post(song);
                    mediaPlayer.setDataSource(song.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }else{
                    Log.d(TAG,"current file is not exist !");
                    Toast.makeText(this,"播放的文件不存在！",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }catch (IllegalStateException e){
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
