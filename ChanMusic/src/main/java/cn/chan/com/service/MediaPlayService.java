package cn.chan.com.service;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import cn.chan.com.activity.BaseApplication;
import cn.chan.com.entity.MyEvent;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.DataProcess;
import cn.chan.com.util.MyConstants;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/23.
 */
public class MediaPlayService extends Service{
    private static final String TAG = "MediaPlayService";
    private  MediaPlayer mediaPlayer;
    BaseApplication mApplication;
    private SongDetailEntity mSong;
    private EventBus mBus;
    private  ArrayList<SongDetailEntity> data = new ArrayList<SongDetailEntity>();
    private int currentPos = -1;    //index
    private int mCurrentProgress;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MediaPlayerServiceBroadcastRec mReceiver;
    private Timer timer;
    //private ProgressMonitor mProgressMonitor;
    private long mClickTime;
    private static final int MEDIA_PLAYER_COMPLETE_MSG = 15;
    private boolean mFromUser = false;
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    private boolean mPoolIsShutDown = false;
    private static final int MEDIA_PLAYER_IS_RELEASED_MSG = 16;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MEDIA_PLAYER_COMPLETE_MSG:
                    Log.d("chan","播放结束！");
                    break;
                default:
                    break;
            }
        }
    };
    Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mediaPlayer != null) {
                int duration = mediaPlayer.getDuration();
                int pos = mediaPlayer.getCurrentPosition();
                //Log.d("chan","progress monitor-->current position="+pos);
                mBus.post(new MyEvent(duration, pos, false, null));
                // 每次延迟50毫秒再启动线程
                handler.postDelayed(updateThread, 50);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        initParams();

    }

    public void initParams(){

        timer = new Timer(true);
//        mProgressMonitor = new ProgressMonitor();
        mApplication = (BaseApplication) getApplication();
        data = mApplication.getAllData();
        Log.d("chan","service create 时候获取内存中数据："+data.size());
        mediaPlayer = new MediaPlayer();
        mBus = EventBus.getDefault();
        mBus.register(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReceiver = new MediaPlayerServiceBroadcastRec();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                Log.d("chan","********prepare*******current pos:"+currentPos);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //Log.d("chan", "********complete*********current pos:" + currentPos);
                if((System.currentTimeMillis() - mClickTime) < mediaPlayer.getDuration() && !mFromUser){
                    Log.d("chan","first complete");
                }else{
                    Log.d("chan","second complete,正常结束被调用currentPos="+currentPos);
                    if(!mFromUser){
                        mFromUser = true;
                    }
                    //check play mode
                    int mode = checkPlayMode();
                    Log.d("chan","MODE:"+mode);
                    switch(mode){
                        case MyConstants.PlayMode.DEFAULT:
                            Log.d("chan","播放完成后，当前模式为默认播放模式");
                            if(currentPos < data.size()){
                                play(data.get(currentPos+1),currentPos+1,System.currentTimeMillis());
                            }else{
                                //如果当前播放的是最后一首，那么结束后播放第一首
                                play(data.get(0),0,System.currentTimeMillis());
                            }
                            break;
                        case MyConstants.PlayMode.SIGNAL:
                            Log.d("chan","播放完成后，当前模式为单曲循环播放模式");
                            play(data.get(currentPos),currentPos,System.currentTimeMillis());
                            break;
                        case MyConstants.PlayMode.RANDOM:

                           currentPos = DataProcess.getRandomIntegerData(data.size());
                            Log.d("chan","播放完成后，当前模式为随机播放模式destPosition:"+currentPos);
                            play(data.get(currentPos),currentPos,System.currentTimeMillis());
                            break;
                        default:
                            break;
                    }


                }
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d("chan","*****seek to complete****");
                mp.start();
            }
        });

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //如果已经扫描过了，db中有数据，如果没有数据，需要扫描，那么这里获得的就是null
        //data = intent.getBundleExtra("data").getParcelableArrayList("all_songs");
        //Log.d("chan","activity绑定service时，获取到来自于activity的所有歌曲信息.size:"+data.size());
        // register local broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyConstants.MyAction.MEDIA_SERVICE_LOCAL_BROADCAST);
        filter.addAction(MyConstants.MyAction.SEEK_BAR_CHANGE_ACTION);
        filter.addAction(MyConstants.MyAction.PLAY_CLICKED_ACTION);
        filter.addAction(MyConstants.MyAction.PREV_CLICKED_ACTION);
        filter.addAction(MyConstants.MyAction.NEXT_CLICKED_ACTION);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);

        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(mLocalBroadcastManager != null){
            if(mReceiver != null){
                mLocalBroadcastManager.unregisterReceiver(mReceiver);
            }
        }
        Log.d("chan","unbind ....");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("chan","onDestroy ....");

        timer.cancel();
        mBus.unregister(this);
        //shutDownMonitor();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    /**
     * @param song
     * @param position 歌曲在list中的位置，即在data中的位置
     */
    public void play(SongDetailEntity song,int position,long clickTime){
        currentPos = position;
        //mClickTime = clickTime;
        mClickTime = System.currentTimeMillis();
        if(mediaPlayer != null){
            try {
                //如果当前正在播放
//                mediaPlayer.reset();
                Log.d("chan","position=" + position);
                Log.d("chan", "play current song " + data.get(position).getTitle() + "position=" + position);
                //Log.d("chan","will play :"+data.get(position).getPath());
                File file = new File(data.get(position).getPath());
                if(file.exists()){
                    mSong = song;
                    //发布事件，为了在activity下显示歌曲信息（title,artist,progress）
                    Log.d(TAG,"歌曲总长度："+mediaPlayer.getDuration());
                    //设置歌曲总长度
                    mSong.setDuration(mediaPlayer.getDuration());
                    mBus.post(new MyEvent(mediaPlayer.getDuration(),-1,false,mSong));
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(mSong.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    //更改状态
                    BaseApplication app = (BaseApplication)getApplication();
                    app.setPLAYING_STATUS(MyConstants.MediaStatus.PLAYING);
                    Log.d("chan", "===================================");
                    //startMonitor();
                    handler.post(updateThread);
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


   public void onEventMainThread(MyEvent event){
           if(event != null){
               if(event.getIsDataUpdate()){
                   data.clear();
                   BaseApplication app = (BaseApplication) getApplicationContext();
                   data.addAll(app.getAllData());
               }

           }
   }

    //用来处理用户拖动seek bar事件
    public class MediaPlayerServiceBroadcastRec extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action != null){
                if(MyConstants.MyAction.SEEK_BAR_CHANGE_ACTION.equals(action)){
                    //先得到用户拖动的数据
                    int seekBarProgress = intent.getIntExtra("progress_by_user",MyConstants.CommonData.DEFAULT_VALUE_INTEGER);
                    int max = intent.getIntExtra("seek_bar_max",100);
                    Log.d("chan","receiver中得到拖动的seekBarProgress:"+seekBarProgress+"| seekbar最大值为："+max);
                    //media player seek to 到对应的位置
                    try {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                            mediaPlayer.prepare();
                        }
                        mFromUser = true;
                        int progress = mediaPlayer.getDuration() * seekBarProgress / max;
                        mediaPlayer.seekTo(progress);
                        mCurrentProgress = progress;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(MyConstants.MyAction.PLAY_CLICKED_ACTION.equals(action)){
                    Log.d("chan","PLAY BUTTON IS CLICKED ...");
                    //如果当前是播放状态，暂停播放
                    if(mediaPlayer != null && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        //更改状态
                        BaseApplication app = (BaseApplication)getApplication();
                        app.setPLAYING_STATUS(MyConstants.MediaStatus.PAUSE);
                    }else{
                        mediaPlayer.start();
                        //更改状态
                        BaseApplication app = (BaseApplication)getApplication();
                        app.setPLAYING_STATUS(MyConstants.MediaStatus.PLAYING);
                    }

                }else if(MyConstants.MyAction.PREV_CLICKED_ACTION.equals(action)){
                    Log.d("chan","PREV BUTTON IS CLICKED ...");
                    //播放前一首歌
                    if(currentPos == 0){
                        currentPos = data.size()-1;
                    }else{
                        currentPos --;
                    }
                    play(data.get(currentPos),currentPos,System.currentTimeMillis());

                }else if(MyConstants.MyAction.NEXT_CLICKED_ACTION.equals(action)){
                    Log.d("chan","NEXT BUTTON IS CLICKED ...");
                    if(currentPos == data.size()-1){
                        currentPos = 0;
                    }else{
                        currentPos ++;
                    }
                    play(data.get(currentPos),currentPos,System.currentTimeMillis());
                }
            }


        }
    }



    public int checkPlayMode(){
        SharedPreferences sp = getSharedPreferences(MyConstants.SharedPreferencesData.FILE_NAME, Activity.MODE_PRIVATE);
        return sp.getInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE,-1);
    }
}
