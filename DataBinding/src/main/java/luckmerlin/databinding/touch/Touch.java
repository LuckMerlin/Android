package luckmerlin.databinding.touch;

import android.app.Application;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import luckmerlin.core.debug.Debug;
import luckmerlin.core.match.Matchable;
import luckmerlin.databinding.Binding;
import luckmerlin.databinding.Model;

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

    private void iterateDispatchObjects(Object object, Matchable matchable){
        if (null==object||null==matchable){
            return;
        }
        Integer matched=matchable.onMatch(object);
        if (null!=matched&&matched!=Matchable.BREAK){
            return;
        }else if (object instanceof View){
            View view=((View)object);
            ViewDataBinding binding=DataBindingUtil.getBinding(view);
            ViewParent parent=view.getParent();
            if (null!=parent&&parent!=view&&parent instanceof View){
                iterateDispatchObjects(parent,matchable);
                return;
            }
            iterateDispatchObjects(view.getContext(),matchable);
        }else if (object instanceof Context&&!(object instanceof Application)){
            Context application=((Context)object).getApplicationContext();
            if (null!=application&&application instanceof Application){
                iterateDispatchObjects(application,matchable);
            }
        }
    }

    @Override
    public void onBind(final View view) {
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
            final WeakHashMap<View, SparseArray<Set<Object>>> dispatches=new WeakHashMap<>(1);
            iterateDispatchObjects(view, new Matchable() {
                @Override
                public Integer onMatch(Object arg) {
                    Debug.D("AAAAAAAAAAAA  "+arg);
                    return null;
                }
            });
            final Runnable clickRunnable=clickEnable?new Runnable() {
                @Override
                public void run() {
                    final int count=clickCount[0];
                    clickCount[0]=0;//Reset
                    if (null!=view){
                        final int viewId=null!=id?id:view.getId();
                        if (null!=listener&&listener instanceof OnViewClick &&((OnViewClick)listener).
                                onClicked(viewId,count, view,tag)){
                            return;
                        }
                        SparseArray<Set<Object>> array=null!=dispatches?dispatches.get(view):null;
                        Set<Object> set=null!=array?array.get(LONG_CLICK):null;
                        if (null!=set) {
                            for (Object child : set) {

                            }
                        }
                    }
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
                    SparseArray<Set<Object>> array=null!=dispatches&&null!=v?dispatches.get(v):null;
                    Set<Object> set=null!=array?array.get(LONG_CLICK):null;
                    return false;
                }
            }:null);
        }
    }
}
