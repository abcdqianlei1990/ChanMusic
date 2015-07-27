package cn.chan.com.util;

import android.util.Log;

/**
 * Created by Administrator on 2015/7/27.
 */
public class DataProcess {


    public static String formatToTime(int time){
        time /= 1000;           //get second
        int min = time / 60;    //get minute
        int hour = min /60;     //get hour
        Log.d("chan","时："+hour+"分："+min);
        int second = min % 60;
        min = min % 60;
        Log.d("chan","秒："+second+"分："+min);
        return String.format("%02d:%02d", min, second);
    }

    public static String formatPath(String path){

        return path.trim();
    }
}
