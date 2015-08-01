package cn.chan.com.custormView;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import cn.chan.com.activity.R;
import cn.chan.com.entity.SongDetailEntity;
import cn.chan.com.util.MeasureView;

/**
 * Created by Administrator on 2015/8/1.
 */
public class POPPlayingQueue extends PopupWindow{

    private static final String TAG = "POPPlayingQueue";
    private View        mContentView;
    private Context     mContext;
    private ImageView   mMode;
    private ImageButton mDeleteAll;
    private ListView    mLv;
    private ArrayList<SongDetailEntity> mData = new ArrayList<SongDetailEntity>();
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
        setContentView(mContentView);
        initPopWindowParams();
        this.update();
    }

    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.pop_playing_queue, null);
        mMode = (ImageView) mContentView.findViewById(R.id.queue_mode);
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

    /**
     *
     * @param parent
     * @param view      参照物
     */
    public void showAtLocation(View parent,View view){
        //此处为了展示在view的上方，先对view进行测量
        MeasureView m = new MeasureView(view);
        this.showAtLocation(parent, Gravity.BOTTOM, 0, m.getMeasuredHeight());
    }

    class ListViewAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return 0;
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
            return null;
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return 0;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
