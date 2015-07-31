package cn.chan.com.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import cn.chan.com.entity.VideoViewHolder;
import cn.chan.com.util.MeasureView;

/**
 * Created by Administrator on 2015/7/23.
 */
public class BaseActivity extends Activity{
    private static final String TAG = "BaseActivity";
    private View mMediaView;
    private PopupWindow mPopWindow;
    private VideoViewHolder mHolder;
    private FrameLayout base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        //initVideoViewEvents();
    }
//    public void initVideoViewPop(){
//        LayoutInflater l = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
//       mMediaView = l.inflate(R.layout.video,null);
//        mPopWindow = new PopupWindow(mMediaView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
//        mPopWindow.setTouchable(true);
//        mPopWindow.setOutsideTouchable(true);
//        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
//        //mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
//        mPopWindow.update();
//        Log.d(TAG,"11111111111111111111111111111111111");
//        mPopWindow.showAtLocation(base,Gravity.BOTTOM,0,0);
//        Log.d(TAG,"222222222222222222222222222222222222");
//    }
    //设置子Activity的布局
    public void setSubContentView(int layoutID){
        base = (FrameLayout) findViewById(R.id.base_layout);
        mMediaView = findViewById(R.id.base_layout_video);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutID,null);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        base.addView(v);
        base.bringChildToFront(mMediaView);
    }

    public void initVideoViewEvents(){
        mHolder.getPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chan","play button is clicked...");
                //如果歌曲是播放状态
            }
        });
        mHolder.getPrev().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chan","prev button is clicked...");
            }
        });
        mHolder.getNext().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chan","next button is clicked...");
            }
        });
    }
}
