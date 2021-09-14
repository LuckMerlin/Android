package luckmerlin.databinding.model;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Toast;

import androidx.databinding.ViewDataBinding;
import java.lang.ref.WeakReference;
import luckmerlin.core.debug.Debug;

/**
 * Create LuckMerlin
 * Date 20:11 2021/1/20
 * TODO
 */
public abstract class Model {
    private Handler mHandler;
    private WeakReference<View> mRoot;
    private static final int TAG_ID=(2<<24);

    protected void onRootAttached(View view){
        //Do nothing
    }

    public final boolean isMainThread(){
        Looper looper=Looper.myLooper();
        if (null==looper||looper!=Looper.getMainLooper()){
            return false;
        }
        return true;
    }

    protected final boolean isCurrentActivity(Activity activity){
        Activity current=null!=activity?getActivity():null;
        return null!=current&&current==activity;
    }

    public final boolean isAttachedToWindow(View view){
        return null!=view&&(Build.VERSION.SDK_INT >= 19?view.isAttachedToWindow():null!=view.getWindowToken());
    }

    public static <T extends Model> T findModel(View view,boolean iterate,Class<T> cls){
        if (null!=view){
            Object tagObject=view.getTag(TAG_ID);
            if (null!=tagObject&&tagObject instanceof Model&&(null==cls||cls.isAssignableFrom(tagObject.getClass()))){
                return (T)tagObject;
            }else if (iterate){
                ViewParent parent=view.getParent();
                return null!=parent&&parent instanceof View?findModel((View)parent,true,cls):null;
            }
        }
        return null;
    }

    protected final <T extends Activity> boolean startActivity(Class<T> cls){
        return startActivity(cls,null);
    }

    protected final <T extends Activity> boolean startActivity(Class<T> cls,Integer requestCode){
        Context context=null!=cls?getContext():null;
        return null!=context&&startActivity(new Intent(context,cls),requestCode);
    }

    protected final boolean startActivity(Intent intent,Integer requestCode){
        Context context=null!=intent?getContext():null;
        if (null==context){
            return false;
        }
        try {
            if (null!=requestCode&&context instanceof Activity){
                ((Activity)context).startActivityForResult(intent,requestCode);
            }else{
                context.startActivity(intent);
            }
            return true;
        }catch (Exception e){
            Debug.E("Exception start activity.e="+e,e);
            e.printStackTrace();
        }
        return false;
    }

    protected final boolean bindService(Class<?extends Service> service, ServiceConnection conn, int flags){
        Context context=null!=service?getContext():null;
        return null!=context&&bindService(new Intent(context,service),conn,flags);
    }

    protected final boolean bindService(Intent service,ServiceConnection conn,int flags){
        Context context=null!=service&&null!=conn?getContext():null;
        return null!=context&&context.bindService(service,conn,flags);
    }

    protected final boolean unbindService(ServiceConnection conn){
        Context context=null!=conn?getContext():null;
        if (null!=context){
            context.unbindService(conn);
            return true;
        }
        return false;
    }

    protected final Application getApplication(){
        return getApplication(null);
    }

    protected final Application getApplication(Context context){
        context=null!=context?context:getContext();
        context=null!=context&&!(context instanceof Application)?context.getApplicationContext():context;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    protected final boolean registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callbacks){
        return registerActivityLifecycleCallbacks(null,callbacks);
    }

    protected final boolean registerActivityLifecycleCallbacks(Context context,
                    Application.ActivityLifecycleCallbacks callbacks){
        Application application=null!=callbacks?getApplication(context):null;
        if (null!=application){
            application.unregisterActivityLifecycleCallbacks(callbacks);
            application.registerActivityLifecycleCallbacks(callbacks);
            return true;
        }
        return false;
    }

    protected final boolean unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callbacks){
        return unregisterActivityLifecycleCallbacks(null,callbacks);
    }

    protected final boolean unregisterActivityLifecycleCallbacks(Context context,
                   Application.ActivityLifecycleCallbacks callbacks){
        Application application=null!=callbacks?getApplication(context):null;
        if (null!=application){
            application.unregisterActivityLifecycleCallbacks(callbacks);
            return true;
        }
        return false;
    }


    final boolean attachRoot(ViewDataBinding binding){
        return null!=binding&&attachRoot(binding.getRoot());
    }

    final boolean attachRoot(View root){
        if (null!=root&&null==mRoot){
            final View current=root;
            final ModeLifecycleListener listener=new ModeLifecycleListener(this){
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (null!=v&&v==current){
                        Debug.TD(null,"Attached model view."+Model.this);
                        v.setTag(TAG_ID,Model.this);
                        mRoot=new WeakReference<View>(v);
                        onRootAttached(v);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (null!=v&&v==current){
                        v.removeOnAttachStateChangeListener(this);
                        v.setTag(TAG_ID,null);
                        Debug.TD(null,"Detached model view."+Model.this);
                        detachedRoot(v);
                        if (Model.this instanceof ActivityLifecycle){
                            unregisterActivityLifecycleCallbacks(v.getContext(),this);
                        }
                        super.setModel(null);
                    }
                }
            };
            if (this instanceof ActivityLifecycle){
                registerActivityLifecycleCallbacks(current.getContext(),listener);
            }
            if (null==current.getRootView()){
                root.addOnAttachStateChangeListener(listener);
                return true;
            }
            listener.onViewAttachedToWindow(current);
            return true;
        }
        return false;
    }

    protected void onRootDetached(View view){
        //Do nothing
    }

    protected final Context getApplicationContext(){
        Context context=getContext();
        return null!=context?context.getApplicationContext():null;
    }

    private final boolean detachedRoot(View v){
        WeakReference<View> reference=mRoot;
        View view=null!=reference?reference.get():null;
        if (null!=v&&null!=view&&v==view){
            mRoot=null;
            if (null!=view){
                Debug.TD(null,"On detached root."+this);
                onRootDetached(v);
                reference.clear();
            }
            return null!=view;
        }
        return false;
    }

    public final boolean detach(){
        View root=getRootView();
        ViewParent parent=null!=root?root.getParent():null;
        if (null!=parent&&parent instanceof ViewGroup){
            ((ViewGroup)parent).removeView(root);
            Debug.D("Detach model root.");
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
        if (null!=text&&null!=(context=null!=context?context:getContext())){
            Toast toast=Toast.makeText(context,text,Toast.LENGTH_SHORT);
            toast.setDuration(duration);
            toast.show();
            return true;
        }
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
        return post(runnable, -1);
    }

    protected final boolean post(Runnable runnable,int delay){
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
