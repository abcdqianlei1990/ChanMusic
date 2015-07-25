package cn.chan.com.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.chan.com.adapter.SongsListAdapter;
import cn.chan.com.entity.SongDetailEntity;
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
    private View mParentView;
    private TextView mSong;
    private TextView mSinger;
    private TextView mAlbum;
    private TextView mFolder;
    private ImageView mPlayModeArrow;
    private ImageView mPlayMode;
    private TextView mPlayModeName;
    private LinearLayout mLayout1;
    private LinearLayout mLayout2;
    private LinearLayout mLayout3;
    private RecyclerView mRecyclerView;
    private SongsListAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawer;
    private Button mScanner;
    private boolean mDrawerOpened;
    private View mTop;
    private View mBottom;
    private PopupWindow pop;
    private SharedPreferences sharedPreferences;
    private int mMode;
    private boolean mBackgroundChange = false;
    private ImageView mPlayModeChecked;
    //某个歌手的所有歌名集合
    private ArrayList<SongDetailEntity> mSongDetails = new ArrayList<SongDetailEntity>();
    private ImageButton mPlay;
    private ImageButton mPrevSong;
    private ImageButton mNextSong;
    private TextView mSongTitle;
    private TextView mArtist;
    private SeekBar mSeekBar;
    private EventBus mEventBus;
    private int mPlayingStatus = MyConstants.MediaStatus.INVALID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mParentView = inflater .inflate(R.layout.local_music,null);
        setContentView(mParentView);

        initParams();
        initEvents();
        initViews();



    }

    //test data
    protected void initData()
    {
        mSongDetails = new ArrayList<SongDetailEntity>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mSongDetails.add(new SongDetailEntity((char)i+"",null,"- 张国荣",null,null,0));
        }
    }
    public void initParams(){
        //initData();
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
        mAdapter = new SongsListAdapter(mSongDetails, this);
        mRecyclerView.setAdapter(mAdapter);
        mPlayMode = (ImageView) mTop.findViewById(R.id.iv_player_mode);
        mPlayModeName = (TextView) mTop.findViewById(R.id.tv_player_mode);
        mPlayModeArrow = (ImageView) mTop.findViewById(R.id.iv_player_mode_arrow);
        mDrawer = (LinearLayout) findViewById(R.id.local_music_drawer);
        mScanner = (Button) findViewById(R.id.local_music_drawer_scanner);
        sharedPreferences = getSharedPreferences("sp_setting",Activity.MODE_PRIVATE);
        mMode = sharedPreferences.getInt("play_mode", 0);
        mPlay = (ImageButton) mBottom.findViewById(R.id.ib_play);
        mPrevSong = (ImageButton) mBottom.findViewById(R.id.ib_prev);
        mNextSong = (ImageButton) mBottom.findViewById(R.id.ib_next);
        mSongTitle = (TextView) mBottom.findViewById(R.id.tv_song_name);
        mArtist = (TextView) mBottom.findViewById(R.id.tv_song_artist);
        mSeekBar = (SeekBar) mBottom.findViewById(R.id.seekBar);
        Log.d(TAG,"current count of songs : "+mSongDetails.size());
        //Toast.makeText(this,"current count of songs : "+mSongDetails.size(),Toast.LENGTH_SHORT).show();
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
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        //initVideoView(getParent(),inflater.inflate(R.layout.video,null)).showAtLocation(mParentView, Gravity.BOTTOM,0,0);
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
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt("play_mode", mMode);
        ed.commit();
        //close service
        if(mAdapter != null){
            mAdapter.onDestroy();
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
                mSongDetails.addAll(new SongScannerImpl(this).getAllSongs());
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.ib_play:
                break;
            case R.id.ib_prev:
                break;
            case R.id.ib_next:
                break;
            default:
                break;
        }
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
            }
        });
        mLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_random_default));
                mPlayModeName.setText("随机播放");
                mMode = MyConstants.PlayMode.RANDOM;
                pop.dismiss();
            }
        });
        mLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayMode.setBackground(getResources().getDrawable(R.mipmap.ic_player_mode_single_default));
                mPlayModeName.setText("单曲循环");
                mMode = MyConstants.PlayMode.SIGNAL;
                pop.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"aaa").setIcon(R.mipmap.skin_image_scan_setting_icon_1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,1,1,"bbb").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        MediaPlayer mp = new MediaPlayer();
//        String path = "storage/emulated/0/qqmusic/song/aiwojiujiu.mp3";
//        //MediaPlayer mp = MediaPlayer.create(this,R.raw.aaa);
//        File f = new File(path);
//        try {
//            if(f.exists()){
//                mp.setDataSource(path);
//                mp.prepare();
//                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mp.start();
//                Toast.makeText(this,"file is exist !",Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(this,"file not exist ",Toast.LENGTH_LONG).show();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if(!mDrawerOpened){
            mDrawerLayout.openDrawer(mDrawer);
        }else{
            mDrawerLayout.closeDrawer(mDrawer);
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(SongDetailEntity song){
        Log.d(TAG,"获得来自于media service的信息，更新歌曲信息,duration:"+song.getDuration());
        //mSeekBar.setMax(song.getDuration());
        //将播放状态改为playing
        mPlayingStatus = MyConstants.MediaStatus.PLAYING;

        mSongTitle.setText(song.getTitle());
        mArtist.setText(song.getArtist());
    }
}
