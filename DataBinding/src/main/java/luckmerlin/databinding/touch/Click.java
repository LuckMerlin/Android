package luckmerlin.databinding.touch;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import luckmerlin.core.match.Matchable;
import luckmerlin.databinding.BS;
import luckmerlin.databinding.Binding;
import luckmerlin.databinding.model.Model;

/**
 * Create LuckMerlin
 * Date 18:50 2021/2/1
 * TODO
 */
public final class Click extends BS implements Binding {
    public final static int NONE=0;
    public final static int CLICK=1;//01
    public final static int LONG_CLICK=2;//10
    public final static int TOUCH=4;//100
    private boolean mClickAnimEnable=true;
    private int mClick;
    private Integer mId;
    private ClickListener mListener;
    private Object mTag;
    private int mDither=0;
    private View.OnTouchListener mOnTouchListener;

    private Click(int click) {
        mClick=click;
    }

    public Click click(ClickListener listener){
        mListener=listener;
        return this;
    }

    public Click tag(Object tag){
        mTag=tag;
        return this;
    }

    public Click id(Object id){
        mId=null!=id?id instanceof Integer?(Integer)id:mId:null;
        return this;
    }

    public Click dither(){
        return dither(true);
    }

    public Click dither(boolean enable){
        return dither(enable?500:0);
    }

    public Click dither(int dither){
        mDither=dither;
        return this;
    }

    public Click animation(boolean clickAnim){
        mClickAnimEnable=clickAnim;
        return this;
    }

    public Click longClick(){
        return longClick(true);
    }

    public Click longClick(boolean enable){
        mClick=(enable?mClick|LONG_CLICK:mClick&(~LONG_CLICK));
        return this;
    }

    public Click touch(View.OnTouchListener touchListener){
        mOnTouchListener=touchListener;
        return this;
    }

    public Click touch(){
        return touch(true);
    }

    public Click touch(boolean enable){
        mClick=(enable?mClick|TOUCH:mClick&(~TOUCH));
        return this;
    }

    public static Click c(Object id){
        return new Click(Click.CLICK).id(id);
    }

    @Override
    public final void onBind(final View view) {
        super.onBind(view);
        if (null==view){
            return;
        }
        view.setOnTouchListener(mOnTouchListener);
        final int clickValue=mClick;
        final Object tag=mTag;
        final ClickListener listener=mListener;
        final Integer id=mId;
        final boolean clickAnimEnable=mClickAnimEnable;
        //Click
        view.setOnClickListener((clickValue&CLICK)>0?new ClickRunnable() {
            @Override
            public void run() {
                iterateInvoke(new Matchable() {
                    @Override
                    public Integer onMatch(Object arg) {
                        return null!=arg&&arg instanceof OnViewClick&&
                                ((OnViewClick)arg).onClicked(view,null!=id?id:view.getId(), mCount,tag)?
                                Matchable.MATCHED:Matchable.CONTINUE;
                    }
                },listener,view);
                mCount=0;//Reset
            }

            @Override
            public void onClick(View v) {
                if (null==v){
                    return;
                }else if (null!=v&&clickAnimEnable){
//                  new ViewClickAnimator().startClickAnim(v);
                }
                v.removeCallbacks(this);
                int dither=mDither;
                if (dither>0&&dither<10000){//0~10s
                    ++mCount;
                    v.postDelayed(this,dither);
                }else{
                    mCount=1;
                    run();
                }
            }
        }:null);
        //Long click
        view.setOnLongClickListener((clickValue&LONG_CLICK)>0?new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return iterateInvoke(new Matchable() {
                    @Override
                    public Integer onMatch(Object arg) {
                        return null!=arg&&arg instanceof OnViewLongClick&&
                                ((OnViewLongClick)arg).onLongClicked(null!=id?id:view.getId(), view,tag)?
                                Matchable.MATCHED:Matchable.CONTINUE;
                    }
                },listener,view);
            }
        }:null);
        //Touch
        view.setOnTouchListener(((clickValue&TOUCH)>0||null!=mOnTouchListener)?new TouchMatcher() {
            @Override
            public Integer onMatch(Object arg) {
                return null!=arg&&arg instanceof OnViewTouch&&
                        ((OnViewTouch)arg).onTouched(null!=id?id:view.getId(),tag,view,mEvent)?
                        Matchable.MATCHED:Matchable.CONTINUE;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEvent=event;
                return iterateInvoke(this,mListener,mOnTouchListener,v);
            }
        }:null);
    }

    private <T extends Model> T findModel(View view,boolean iterate,Class<T> cls){
        return Model.findModel(view,iterate,cls);
    }

    private boolean iterateInvoke(Matchable matchable,Object ...objects){
        if (null!=objects&&objects.length>0&&null!=matchable){
            for (Object child:objects) {
                if (null!=child&&iterateInvoke(child,matchable)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean iterateInvoke(Object view,Matchable matchable){
        if (null==view||null==matchable){
            return false;
        }else if (isMatched(view,matchable)){
            return true;
        }else if (!(view instanceof View)){
            return false;
        }else if (isMatched(findModel((View) view,false,null),matchable)) {
            return true;
        }else if (((View)view).getId()==android.R.id.content){
            return false;
        }
        ViewParent parent=((View)view).getParent();
        if (null!=parent&&parent instanceof View&&parent!=view){
            return iterateInvoke((View) parent,matchable);
        }
        Context context=((View)view).getContext();
        if (null==context){
            return false;
        }else if (context instanceof Activity){
            Window window=((Activity)context).getWindow();
            View decorView=null!=window?window.getDecorView():null;
            return null!=decorView&&iterateViewChildren(decorView,matchable);
        }else if (isMatched(context,matchable)){
            return true;
        }else if (context instanceof ContextWrapper&&
                isMatched(((ContextWrapper)context).getBaseContext(),matchable)){
            return true;
        }else if(isMatched(context.getApplicationContext(),matchable)){
            return true;
        }
        return false;
    }

    private boolean iterateViewChildren(View view,Matchable matchable){
        if (null!=view&&null!=matchable){
            Model model=findModel(view,false,null);
            if (null!=model&&isMatched(model,matchable)){
                return true;
            }else if (isMatched(view,matchable)){
                return true;
            }else if (view instanceof ViewGroup){
                ViewGroup vg=(ViewGroup)view;
                int count=null!=vg?vg.getChildCount():0;
                for (int i = 0; i < count; i++) {
                    if (iterateViewChildren(vg.getChildAt(i),matchable)){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private boolean isMatched(Object view,Matchable matchable){
        if (null==view||null==matchable){
            return false;
        }
        Integer matched=matchable.onMatch(view);
        return null==matched||matched==Matchable.MATCHED;
    }

    private static abstract class ClickRunnable implements View.OnClickListener,Runnable{
        int mCount=0;
    }

    private static abstract class TouchMatcher implements View.OnTouchListener,Matchable{
        MotionEvent mEvent;
    }
}
