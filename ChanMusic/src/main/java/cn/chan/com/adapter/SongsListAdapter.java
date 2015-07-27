package cn.chan.com.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG,"media play service is binded.");
            MediaPlayService.MyBinder binder = (MediaPlayService.MyBinder)iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public SongsListAdapter(ArrayList<SongDetailEntity> data,Context context){
        Log.d(TAG,"Adapter构造方法");
        this.data = data;
        this.context = context;
        this.service = service;

    }
    //将bind service封装成一个方法，这样我们可以在适当的地方去调用
    //在扫描完成后再进行绑定，这样才会有数据
    public void bindService(){
        Intent intent = new Intent(context,MediaPlayService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("all_songs",data);
        intent.putExtra("data",bundle);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
        songsListItemViewHolder.action.setBackground(context.getResources().getDrawable(R.mipmap.game_icon_expand_down));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SongsListItemViewHolder extends RecyclerView.ViewHolder{
        ImageButton add;
        TextView    songDetail;
        ImageButton action;


        public SongsListItemViewHolder(View itemView, final Context context) {
            super(itemView);
            add = (ImageButton) itemView.findViewById(R.id.all_music_item_add);
            songDetail = (TextView) itemView.findViewById(R.id.all_music_item_song_detail);
            action = (ImageButton) itemView.findViewById(R.id.all_music_item_action);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int id = view.getId();
                    if(id == R.id.all_music_item_add){
                        Toast.makeText(context,"add button is clicked,the song will add to playing queue.",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"add button is clicked,the song will add to playing queue.");
                    }else{
                        Toast.makeText(context,"play current song "+data.get(getLayoutPosition()).getTitle(),Toast.LENGTH_SHORT).show();
                        //play the song
                        service.play(data.get(getLayoutPosition()),getLayoutPosition());
                    }
                }
            });

        }
    }
    public void onDestroy(){
        if(service != null){
            context.unbindService(conn);
        }
    }
}

