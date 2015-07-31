package cn.chan.com.util;

/**
 * Created by Administrator on 2015/7/22.
 */
public interface MyConstants {

    static class CommonData{
        public static final int DEFAULT_VALUE_INTEGER = -1;
    }
    static class PlayMode{
        public static final int DEFAULT = 0X001;
        public static final int RANDOM = 0X002;
        public static final int SIGNAL = 0X003;

    }
    static class DataBase{
        public static final String TABLE_NAME = "songs_t";
        public static final String DATABASE_NAME = "chanMusic.db";
    }
    static class MediaControlCMD{
        public static final int PLAY = 0X004;
        public static final int PAUSE = 0X005;
        public static final int STOP = 0X006;
    }

    static class MediaStatus{
        public static final int PLAYING = 0X007;
        public static final int PAUSE = 0X008;
        public static final int COMPLETE = 0X009;
        public static final int STOP = 0X010;
        public static final int INVALID = 0X011;
    }
    static class MyAction{
        public static final String MEDIA_SERVICE = "cn.chan.com.service.MediaPlayService";
        public static final String MEDIA_SERVICE_LOCAL_BROADCAST = "cn.chan.com.service.MediaPlayService.MediaPlayerServiceBroadcastRec";
        public static final String SEEK_BAR_CHANGE_ACTION = "seek_bar_change_action";
        public static final String PLAY_CLICKED_ACTION = "play_clicked_action";
        public static final String PREV_CLICKED_ACTION = "prev_clicked_action";
        public static final String NEXT_CLICKED_ACTION = "next_clicked_action";
    }
    static class SharedPreferencesData{
        public static final String FILE_NAME = "music_setting";
        public static final String FIELD_NAME_PLAY_MODE = "play_mode";
    }
}
