package cn.chan.com.activity;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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
        return playingQueue;
    }

    public void setPlayingQueue(ArrayList<SongDetailEntity> playingQueue) {
        this.playingQueue = playingQueue;
    }
    
    private void initData(){
        DBAction dBAction = new DBAction(this);
        allData.clear();
        allData = dBAction.querySongs();
    }


    public void addToPlayingQueue(SongDetailEntity song){
        playingQueue.add(song);
    }
}
