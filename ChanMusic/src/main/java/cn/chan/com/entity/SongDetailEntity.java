package cn.chan.com.entity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongDetailEntity {
    private String title;
    private String path;
    private String artist;
    private String duration;
    private String album;
    private int    progress;
    public SongDetailEntity(String title, String path, String artist, String duration, String album,int progress) {
        this.title = title;
        this.path = path;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
        this.progress = progress;
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

    public String getDuration() {
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

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
