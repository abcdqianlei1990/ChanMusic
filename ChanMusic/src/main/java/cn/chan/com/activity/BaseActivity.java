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
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2015/7/23.
 */
public class BaseActivity extends Activity{
    private static final String TAG = "BaseActivity";
    //private View mMediaView;
    private PopupWindow mPopWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public PopupWindow initVideoView(Context context,View mMediaView){
        LayoutInflater l = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
       // mMediaView = l.inflate(R.layout.video,null);
        mPopWindow = new PopupWindow(mMediaView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        mPopWindow.setTouchable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mPopWindow.update();
        Log.d(TAG,"11111111111111111111111111111111111");
        //mPopWindow.showAtLocation(view,Gravity.BOTTOM,0,0);
        Log.d(TAG,"222222222222222222222222222222222222");
        return mPopWindow;
    }
}
