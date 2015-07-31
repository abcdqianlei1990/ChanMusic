package cn.chan.com.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.chan.com.adapter.SongsListAdapter;
import cn.chan.com.dataUtil.DBAction;
import cn.chan.com.dataUtil.DBAdapter;
import cn.chan.com.entity.MyEvent;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.entity.VideoViewHolder;
import cn.chan.com.service.MediaPlayService;
import cn.chan.com.util.MeasureView;
import cn.chan.com.util.MyConstants;
import cn.chan.com.util.SongScannerImpl;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/21.
 */
public class LocalMusicActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "LocalMusicActivity";
    private Context         mContext;
    private View            mParentView;
    private TextView        mSong;
    private TextView        mSinger;
    private TextView        mAlbum;
    private TextView        mFolder;
    private ImageView       mPlayModeArrow;
    private ImageView       mPlayMode;
    private TextView        mPlayModeName;
    private LinearLayout    mLayout1;
    private LinearLayout    mLayout2;
    private LinearLayout    mLayout3;
    private RecyclerView    mRecyclerView;
    private SongsListAdapter mAdapter;
    private DrawerLayout    mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout    mDrawer;
    private Button          mScanner;
    private boolean        mDrawerOpened;
    private View            mTop;
    private View            mBottom;
    private PopupWindow     pop;
    private SharedPreferences sharedPreferences;
    private int             mMode;
    private boolean mBackgroundChange = false;
    private ImageView       mPlayModeChecked;
    //某个歌手的所有歌名集合
    private ArrayList<SongDetailEntity> mSongDetails = new ArrayList<SongDetailEntity>();
    private ImageButton     mPlay;
    private ImageButton     mPrevSong;
    private ImageButton     mNextSong;
    private ImageButton     mPlayingQueue;
    private ImageView       mAlbumart;
    private TextView        mSongTitle;
    private TextView        mArtist;
    private SeekBar         mSeekBar;
    private EventBus        mEventBus;
    private DBAction        mDBAction;
    int temp;
    private static MediaPlayService service;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"media play service is binded.");
            MediaPlayService.MyBinder binder = (MediaPlayService.MyBinder)iBinder;
            service = binder.getService();
            adapterData();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mParentView = inflater .inflate(R.layout.local_music,null);
        setContentView(mParentView);

        initParams();
        initEvents();
        initViews();
        bindService();
    }

    public void initParams(){
        //initData();
        mContext = this;
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mTop = findViewById(R.id.local_music_top_part);
        mBottom = findViewById(R.id.local_music_video);
        mSong = (TextView) mTop.findViewById(R.id.song_select);
        mSinger = (TextView) mTop.findViewById(R.id.singer_select);
        mAlbum = (TextView) mTop.findViewById(R.id.album_select);
        mFolder = (TextView) mTop.findViewById(R.id.folder_select);
        mRecyclerView = (RecyclerView) findViewById(R.id.local_music_recycler_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDBAction = new DBAction(this);
        mPlayMode = (ImageView) mTop.findViewById(R.id.iv_player_mode);
        mPlayModeName = (TextView) mTop.findViewById(R.id.tv_player_mode);
        mPlayModeArrow = (ImageView) mTop.findViewById(R.id.iv_player_mode_arrow);
        mDrawer = (LinearLayout) findViewById(R.id.local_music_drawer);
        mScanner = (Button) findViewById(R.id.local_music_drawer_scanner);
        sharedPreferences = getSharedPreferences(MyConstants.SharedPreferencesData.FILE_NAME,Activity.MODE_PRIVATE);
        mMode = sharedPreferences.getInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE, 0);
        mPlay = (ImageButton) mBottom.findViewById(R.id.ib_play);
        mPrevSong = (ImageButton) mBottom.findViewById(R.id.ib_prev);
        mNextSong = (ImageButton) mBottom.findViewById(R.id.ib_next);
        mPlayingQueue = (ImageButton) mBottom.findViewById(R.id.ib_play_queue);
        mSongTitle = (TextView) mBottom.findViewById(R.id.tv_song_name);
        //mAlbumart = (ImageView) mBottom.findViewById(R.id.video_albumart);
        mArtist = (TextView) mBottom.findViewById(R.id.tv_song_artist);
        mSeekBar = (SeekBar) mBottom.findViewById(R.id.seekBar);
        Log.d(TAG,"current count of songs : "+mSongDetails.size());
    }

    public void adapterData(){
        if(service == null){
            Log.d("chan","service 为空");
        }

        mAdapter = new SongsListAdapter(this,service,mAlbumart);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        BaseApplication app = (BaseApplication) getApplication();
        ArrayList<SongDetailEntity> allData = app.getAllData();
    }
    public void initEvents(){
        mSong.setOnClickListener(this);
        mSinger.setOnClickListener(this);
        mAlbum.setOnClickListener(this);
        mFolder.setOnClickListener(this);
        mPlayModeArrow.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mPrevSong.setOnClickListener(this);
        mNextSong.setOnClickListener(this);
        mPlayingQueue.setOnClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_launcher, R.string.open,
                R.string.close)
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                mDrawerOpened = false;
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                mDrawerOpened = true;
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mScanner.setOnClickListener(this);

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
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public void initViews(){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        int topPartHeight = new MeasureView(mTop).getMeasuredHeight();
        Log.d(TAG,"top part's height = "+topPartHeight);
        params.setMargins(0,topPartHeight,0,0);
        mRecyclerView.setLayoutParams(params);
        Log.d(TAG,"current play mode is : "+mMode);
        switch (mMode){
            case MyConstants.PlayMode.DEFAULT:
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_all_default));
                mPlayModeName.setText("顺序播放");
                break;
            case MyConstants.PlayMode.RANDOM:
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_random_default));
                mPlayModeName.setText("随机播放");
                break;
            case MyConstants.PlayMode.SIGNAL:
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_single_default));
                mPlayModeName.setText("单曲循环");
                break;
            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //save data
        Log.d(TAG,"SAVE PLAY MODE ...");

        //close service
        if(service != null){
            unbindService(conn);
        }
        mEventBus.unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.song_select:
               mSong.setBackground(getResources().getDrawable(R.drawable.kg_tab_left_select));
               mSinger.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
               mAlbum.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
               mFolder.setBackground(getResources().getDrawable(R.drawable.kg_tab_right));

            break;
            case R.id.singer_select:
                Log.d(TAG,"singer_select is clicked...");
                mSong.setBackground(getResources().getDrawable(R.drawable.kg_tab_left));
                mSinger.setBackground(getResources().getDrawable(R.drawable.kg_tab_center_select));
                mAlbum.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
                mFolder.setBackground(getResources().getDrawable(R.drawable.kg_tab_right));
                break;
            case R.id.album_select:
                Log.d(TAG,"album_select is clicked...");
                mSong.setBackground(getResources().getDrawable(R.drawable.kg_tab_left));
                mSinger.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
                mAlbum.setBackground(getResources().getDrawable(R.drawable.kg_tab_center_select));
                mFolder.setBackground(getResources().getDrawable(R.drawable.kg_tab_right));
                break;
            case R.id.folder_select:
                Log.d(TAG,"folder_select is clicked...");
                mSong.setBackground(getResources().getDrawable(R.drawable.kg_tab_left));
                mSinger.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
                mAlbum.setBackground(getResources().getDrawable(R.drawable.kg_tab_center));
                mFolder.setBackground(getResources().getDrawable(R.drawable.kg_tab_right_select));
                break;
            case R.id.iv_player_mode_arrow:
                mPlayModeArrow.setBackground(getResources().getDrawable(R.mipmap.kg_group_arrow_down));
                View v = LayoutInflater.from(this).inflate(R.layout.pop_play_mode,null);
                pop = new PopupWindow(v, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
                initPopViews(v);
                initPopEvents();
                pop.setTouchable(true);
                pop.setOutsideTouchable(true);
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.update();
                pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mPlayModeArrow.setBackground(getResources().getDrawable(R.mipmap.kg_group_arrow_up));
                    }
                });
                pop.showAsDropDown(view, 10, 10);
                break;
            case R.id.local_music_drawer_scanner:
                //Toast.makeText(this,"扫描歌曲",Toast.LENGTH_SHORT).show();
                if(mSongDetails.size() != 0){
                    Log.d("chan","扫描时:mSongDetails先清空，size="+mSongDetails.size());
                    mSongDetails.clear();
                }
                mSongDetails.addAll(new SongScannerImpl(this).getAllSongs());
                Log.d("chan","扫描结束,成功扫描到 "+mSongDetails.size()+" 条数据");
                updateData(mSongDetails);
                //插入数据库
                mDBAction.resetTable();
                mDBAction.addSongs(mSongDetails);
                //将数据传送到service
                mEventBus.post(new MyEvent(-1,-1,true,null));
                adapterData();
                break;
            case R.id.ib_play:
                //如果正在播放，则暂停，反之.
                //更改状态
                BaseApplication app = (BaseApplication)getApplication();
                int status  = app.getPLAYING_STATUS();
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
                break;
            case R.id.ib_prev:
                sendLocalBroadcast(MyConstants.MyAction.PREV_CLICKED_ACTION);
                break;
            case R.id.ib_next:
                sendLocalBroadcast(MyConstants.MyAction.NEXT_CLICKED_ACTION);
                break;
            case R.id.ib_play_queue:
                //弹出popwindow 显示播放队列
                Log.d("chan","play queue clicked ... ");
                showPlayingQueueWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 弹出window，显示播放队列
     */
    private void showPlayingQueueWindow() {
        PopupWindow window = new PopupWindow();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_playing_queue,null,false);
        window.setContentView(view);
        window.isOutsideTouchable();
        //window.showAtLocation(mBottom, Gravity.TOP,0,0);
        window.showAsDropDown(mBottom);
    }

    public void initPopViews(View v){
        mLayout1 = (LinearLayout) v.findViewById(R.id.pop_layout_1);
        mLayout2 = (LinearLayout) v.findViewById(R.id.pop_layout_2);
        mLayout3 = (LinearLayout) v.findViewById(R.id.pop_layout_3);
        switch (mMode){
            case MyConstants.PlayMode.DEFAULT:
                mLayout1.findViewById(R.id.pop_play_mode_1).setVisibility(View.VISIBLE);
                break;
            case MyConstants.PlayMode.RANDOM:
                mLayout2.findViewById(R.id.pop_play_mode_2).setVisibility(View.VISIBLE);
                break;
            case MyConstants.PlayMode.SIGNAL:
                mLayout3.findViewById(R.id.pop_play_mode_3).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }
    public void initPopEvents(){
        mLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_all_default));
                mPlayModeName.setText("顺序播放");
                mMode = MyConstants.PlayMode.DEFAULT;
                pop.dismiss();
                saveMode();
            }
        });
        mLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_random_default));
                mPlayModeName.setText("随机播放");
                mMode = MyConstants.PlayMode.RANDOM;
                pop.dismiss();
                saveMode();
            }
        });
        mLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_single_default));
                mPlayModeName.setText("单曲循环");
                mMode = MyConstants.PlayMode.SIGNAL;
                pop.dismiss();
                saveMode();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    //保存播放模式
    public void saveMode(){
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE, mMode);
        ed.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"aaa").setIcon(R.mipmap.skin_image_scan_setting_icon_1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,1,1,"bbb").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!mDrawerOpened){
            mDrawerLayout.openDrawer(mDrawer);
        }else{
            mDrawerLayout.closeDrawer(mDrawer);
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("chan","local activity ACTION_DOWN ");
            break;
            case MotionEvent.ACTION_UP:
                Log.d("chan","local activity ACTION_UP ");
            break;
        }
        return super.onTouchEvent(event);
    }
    //将bind service封装成一个方法，这样我们可以在适当的地方去调用
    //在扫描完成后再进行绑定，这样才会有数据
    public void bindService(){
        Intent intent = new Intent(this,MediaPlayService.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("all_songs",mSongDetails);
//        intent.putExtra("data",bundle);

        this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    //扫描后更新内存中数据
    public void updateData(ArrayList<SongDetailEntity> list){
        BaseApplication app = (BaseApplication) getApplication();
        app.setAllData(list);
        //Log.d("chan","更新内存中的数据");
    }
}
