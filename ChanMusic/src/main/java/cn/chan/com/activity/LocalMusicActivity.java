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
import cn.chan.com.custormView.POPPlayingQueue;
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
    private PopupWindow     pop;
    private SharedPreferences sharedPreferences;
    private int             mMode;
    private boolean mBackgroundChange = false;
    private ImageView       mPlayModeChecked;
    private EventBus mEventBus;
    //某个歌手的所有歌名集合
    private ArrayList<SongDetailEntity> mSongDetails = new ArrayList<SongDetailEntity>();
    private ImageView       mAlbumart;
    private DBAction        mDBAction;
    private static MediaPlayService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        mParentView = inflater .inflate(R.layout.local_music,null);
        //setContentView(mParentView);
        setSubContentView(R.layout.local_music);
        initParams();
        initEvents();
        initViews();
    }

    public void initParams(){
        //initData();
        mContext = this;
        service = getService();
        mEventBus = EventBus.getDefault();
        mTop = findViewById(R.id.local_music_top_part);
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
        BaseApplication app = (BaseApplication) getApplication();
        mSongDetails = app.getAllData();
    }

    public void adapterData(){
        if(service == null){
            Log.d("chan","service 为空");
        }

        mAdapter = new SongsListAdapter(this,service,mAlbumart);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
//        BaseApplication app = (BaseApplication) getApplication();
//        ArrayList<SongDetailEntity> allData = app.getAllData();
    }
    public void initEvents(){
        mSong.setOnClickListener(this);
        mSinger.setOnClickListener(this);
        mAlbum.setOnClickListener(this);
        mFolder.setOnClickListener(this);
        mPlayModeArrow.setOnClickListener(this);

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

    }
    @Override
    protected void onResume() {
        super.onResume();
        adapterData();
        mMode = sharedPreferences.getInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE, 0);
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
        //menu.add(0,1,1,"aaa").setIcon(R.mipmap.skin_image_scan_setting_icon_1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(0,1,1,"bbb").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
    //扫描后更新内存中数据
    public void updateData(ArrayList<SongDetailEntity> list){
        BaseApplication app = (BaseApplication) getApplication();
        app.setAllData(list);
        //Log.d("chan","更新内存中的数据");
    }


}
