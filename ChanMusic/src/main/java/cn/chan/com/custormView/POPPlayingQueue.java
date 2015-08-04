package cn.chan.com.custormView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chan.com.activity.BaseActivity;
import cn.chan.com.activity.BaseApplication;
import cn.chan.com.activity.LocalMusicActivity;
import cn.chan.com.activity.R;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.service.MediaPlayService;
import cn.chan.com.util.MeasureView;
import cn.chan.com.util.MyConstants;

/**
 * Created by Administrator on 2015/8/1.
 */
public class POPPlayingQueue extends PopupWindow implements PopupWindow.OnDismissListener{

    private static final String TAG = "POPPlayingQueue";
    private View        mContentView;
    private Context     mContext;
    private ImageView mPlayMode;
    private int         mMode;
    private ImageButton mDeleteAll;
    private ListView    mLv;
    private TextView    mCount;
    private ListViewAdapter mAdapter;
    private ArrayList<SongDetailEntity> queue = new ArrayList<SongDetailEntity>();
    private BaseApplication application;
    private SharedPreferences sharedPreferences;
    private int             pos;   //当前点击的item在list（非播放队列）中的坐标
    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     * <p/>
     * <p>The popup does provide a background.</p>
     *
     * @param context
     */
    public POPPlayingQueue(Context context) {
        super(context);
        mContext = context;
        initView();
        initParams();
        setContentView(mContentView);
        initPopWindowParams();
        initEvents();
        this.update();
    }

    private void initParams(){
        application = (BaseApplication) mContext.getApplicationContext();
        //queue.addAll(application.getPlayingQueue());
        queue = application.getPlayingQueue();  //这里只需要得到引用即可
        sharedPreferences = mContext.getSharedPreferences(MyConstants.SharedPreferencesData.FILE_NAME, Activity.MODE_PRIVATE);
        mAdapter = new ListViewAdapter();

        Log.d("chan","get playing queue from application,size is:"+queue.size());
    }
    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.pop_playing_queue, null);
        mCount = (TextView) mContentView.findViewById(R.id.queue_count);
        //mContentView.setAlpha(100);
        mPlayMode = (ImageView) mContentView.findViewById(R.id.queue_mode);
        mDeleteAll = (ImageButton) mContentView.findViewById(R.id.queue_delete_all);
        mLv = (ListView) mContentView.findViewById(R.id.queue_lv);


    }

    private void initPopWindowParams(){
        //设置window的高度，为默认高度的1/2,宽度为MATCH_PARENT
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int h = wm.getDefaultDisplay().getHeight();
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //this.setHeight(h/2);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.isOutsideTouchable();
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.popupwindowanim);
        this.setFocusable(true);
    }

    private void initEvents(){
        //点击播放
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("chan","播放队列item被点击");
                LocalMusicActivity activity = (LocalMusicActivity) mContext;
                MediaPlayService service = activity.getService();
                service.play(position);
            }
        });
        mPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("chan","*******mPlayMode CLICK*******");
                //order: default -> random -> loop
                switch (mMode){
                    case MyConstants.PlayMode.DEFAULT:
                        //.d("chan","********1******");
                        mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_random_default));
                        mMode = MyConstants.PlayMode.RANDOM;
                        break;
                    case MyConstants.PlayMode.RANDOM:
                        //Log.d("chan","********2******");
                        mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_single_default));
                        mMode = MyConstants.PlayMode.SIGNAL;
                        break;
                    case MyConstants.PlayMode.SIGNAL:
                        //Log.d("chan","********3******");
                        mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_all_default));
                        mMode = MyConstants.PlayMode.DEFAULT;
                        break;
                    default:
                        break;
                }
                saveMode();
            }
        });
        //清空播放列表
        mDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.clear();
                updateCount();
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     *
     * @param parent
     * @param view      参照物
     */
    public void showAtLocation(View parent,View view){
        //此处为了展示在view的上方，先对view进行测量
        MeasureView m = new MeasureView(view);
        this.showAtLocation(parent, Gravity.BOTTOM, 0, m.getMeasuredHeight());
        onShow();
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Called when this popup window is dismissed.
     */
    @Override
    public void onDismiss() {
        saveMode();
    }


    public void onShow() {
        mMode = sharedPreferences.getInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE, 0);
        mLv.setAdapter(mAdapter);
        //Log.d("chan","playing window is shown ,mode="+mMode);
        switch (mMode){
            case MyConstants.PlayMode.DEFAULT:
                //.d("chan","********1******");
                mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_all_default));
                break;
            case MyConstants.PlayMode.RANDOM:
                //Log.d("chan","********2******");
                mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_random_default));
                break;
            case MyConstants.PlayMode.SIGNAL:
                //Log.d("chan","********3******");
                mPlayMode.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_player_mode_single_default));
                break;
            default:
                break;
        }
    }

    class ListViewAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            int count = queue.size();
            mCount.setText("播放队列（"+count+"）");
            return count;
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return queue.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final PopPlayingQueueViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.queue_listview_item,null);
                holder = new PopPlayingQueueViewHolder();
                holder.itemID = (TextView) convertView.findViewById(R.id.queue_lv_item_id);
                holder.title = (TextView) convertView.findViewById(R.id.queue_lv_item_title);
                holder.subTitle = (TextView) convertView.findViewById(R.id.queue_lv_item_sub_title);
                holder.favor = (ImageButton) convertView.findViewById(R.id.queue_lv_item_favor);
                holder.delete = (ImageButton) convertView.findViewById(R.id.queue_lv_item_delete);
                convertView.setTag(holder);
            }else{
                holder = (PopPlayingQueueViewHolder) convertView.getTag();
            }
            holder.itemID.setText((position+1)+"");
            holder.title.setText(queue.get(position).getTitle());
            holder.subTitle.setText(queue.get(position).getArtist());
            if(queue.get(position).getFavor() == 1){
                holder.favor.setImageResource(R.mipmap.player_queue_like);
            }else{
                holder.favor.setImageResource(R.mipmap.player_queue_unlike);
            }
            holder.favor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //先判断该歌的喜好，如果已被添加，那么就改为删除，反之，添加为喜好
                    //先拿到点击的item数据，然后去

                    if(queue.get(position).getFavor() == 1){
                        holder.favor.setImageResource(R.mipmap.player_queue_unlike);
                        queue.get(position).setFavor(0);
                    }else{
                        holder.favor.setImageResource(R.mipmap.player_queue_like);
                        queue.get(position).setFavor(1);
                        Log.d("chan","**********");
                    }
                    //queue和application中的playing queue是指向同一块内存
                    notifyDataSetChanged();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queue.remove(position);
                    notifyDataSetChanged();
                    updateCount();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("chan","播放队列item被点击");
                    BaseActivity activity = (BaseActivity)mContext;
                    MediaPlayService service = activity.getService();
                    service.play(position);
                }
            });
            return convertView;

        }
    }
    public class PopPlayingQueueViewHolder {
        private TextView        itemID;
        private TextView        title;
        private TextView        subTitle;
        private ImageButton     favor;
        private ImageButton     delete;
    }
    //保存播放模式
    public void saveMode(){
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(MyConstants.SharedPreferencesData.FIELD_NAME_PLAY_MODE, mMode);
        ed.commit();
    }

    //当执行delete操作后更新
    public void updateCount(){
        mCount.setText("播放队列（"+queue.size()+"）");
    }

}
