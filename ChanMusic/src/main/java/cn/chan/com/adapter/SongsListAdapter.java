package cn.chan.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chan.com.activity.R;
import cn.chan.com.entity.SongDetailEntity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class SongsListAdapter extends RecyclerView.Adapter<SongsListItemViewHolder>{
    private ArrayList<SongDetailEntity> data = new ArrayList<SongDetailEntity>();
    private Context context;
    public SongsListAdapter(ArrayList<SongDetailEntity> data,Context context){
        this.data = data;
        this.context = context;
    }
    @Override
    public SongsListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        SongsListItemViewHolder holder = new SongsListItemViewHolder(LayoutInflater.from(context).inflate(R.layout.all_music_item,viewGroup,false));
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
}
class SongsListItemViewHolder extends RecyclerView.ViewHolder{
    ImageButton add;
    TextView    songDetail;
    ImageButton action;
    public SongsListItemViewHolder(View itemView) {
        super(itemView);
        add = (ImageButton) itemView.findViewById(R.id.all_music_item_add);
        songDetail = (TextView) itemView.findViewById(R.id.all_music_item_song_detail);
        action = (ImageButton) itemView.findViewById(R.id.all_music_item_action);
    }
}
