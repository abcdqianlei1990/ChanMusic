package cn.chan.com.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Administrator on 2015/7/23.
 */
public class BaseActivity extends Activity{
    private View mMediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mMediaView = LayoutInflater.from(this).inflate();
    }
}
