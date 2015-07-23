package cn.chan.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MyConstants;

/**
 * Created by Administrator on 2015/7/23.
 */
public class MediaPlayService extends Service{
    private MediaPlayer mediaPlayer;
    private SongDetailEntity mSong;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initParams(){
        mediaPlayer = new MediaPlayer();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = intent.getIntExtra("CMD",-1);
        mSong = intent.getBundleExtra()
        if(cmd == MyConstants.MediaControlCMD.PLAY){
            mediaPlayer.start();
        }else if(cmd == MyConstants.MediaControlCMD.PAUSE){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else
                mediaPlayer.start();
        }else{
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

}
