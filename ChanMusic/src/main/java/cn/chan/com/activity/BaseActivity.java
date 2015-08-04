package cn.chan.com.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chan.com.adapter.SongsListAdapter;
import cn.chan.com.custormView.POPPlayingQueue;
import cn.chan.com.entity.MyEvent;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.entity.VideoViewHolder;
import cn.chan.com.service.MediaPlayService;
import cn.chan.com.util.MeasureView;
import cn.chan.com.util.MyConstants;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/23.
 */
public class BaseActivity extends Activity{
    private static final String TAG = "BaseActivity";
    private View mMediaView;
    private FrameLayout base;
    private ImageButton mPlay;
    private ImageButton mPrevSong;
    private ImageButton mNextSong;
    private SeekBar     mSeekBar;
    private TextView    mSongTitle;
    private TextView    mArtist;
    private ImageButton     mPlayingQueue;
    private POPPlayingQueue popQueue;
    private View            mParentView;
    private EventBus mEventBus;
    private int temp;
    private static MediaPlayService service;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"media play service is binded.");
            MediaPlayService.MyBinder binder = (MediaPlayService.MyBinder)iBinder;
            service = binder.getService();
            //adapterData();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentView = LayoutInflater.from(this).inflate(R.layout.base_layout,null);
        setContentView(mParentView);
        initParams();
        initBaseEvents();
        bindService();
    }
    //childrens set content view throungh the method.
    public void setSubContentView(int layoutID){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutID,null);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        base.addView(v);
        base.bringChildToFront(mMediaView);
    }

    private void initParams(){
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        base = (FrameLayout) findViewById(R.id.base_layout);
        mMediaView = findViewById(R.id.base_layout_video);
        mPlay = (ImageButton) mMediaView.findViewById(R.id.ib_play);
        mPrevSong = (ImageButton) mMediaView.findViewById(R.id.ib_prev);
        mNextSong = (ImageButton) mMediaView.findViewById(R.id.ib_next);
        mSeekBar = (SeekBar) mMediaView.findViewById(R.id.seekBar);
        mSongTitle = (TextView) mMediaView.findViewById(R.id.tv_song_name);
        mArtist = (TextView) mMediaView.findViewById(R.id.tv_song_artist);
        mPlayingQueue = (ImageButton) mMediaView.findViewById(R.id.ib_play_queue);

    }

    /**
     * 发送本地广播事件，在media player service中处理
     * @param action
     */
    private void sendLocalBroadcast(String action) {
        Intent intent = new Intent(action);
        if(MyConstants.MyAction.SEEK_BAR_CHANGE_ACTION.equals(action)){
            intent.putExtra("progress_by_user", temp);
            intent.putExtra("seek_bar_max",mSeekBar.getMax());
        }else if(MyConstants.MyAction.PLAY_CLICKED_ACTION.equals(action)){
            //do nothing
        }else if(MyConstants.MyAction.PREV_CLICKED_ACTION.equals(action)){

        }else if(MyConstants.MyAction.NEXT_CLICKED_ACTION.equals(action)){

        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void initBaseEvents(){
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("chan", "play button is clicked...");
                //如果正在播放，则暂停，反之.
                //更改状态
                BaseApplication app = (BaseApplication)getApplication();
                int status  = app.getPLAYING_STATUS();
                //如果播放队列为空，播放所有歌曲
                ArrayList<SongDetailEntity> list = app.getPlayingQueue();
                if(list == null){
                    list.addAll(app.getAllData());
                }else{
                    if(list.size() == 0){
                        list.addAll(app.getAllData());
                        Log.d("chan", "*********"+list.size());
                    }
                }
                if(status == MyConstants.MediaStatus.PLAYING){
                    mPlay.setImageResource(R.mipmap.statusbar_btn_play);
                    mPlay.setBaseline(R.id.ib_prev);
                    app.setPLAYING_STATUS(MyConstants.MediaStatus.PAUSE);
                }else if(status == MyConstants.MediaStatus.PAUSE){
                    mPlay.setImageResource(R.mipmap.statusbar_btn_pause);
                    mPlay.setBaseline(R.id.ib_prev);
                    app.setPLAYING_STATUS(MyConstants.MediaStatus.PLAYING);
                }
                sendLocalBroadcast(MyConstants.MyAction.PLAY_CLICKED_ACTION);
            }
        });
        mPrevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chan", "prev button is clicked...");
                sendLocalBroadcast(MyConstants.MyAction.PREV_CLICKED_ACTION);
            }
        });
        mNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chan", "next button is clicked...");
                sendLocalBroadcast(MyConstants.MyAction.NEXT_CLICKED_ACTION);
            }
        });
        mPlayingQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出popwindow 显示播放队列
                Log.d("chan","play queue clicked ... ");
                showPlayingQueueWindow();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //如果是用户手动调整进度
                if(b){
                    //Log.d("chan","手动滑动seek bar到"+i+"位置");
                    temp = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("chan","得到最终的拖动progress temp："+temp);
                sendLocalBroadcast(MyConstants.MyAction.SEEK_BAR_CHANGE_ACTION);
                seekBar.setProgress(temp);
            }
        });
    }
    public void onEventMainThread(MyEvent event){

        //String time = DataProcess.formatToTime(song.getDuration());

        int duration = event.getDuration();
        int progress = event.getProgress();
        SongDetailEntity song = event.getSong();

        if(progress != -1){
            //Log.d("chan","progress = "+progress);
            mSeekBar.setProgress(progress * mSeekBar.getMax() / duration);
        }
        if(song != null){
            Log.d("chan","获得来自于media service的信息，更新歌曲信息,:"+song.getTitle());
            mPlay.setImageResource(R.mipmap.statusbar_btn_pause);
            mPlay.setBaseline(R.id.ib_prev);
            mSongTitle.setText(song.getTitle());
            mArtist.setText(song.getArtist());
        }
    }

    //将bind service封装成一个方法，这样我们可以在适当的地方去调用
    //在扫描完成后再进行绑定，这样才会有数据
    public void bindService(){
        Intent intent = new Intent(this,MediaPlayService.class);
        this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
//    public void adapterData(){
//        if(service == null){
//            Log.d("chan","service 为空");
//        }
//
//        mAdapter = new SongsListAdapter(this,service,mAlbumart);
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
//        BaseApplication app = (BaseApplication) getApplication();
//        ArrayList<SongDetailEntity> allData = app.getAllData();
//    }
    public MediaPlayService getService(){
        return service;
    }

    /**
     * 弹出window，显示播放队列
     */
    private void showPlayingQueueWindow() {
        if(popQueue != null){
            if(popQueue.isShowing()){
                popQueue.dismiss();
                popQueue = null;
            }
        }

        popQueue = new POPPlayingQueue(this);
        popQueue.showAtLocation(mParentView,mMediaView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close service
        if(service != null){
            unbindService(conn);
        }
        mEventBus.unregister(this);
    }

}
