package luckmerlin.databinding.touch;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import luckmerlin.databinding.Binding;

public class Touch implements Binding {
    public final static int NONE=0;
    public final static int CLICK=1;
    public final static int LONG_CLICK=2;
    private Animation mClickAnim;
    private final int mClick;
    private Integer mId;
    private final ClickListener mListener;
    private Object mTag;
    private View.OnTouchListener mOnTouchListener;
    private Integer mDither;

    private Touch(int click, ClickListener listener) {
        mClick=click;
        mListener=listener;
    }

    public Touch dither(Object dither){
        dither=null!=dither?dither instanceof Boolean?((Boolean)dither)?460:null:dither:null;
        mDither=null!=dither&&dither instanceof Integer?(Integer)dither:null;
        return this;
    }

    public Touch tag(Object tag){
        mTag=tag;
        return this;
    }

    public Touch id(Object id){
        mId=null!=id?id instanceof Integer?(Integer)id:mId:null;
        return this;
    }

    public Touch animation(boolean clickAnim){
        mClickAnim=null;
        if (clickAnim){
            AlphaAnimation animation=new AlphaAnimation(0.5f,1f);
            animation.setDuration(500);
            animation.setInterpolator(new AccelerateInterpolator());
            mClickAnim=animation;
        }
        return this;
    }

    public Touch touch(View.OnTouchListener touchListener){
        mOnTouchListener=touchListener;
        return this;
    }

    @Override
    public boolean onBind(final View view) {
        if (null!=view) {
            view.setOnTouchListener(mOnTouchListener);
            final int clickValue=mClick;
            final Object tag=mTag;
            final ClickListener listener=mListener;
            final Integer id=mId;
            final Animation clickAnim=mClickAnim;
            final Integer dither=mDither;
            final Runnable[] ditherRunnable=new Runnable[1];
            final int[] clickCount=new int[1];
            final boolean clickEnable=(clickValue&CLICK)>0;
            final Runnable clickRunnable=clickEnable?new Runnable() {
                @Override
                public void run() {
                    final int count=clickCount[0];
                    clickCount[0]=0;//Reset
                    final int viewId=null!=id?id:null!=view?view.getId():0;
                    if (null!=listener&&listener instanceof OnViewClick &&((OnViewClick)listener).
                            onClicked(viewId,count, view,tag)){
                        return;
                    }
//                    new DataBinding().dispatch(view, new Dispatcher() {
//                        @Override
//                        public boolean dispatch(Object arg) {
//                            return null!=arg&&arg instanceof OnViewClick&&((OnViewClick)arg).
//                                    onClicked(viewId,count,  view,tag);
//                        }
//                    });
                }
            }:null;
            view.setOnClickListener(clickEnable?new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (null!=v&&null!=clickAnim){
                        v.startAnimation(clickAnim);
                    }
                    Runnable runnable=ditherRunnable[0];
                    if (null!=runnable){
                        v.removeCallbacks(runnable);
                    }
                    int count=clickCount[0];
                    clickCount[0]=count>=1?count+1:1;
                    if (null!=dither&&dither>0){
                        runnable=null!=runnable?runnable:(ditherRunnable[0]=new Runnable() {
                            @Override
                            public void run() {
                                v.removeCallbacks(this);
                                ditherRunnable[0]=null;
                                clickRunnable.run();
                            }
                        });
                        v.postDelayed(runnable,dither>=50000?50000:dither);
                        return;
                    }
                    clickRunnable.run();
                }
            }:null);
            boolean longClickEnable=(clickValue&LONG_CLICK)>0;
            view.setOnLongClickListener(longClickEnable?new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    final int viewId =null!=id?id: null != v ? v.getId() : 0;
                    if (null != listener && listener instanceof OnViewLongClick && ((OnViewLongClick) listener).
                            onLongClicked(viewId, v, tag)) {
                        return true;
                    }
                    return false;
                }
            }:null);
        }
        return false;
    }
}
