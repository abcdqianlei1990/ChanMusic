package cn.chan.com.util;

import android.view.View;

/**
 * Created by Administrator on 2015/7/22.
 */
public class MeasureView {
    private  View view;

    public MeasureView(View view){
        this.view = view;
        startMeasure();
    }

    private void startMeasure(){
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    public int getMeasuredWidth(){
        return view.getMeasuredWidth();
    }
    public  int getMeasuredHeight(){
        return view.getMeasuredHeight();
    }

}
