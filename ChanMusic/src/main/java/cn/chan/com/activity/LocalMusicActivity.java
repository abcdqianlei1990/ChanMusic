package cn.chan.com.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/7/21.
 */
public class LocalMusicActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "LocalMusicActivity";
    private TextView mSong;
    private TextView mSinger;
    private TextView mAlbum;
    private TextView mFolder;
    private View mTop;
    private View mBottom;

    private boolean mBackgroundChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_music);
        initParams();
        initEvents();
    }
    public void initParams(){
        mTop = findViewById(R.id.local_music_top_part);
        mBottom = findViewById(R.id.local_music_video);
        mSong = (TextView) mTop.findViewById(R.id.song_select);
        mSinger = (TextView) mTop.findViewById(R.id.singer_select);
        mAlbum = (TextView) mTop.findViewById(R.id.album_select);
        mFolder = (TextView) mTop.findViewById(R.id.folder_select);
    }

    public void initEvents(){
        mSong.setOnClickListener(this);
        mSinger.setOnClickListener(this);
        mAlbum.setOnClickListener(this);
        mFolder.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
