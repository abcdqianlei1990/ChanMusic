package cn.chan.com.util;

/**
 * Created by Administrator on 2015/7/22.
 */
public interface MyConstants {

    static class PlayMode{
        public static final int DEFAULT = 0X01;
        public static final int RANDOM = 0X02;
        public static final int SIGNAL = 0X03;

    }
    static class DataBase{
        public static final String TABLE_NAME = "songs_t";
        public static final String DATABASE_NAME = "chanMusic.db";
    }
    static class MediaControlCMD{
        public static final int PLAY = 0X04;
        public static final int PAUSE = 0X05;
        public static final int STOP = 0X06;
    }
}
