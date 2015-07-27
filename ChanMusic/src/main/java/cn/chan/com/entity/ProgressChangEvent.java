package cn.chan.com.entity;

/**
 * Created by Administrator on 2015/7/27.
 */
public class ProgressChangEvent {
    private int duration;
    private int progress;

    public ProgressChangEvent(int duration, int progress) {
        this.duration = duration;
        this.progress = progress;
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
}
