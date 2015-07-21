package cn.chan.com.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;



public class MainActivity extends Activity implements View.OnClickListener{
    private ActionBar mActionBar;
    private Context mContext;
    private ImageView mIvListen;
    private ImageView mIvSee;
    private ImageView mIvSing;
    private ImageView mIvSetting;
    private View mCustomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParams();
        initViews();
        initEvents();
    }

    private void initParams() {
        mContext = this;
        mActionBar = getActionBar();
        mCustomView = LayoutInflater.from(mContext).inflate(R.layout.header,null);
        mIvListen = (ImageView) mCustomView.findViewById(R.id.header_iv_2);
        mIvSee = (ImageView) mCustomView.findViewById(R.id.header_iv_3);
        mIvSing = (ImageView) mCustomView.findViewById(R.id.header_iv_4);
        mIvSetting = (ImageView) mCustomView.findViewById(R.id.header_iv_5);
    }
    private void initEvents() {
        mIvListen.setOnClickListener(this);
        mIvSee.setOnClickListener(this);
        mIvSing.setOnClickListener(this);
        mIvSetting.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
    private void initViews() {
        if(mActionBar != null){
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(mCustomView);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.header_iv_2:
                Toast.makeText(this,"listen is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.header_iv_3:
                Toast.makeText(this,"see is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.header_iv_4:
                Toast.makeText(this,"sing is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.header_iv_5:
                Toast.makeText(this,"setting is clicked",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
