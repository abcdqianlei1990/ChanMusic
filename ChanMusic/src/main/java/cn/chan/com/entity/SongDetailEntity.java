package cn.chan.com.entity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongDetailEntity implements Parcelable{
    private String  title;
    private String  path;
    private String  artist;
    private int     duration;
    private String  album;
    private int     progress;
    private byte [] bmp;    //icon
    private int     favor;  //1:true 0:false
    private int     inQueue; //1:true 0:false
    public SongDetailEntity(String title, String path, String artist, int duration, String album,int progress,byte [] bmp,int favor,int inQueue) {
        this.title = title;
        this.path = path;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
        this.progress = progress;
        this.bmp = bmp;
        this.favor = favor;
        this.inQueue = inQueue;
    }


    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public int getProgress() {
        return progress;
    }

    public void setSongName(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getFavor() {
        return favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }

    public int getInQueue() {
        return inQueue;
    }

    public void setInQueue(int inQueue) {
        this.inQueue = inQueue;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeString(artist);
        parcel.writeInt(duration);
        parcel.writeString(album);
        parcel.writeInt(progress);
        parcel.writeInt(favor);
        parcel.writeInt(inQueue);
        //parcel.writeByteArray(bmp);
    }

    public SongDetailEntity(Parcel in){
        //顺序要和writeToParcel写的顺序一样
        title = in.readString();
        path = in.readString();
        artist = in.readString();
        duration = in.readInt();
        album = in.readString();
        progress = in.readInt();
        favor = in.readInt();
        inQueue = in.readInt();
        //in.readByteArray(bmp);
    }

    public static final Creator CREATOR = new Creator<SongDetailEntity>() {
        @Override
        public SongDetailEntity createFromParcel(Parcel parcel) {
            return new SongDetailEntity(parcel);
        }

        @Override
        public SongDetailEntity[] newArray(int i) {
            return new SongDetailEntity[i];
        }
    };

    public byte [] getBmp() {
        return bmp;
    }

    public void setBmp(byte [] bmp) {
        this.bmp = bmp;
    }
}
