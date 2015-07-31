package cn.chan.com.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.chan.com.activity.BaseApplication;
import cn.chan.com.activity.R;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.service.MediaPlayService;
import cn.chan.com.util.MyConstants;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.SongsListItemViewHolder>{
    private static final String TAG = "SongsListAdapter";
    private static ArrayList<SongDetailEntity> data = new ArrayList<SongDetailEntity>();
    private Context context;
    private static MediaPlayService service;

    public SongsListAdapter(Context context,MediaPlayService service,ImageView view){

        this.context = context;
        this.service = service;
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        ArrayList<SongDetailEntity> allData = app.getAllData();
        this.data = allData;
        //Log.d("chan","Adapter构造方法,获取内存中数据，size："+allData.size());
    }
    @Override
    public SongsListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        SongsListItemViewHolder holder = new SongsListItemViewHolder(LayoutInflater.from(context).inflate(R.layout.all_music_item,viewGroup,false),context);
        return holder;
    }

    @Override
    public void onBindViewHolder(SongsListItemViewHolder songsListItemViewHolder, int i) {
        songsListItemViewHolder.add.setBackground(context.getResources().getDrawable(R.mipmap.fx_ic_img_plus));
        songsListItemViewHolder.songDetail.setText(data.get(i).getTitle()+data.get(i).getArtist());
        //songsListItemViewHolder.action.setBackground(context.getResources().getDrawable(R.mipmap.game_icon_expand_down));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SongsListItemViewHolder extends RecyclerView.ViewHolder{
        ImageButton add;
        TextView    songDetail;
        //ImageButton action;
        Context context;

        public SongsListItemViewHolder(View itemView, final Context context) {
            super(itemView);
            this.context = context;
            add = (ImageButton) itemView.findViewById(R.id.all_music_item_add);
            songDetail = (TextView) itemView.findViewById(R.id.all_music_item_song_detail);
            //action = (ImageButton) itemView.findViewById(R.id.all_music_item_action);
            initEvents();
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("chan", "item is clicked ...");
//                    //play the song
//                    if(service != null){
//                        service.play(data.get(getLayoutPosition()),getLayoutPosition(),System.currentTimeMillis());
//                    }else{
//                        Log.d("chan","service is NULL !");
//                    }
//                }
//            });
        }
        public void initEvents(){

            //加入播放队列并播放
            songDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        if(service != null){
                            service.play(data.get(getLayoutPosition()),getLayoutPosition(),System.currentTimeMillis());
                        }else{
                            Log.d("chan","service is NULL !");
                        }
                }
            });
            //仅加入播放队列
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"add button is clicked,the song will add to playing queue.",Toast.LENGTH_SHORT).show();
                  Log.d("chan","add button is clicked,the song will add to playing queue.");
                    //将该歌曲添加到播放队列 playing queue
                    BaseApplication app = (BaseApplication) context.getApplicationContext();
                    ArrayList<SongDetailEntity> allData = app.getAllData();
                    app.addToPlayingQueue(allData.get(getLayoutPosition()));    //add to playing queue
                }
            });
        }
    }
}

