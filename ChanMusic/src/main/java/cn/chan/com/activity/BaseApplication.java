package cn.chan.com.activity;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;

import cn.chan.com.dataUtil.DBAction;
import cn.chan.com.dataUtil.DBAdapter;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MyConstants;

/**
 * Created by Administrator on 2015/7/31.
 */
public class BaseApplication extends Application {

    private int PLAYING_STATUS = MyConstants.MediaStatus.INVALID;
    private ArrayList<SongDetailEntity> allData = new ArrayList<SongDetailEntity>();
    private ArrayList<SongDetailEntity> playingQueue = new ArrayList<SongDetailEntity>();
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }


    public int getPLAYING_STATUS() {
        return PLAYING_STATUS;
    }

    public void setPLAYING_STATUS(int PLAYING_STATUS) {
        this.PLAYING_STATUS = PLAYING_STATUS;
    }

    public ArrayList<SongDetailEntity> getAllData() {
        return allData;
    }

    public void setAllData(ArrayList<SongDetailEntity> allData) {
        this.allData = allData;
    }

    public ArrayList<SongDetailEntity> getPlayingQueue() {
        //test 为了测试playing queue中操作的数据是不是同一块内存
//        for(SongDetailEntity song:playingQueue){
//            Log.d("chan","test:"+song.getTitle()+":"+song.getFavor()+" | ");
//        }
        //Log.d("chan","test:"+playingQueue.size());
        return playingQueue;
    }

    public void setPlayingQueue(ArrayList<SongDetailEntity> queue) {
//        if(this.playingQueue != null){
//            this.playingQueue.clear();
//        }
        this.playingQueue.addAll(queue);
        //this.playingQueue.addAll(playingQueue);
    }
    
    private void initData(){
        DBAction dBAction = new DBAction(this);
        allData.clear();
        allData = dBAction.querySongs();
    }


    public void addToPlayingQueue(SongDetailEntity song){
        playingQueue.add(song);
    }

    public void clearPlayingQueue() {
        playingQueue.clear();
    }


}
