package cn.chan.com.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.chan.com.activity.LocalMusicActivity;
import cn.chan.com.entity.SongDetailEntity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongScannerImpl implements ISongScanner {
    private static final String TAG = "SongScannerImpl";
    private Context context;
    private ArrayList<SongDetailEntity> mSongs = new ArrayList<SongDetailEntity>();

    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public  SongScannerImpl(Context context){
        this.context = context;

    }
    @Override
    public ArrayList<SongDetailEntity> getAllSongs() {
        final Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                while(cursor.moveToNext()){
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)).trim().toString();
                    //String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    //Uri uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
                    //Log.d(TAG,title+" | "+path);
                    //此处歌曲的总长度duration和播放进度，初始化为0
                    mSongs.add(new SongDetailEntity(title, path, artist, 0, album,0,null,0,0));
                    int albumid = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    //mSongs.add(new SongDetailEntity(title, path, artist, 0, album,0,getArtwork(context,1,albumid,false,false)));
                    //Log.d("chan","getAllSongs --> getImage(cursor).length = "+getImage(cursor).length);
                }
        cursor.close();
        return mSongs;
    }

    @Override
    public Bitmap getAlbumBitmap() {
        return null;
    }

    private String getAlbumArt(Cursor c,int album_id) {
        //String mUriAlbums = "content://media/external/audio/albums";
        Uri mUriAlbums =Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(mUriAlbums, album_id);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // album_art = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ALBUM_ART));
        //MediaStore.Audio.AlbumColumns.ALBUM_ART
        String[] projection = new String[] { android.provider.MediaStore.Audio.AlbumColumns.ALBUM_ART };
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        Log.d("chan","cur.getCount()："+cur.getCount());
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);

        }
        cur.close();
        return album_art;
    }

    private byte [] getImage(Cursor cursor){
        byte [] array = null;
        long album_id = 0;
        Uri mUriAlbums = null;
        Uri albumUri = null;
        //专辑ID
        album_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        mUriAlbums = Uri .parse("content://media/external/audio/albumart");
        albumUri = ContentUris.withAppendedId(mUriAlbums, album_id);
        //Log.d("chan","album_id："+album_id+"| albumArtUri :"+albumArtUri);
        Bitmap bitmap;
        try {
//            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
//            MediaStore.Images.Media.query(context.getContentResolver(),albumArtUri,null);
//            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
//            array = bos.toByteArray();
//            Log.d("chan","album_id："+album_id+"| albumArtUri :"+albumArtUri);
            InputStream is = context.getContentResolver().openInputStream(albumUri);
            if(null != is) {
                bitmap = BitmapFactory.decodeStream(is);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
//                array = bos.toByteArray();
                Log.d("chan","album_id："+album_id+"| albumArtUri :"+albumUri+"| array.length=");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid){
        Bitmap bm = null;
        if(albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumid < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    public static byte [] getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small){
        if(album_id < 0) {
            if(song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if(bm != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    byte [] array = bos.toByteArray();
                    return array;
                }
            }
//            if(allowdefalut) {
//                return getDefaultArtwork(context, small);
//            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if(uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(in, null, options);
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
                if(small){
                    options.inSampleSize = computeSampleSize(options, 40);
                } else{
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,100,bos);
                byte [] array = bos.toByteArray();
                Log.d("chan","########"+array.length);
                return array;
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if(bm != null) {
                    if(bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
//                        if(bm == null && allowdefalut) {
//                            return getDefaultArtwork(context, small);
//                        }
                    }
                }
//                else if(allowdefalut) {
//                    bm = getDefaultArtwork(context, small);
//                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,100,bos);
                byte [] array = bos.toByteArray();
                return array;
            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if(candidate == 0) {
            return 1;
        }
        if(candidate > 1) {
            if((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if(candidate > 1) {
            if((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }
}
