package cn.chan.com.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/27.
 */
public class MyEvent {
    private int duration;
    private int progress;
    private boolean     isDataUpdate;
    private SongDetailEntity song;

    public SongDetailEntity getSong() {
        return song;
    }

    public void setSong(SongDetailEntity song) {
        this.song = song;
    }


    public MyEvent(int duration, int progress, boolean     isDataUpdate,SongDetailEntity song) {
        this.duration = duration;
        this.progress = progress;
        this.isDataUpdate = isDataUpdate;
        this.song = song;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getIsDataUpdate() {
        return isDataUpdate;
    }

    public void setIsDataUpdate(boolean isDataUpdate) {
        this.isDataUpdate = isDataUpdate;
    }
}
