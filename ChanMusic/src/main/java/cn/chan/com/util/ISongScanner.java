package cn.chan.com.util;

import android.graphics.Bitmap;

import java.util.List;

import cn.chan.com.entity.SongDetailEntity;

/**
 * Created by Administrator on 2015/7/22.
 */
public interface ISongScanner {
    public  List<SongDetailEntity> getAllSongs();
    public Bitmap getAlbumBitmap();
}
