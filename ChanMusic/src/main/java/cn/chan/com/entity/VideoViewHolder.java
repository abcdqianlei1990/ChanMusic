package cn.chan.com.entity;

import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/7/29.
 */
public class VideoViewHolder {
    private ImageButton play;
    private ImageButton prev;
    private ImageButton next;
    private TextView    title;
    private TextView    artist;
    private SeekBar     bar;

    public VideoViewHolder(ImageButton play, ImageButton prev, ImageButton next, TextView title, TextView artist, SeekBar bar) {
        this.play = play;
        this.prev = prev;
        this.next = next;
        this.title = title;
        this.artist = artist;
        this.bar = bar;
    }


    public ImageButton getPlay() {
        return play;
    }

    public void setPlay(ImageButton play) {
        this.play = play;
    }

    public ImageButton getPrev() {
        return prev;
    }

    public void setPrev(ImageButton prev) {
        this.prev = prev;
    }

    public ImageButton getNext() {
        return next;
    }

    public void setNext(ImageButton next) {
        this.next = next;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getArtist() {
        return artist;
    }

    public void setArtist(TextView artist) {
        this.artist = artist;
    }

    public SeekBar getBar() {
        return bar;
    }

    public void setBar(SeekBar bar) {
        this.bar = bar;
    }
}
