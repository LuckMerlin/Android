package luckmerlin.databinding;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import luckmerlin.core.debug.Debug;
import luckmerlin.databinding.Binding;

/**
 * Create LuckMerlin
 * Date 20:11 2021/1/20
 * TODO
 */
public abstract class Model {
    private Handler mHandler;
    private WeakReference<View> mRoot;

    protected void onRootAttached(String debug){
        //Do nothing
    }

    public final boolean isMainThread(){
        Looper looper=Looper.myLooper();
        if (null==looper||looper!=Looper.getMainLooper()){
            return false;
        }
        return true;
    }

    public final boolean isAttachedToWindow(View view){
        return null!=view&&(Build.VERSION.SDK_INT >= 19?view.isAttachedToWindow():null!=view.getWindowToken());
    }

    public static Binding model(){
        return new Binding() {
            @Override
            public void onBind(View view) {
                new ModelBinder().bind(view);
            }
        };
    }

    final boolean attachRoot(View root, String debug){
        if (null!=root&&null==mRoot){
            mRoot=new WeakReference<View>(root);
            Debug.TD(null,"On attach root."+this);
            onRootAttached(debug);
            return true;
        }
        return false;
    }

    protected void onRootDetached(String debug){
        //Do nothing
    }

    protected final Context getApplicationContext(){
        Context context=getContext();
        return null!=context?context.getApplicationContext():null;
    }

    final boolean detachedRoot(String debug){
        WeakReference<View> reference=mRoot;
        View view=null!=reference?reference.get():null;
        mRoot=null;
        if (null!=view){
            Debug.TD(null,"On detached root."+this);
            onRootDetached(debug);
            reference.clear();
        }
        return null!=view;
    }

    protected final boolean detach(String debug){
        View root=getRootView();
        ViewParent parent=null!=root?root.getParent():null;
        if (null!=parent&&parent instanceof ViewGroup){
            ((ViewGroup)parent).removeView(root);
            Debug.D("Detach model root "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    protected final int getColor(int id){
        return getColor(id, Color.TRANSPARENT);
    }

    protected final int getColor(int id,int def){
        Resources resources=getResources();
        return null!=resources?resources.getColor(id):def;
    }

    protected final Resources getResources(){
        Context context=getContext();
        return null!=context?context.getResources():null;
    }

    protected final String getText(int textId,Object ...args){
        return getText(getContext(), textId, args);
    }

    protected final String getText(Context context, int textId, Object ...args){
        try {
            context=null!=context?context:getContext();
            return null!=context?context.getString(textId, args):null;
        }catch (Exception e){
            Debug.E("Exception get text.e="+e,e);
            e.printStackTrace();
            //Do nothing
        }
        return null;
    }

    protected final SharedPreferences getSharedPreferences(String name, int mode){
        Context context=getApplicationContext();
        return null!=context&&null!=name?context.getSharedPreferences(name,mode):null;
    }

    protected final Drawable getDrawable(int drawableId){
        return getDrawable(getContext(), drawableId, null);
    }

    protected final Drawable getDrawable(Context context, int drawableId, Drawable def){
        try {
            context=null!=context?context:getContext();
            Resources resources=null!=context?context.getResources():null;
            return null!=resources?resources.getDrawable(drawableId):null;
        }catch (Exception e){
            Debug.E("Exception get drawable.e="+e,e);
            e.printStackTrace();
            //Do nothing
        }
        return null;
    }

    protected final boolean toast(int textId,Object ...args){
        return toast(getContext(),textId,args);
    }

    protected final boolean toast(Context context, int textId,Object ...args){
        context=null!=context?context:getContext();
        return toast(context,getText(context,textId, args),-1);
    }

    protected final boolean toast(final CharSequence text){
        return toast(getContext(), text,-1);
    }

    protected final boolean toast(Context context,final CharSequence text){
        return toast(context,text,-1);
    }

    protected final boolean toast(Context context,final CharSequence text,int duration){
        context=null!=context?context:getContext();
//        Api api=getApi();
//        UiManager manager=null!=api?api.getUiManager():null;
//        return null!=manager&&manager.toast(context,new Toast().setMessage(null!=text?text.toString():null).setDuration(duration))==Code.CODE_SUCCEED;
        return false;
    }

    protected final View findViewById(int viewId){
        View root=getRootView();
        return null!=root?root.findViewById(viewId):null;
    }

    protected final CharSequence getViewText(int viewId){
        return getViewText(getRootView(), viewId);
    }

    protected final CharSequence getViewText(View root,int viewId){
        View view=null!=root?root.findViewById(viewId):null;
        return null!=view&&view instanceof TextView ?((TextView)view).getText():null;
    }

    public final boolean isRootAttached(){
        return null!=getRootView();
    }

    public final View getRootView(){
        WeakReference<View> root=mRoot;
        return null!=root?root.get():null;
    }

    protected final Window getWindow(){
        View view=getRootView();
        Context context=null!=view?view.getContext():null;
        if (null==context){
            return null;
        }else if (context instanceof Activity){
            return ((Activity)context).getWindow();
        }
        return null;
    }

    protected final Activity getActivity(){
        return getActivity(false);
    }

    protected final Activity getActivity(boolean top){
        return getActivity(getContext(),top);
    }

    protected final Activity getActivity(Context context,boolean top){
        if (null==context){
            return null;
        }else if (context instanceof Activity){
            return (Activity)context;
        }else if (top&&context instanceof ContextThemeWrapper){
            return getActivity(((ContextThemeWrapper)context).getBaseContext(),false);
        }
        return null;
    }

    protected final Context getContext(){
        View root=getRootView();
        Context context= null!=root?root.getContext():null;
        if (null!=context&&context instanceof ContextThemeWrapper){
            Context baseContext=((ContextThemeWrapper)context).getBaseContext();
            context=null!=baseContext&&baseContext instanceof Activity?baseContext:context;
        }
        return context;
    }

    protected final View findFocus(){
        View root=getRootView();
        root=null!=root?root.getRootView():null;
        return null!=root?root.findFocus():null;
    }

    protected final boolean post(Runnable runnable){
        return post(runnable,null);
    }

    protected final boolean post(Runnable runnable,String debug){
        return post(runnable, -1,debug);
    }

    protected final boolean post(Runnable runnable,int delay,String debug){
        if (null!=runnable){
            delay=delay<=0?0:delay;
            Handler handler=mHandler;
            handler=null!=handler?handler:(mHandler=new Handler(Looper.getMainLooper()));
            handler.postDelayed(runnable,delay);
            return true;
        }
        return false;
    }

    protected final boolean remove(Runnable runnable,String debug){
        Handler handler=null!=runnable?mHandler:null;
        if (null!=handler){
            handler.removeCallbacks(runnable);
            return true;
        }
        return false;
    }

}
