package cn.chan.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.chan.com.entity.ProgressChangEvent;
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
    private  ArrayList<SongDetailEntity> data = new ArrayList<SongDetailEntity>();
    private int currentPos = -1;
    @Override
    public void onCreate() {
        super.onCreate();
        initParams();
    }

    Timer timer = new Timer(true);
    public void initParams(){
        mediaPlayer = new MediaPlayer();
        mBus = EventBus.getDefault();
        mBus.register(this);
        //mBus.register(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("chan", "********complete*********current pos:" + currentPos);
                //Toast.makeText(MediaPlayService.this,"********complete*********",Toast.LENGTH_SHORT).show();
                //播放完成后，继续播放下一首,得到当前歌曲在data中的pos
                //play(data.get(currentPos+1),currentPos+1);
                //mediaPlayer.
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                Log.d("chan","********prepare*******current pos:"+currentPos);
                //Toast.makeText(MediaPlayService.this,"********prepare*********",Toast.LENGTH_SHORT).show();
                //mediaPlayer.start();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int progress = mediaPlayer.getCurrentPosition();
                        mSong.setProgress(progress);
                        mBus.post(mSong);
                    }
                },0,500);
            }
        });

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = intent.getBundleExtra("list");
        ArrayList<Parcelable> songs = data.getParcelableArrayList("songs");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        data = intent.getBundleExtra("data").getParcelableArrayList("all_songs");
        Log.d("chan","activity绑定service时，获取到来自于activity的所有歌曲信息.size:"+data.size());
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        timer.cancel();
        mBus.unregister(this);
        super.onDestroy();
        //mBus.unregister(this);
    }

    /**
     * @param song
     * @param position 歌曲在list中的位置，即在data中的位置
     */
    public void play(SongDetailEntity song,int position){
        currentPos = position;
        if(mediaPlayer != null){
            try {
                //如果当前正在播放
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.reset();
                }
                Log.d("chan","will play :"+song.getPath());
                File file = new File(song.getPath());
                if(file.exists()){
                    mSong = song;
                    //发布事件，为了在activity下显示歌曲信息（title,artist,progress）
                    Log.d(TAG,"歌曲总长度："+mediaPlayer.getDuration());
                    //设置歌曲总长度
                    mSong.setDuration(mediaPlayer.getDuration());
                    mBus.post(mSong);
                    mediaPlayer.setDataSource(mSong.getPath());
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

    //当用户手动调整播放进度时候，播放服务得到进度，然后调整media play 的progress
   public void onEventMainThread(ProgressChangEvent event){
       if(event instanceof ProgressChangEvent){
           if(event != null){
               Log.d("chan","用户手动调整progress："+event.getProgress());
                mediaPlayer.seekTo(event.getProgress());
           }
       }
   }

}
