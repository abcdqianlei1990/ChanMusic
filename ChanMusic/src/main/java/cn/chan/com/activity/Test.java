package cn.chan.com.activity;

import android.os.Bundle;

/**
 * Created by Administrator on 2015/7/29.
 */
public class Test extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContentView(R.layout.local_music);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
