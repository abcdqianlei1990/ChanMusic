package cn.chan.com.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.chan.com.activity.LocalMusicActivity;
import cn.chan.com.activity.R;

/**
 * Created by Administrator on 2015/7/20.
 */
public class LocalMusicPartFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "LocalMusicPartFragment";
    private TextView mAllSongs;
    private ImageButton mPlayBtn;
    private ImageButton mMyLike;
    private ImageButton mMyList;
    private ImageButton mMyDown;
    private ImageButton mLatestPlay;
    private View mView;
    private View mImageText;
    private Context mContext;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.local_music_part_fragment,container,false);
        initParams();
        initEvents();
        return mView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void initParams(){
        mAllSongs = (TextView) mView.findViewById(R.id.tv_local_music_all_songs);
        mPlayBtn = (ImageButton) mView.findViewById(R.id.ib_start);
        mMyLike = (ImageButton) mView.findViewById(R.id.ib_my_like);
        mMyList = (ImageButton) mView.findViewById(R.id.ib_my_list);
        mMyDown = (ImageButton) mView.findViewById(R.id.ib_my_download);
        mLatestPlay = (ImageButton) mView.findViewById(R.id.ib_latest_play);
        mImageText =mView.findViewById(R.id.image_up_text_down);
        mContext = getActivity();
    }
    public void initEvents(){
        mAllSongs.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mMyLike.setOnClickListener(this);
        mMyList.setOnClickListener(this);
        mMyDown.setOnClickListener(this);
        mLatestPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tv_local_music_all_songs:
                //Toast.makeText(mContext,"all songs clicked...",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"all songs clicked...");
                Intent intent = new Intent(mContext, LocalMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_start:
                Toast.makeText(mContext,"start to play...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_my_like:
                Toast.makeText(mContext,"my like clicked...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_my_list:
                Toast.makeText(mContext,"my list clicked...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_my_download:
                Toast.makeText(mContext,"my download clicked...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_latest_play:
                Toast.makeText(mContext,"latest play clicked...",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
