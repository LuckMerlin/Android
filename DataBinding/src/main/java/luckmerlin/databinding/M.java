package luckmerlin.databinding;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.widget.PopupWindow;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import luckmerlin.core.debug.Debug;

public final class M {

    private M(){
        //Do nothing
    }

    public static ViewDataBinding getBinding(View view) {
        return null==view?null: DataBindingUtil.getBinding(view);
    }

    public static <T extends ViewDataBinding> T inflate(LayoutInflater inflater, int layoutId,ViewGroup parent, boolean attachToParent){
        return inflate(inflater,layoutId,parent,attachToParent,null,null);
    }

    public static <T extends ViewDataBinding> T inflate(LayoutInflater inflater, int layoutId,ViewGroup parent,
                                                        boolean attachToParent,DataBindingComponent bindingComponent){
        return inflate(inflater,layoutId,parent,attachToParent,bindingComponent,null);
    }

    public static <T extends ViewDataBinding> T inflate(LayoutInflater inflater, int layoutId, ViewGroup parent,
                                                        boolean attachToParent, DataBindingComponent bindingComponent, Listener binder){
        T binding= DataBindingUtil.inflate(inflater, layoutId,parent,attachToParent,bindingComponent);
        return bind(binding,binder);
    }

    public static ViewDataBinding setContentView(Context context,PopupWindow window,int layoutId, ViewGroup.LayoutParams params) {
        return setContentView(context,window,layoutId,params,null,null);
    }

    public static ViewDataBinding setContentView(Context context, PopupWindow window, int layoutId,
                                                 ViewGroup.LayoutParams params, DataBindingComponent component, Listener binder){
        if (null==window){
            return null;
        }
        return setContentView(window,inflate(LayoutInflater.from(context),
                layoutId,null,false,component,binder),params,component,null);
    }

    public static ViewDataBinding setContentView(Context context,ViewManager manager,int layoutId, ViewGroup.LayoutParams params){
        return setContentView(context,manager,layoutId,params,null,null);
    }

    public static ViewDataBinding setContentView(Context context, ViewManager manager, int layoutId,
                                                 ViewGroup.LayoutParams params, DataBindingComponent component, Listener binder){
        if (null==manager||null==context){
            return null;
        }
        return setContentView(manager,inflate(LayoutInflater.from(context),
                layoutId,null,false,component,binder),params,component,null);
    }

    public static ViewDataBinding setContentView(Object target, Object root) {
        return setContentView(target,root,null,null,null);
    }

    public static ViewDataBinding setContentView(Object target, Object root, Listener binder) {
        return setContentView(target, root,null,null,binder);
    }

    public static ViewDataBinding setContentView(Object target, Object root, DataBindingComponent bindingComponent) {
        return setContentView(target, root, null,bindingComponent,null);
    }

    public static ViewDataBinding setContentView(Object target, Object root, ViewGroup.LayoutParams params,
                                                 DataBindingComponent bindingComponent, Listener binder) {
        if (null==target||null==root){
            return null;
        }else if (root instanceof Integer){//Layout id
            if (target instanceof Activity){
                return bind(DataBindingUtil.setContentView((Activity)target,(Integer)root,bindingComponent),binder);
            }else if (target instanceof Dialog){
                Dialog dialog=(Dialog)target;
                ViewDataBinding binding=inflate(dialog.getLayoutInflater(),(Integer)root, null,false,bindingComponent,binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    dialog.setContentView(rootView,params);
                }
                return binding;
            }else if (target instanceof Window){
                Window window=(Window)target;
                ViewDataBinding binding=inflate(window.getLayoutInflater(), (Integer)root,null,false,bindingComponent,binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    window.setContentView(rootView,params);
                }
                return binding;
            }else if (target instanceof ViewGroup){
                ViewGroup viewGroup=(ViewGroup)target;
                Context context=viewGroup.getContext();
                ViewDataBinding binding=null==context?null:inflate(LayoutInflater.from(context),
                        (Integer)root,null,false,bindingComponent,binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    viewGroup.addView(rootView,params);
                }
                return binding;
            }
            return null;//Not support
        }else if (root instanceof ViewDataBinding){//DataBinding
            return setContentView(target,((ViewDataBinding)root).getRoot(),params,bindingComponent,binder);
        }else if (root instanceof View){
            if (target instanceof Activity){
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                if (null!=binding){
                    ((Activity)target).setContentView((View)root,params);
                }
                return binding;
            }else if (target instanceof Dialog){
                Dialog dialog=(Dialog)target;
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    dialog.setContentView(rootView,params);
                }
                return binding;
            }else if (target instanceof Window){
                Window window=(Window)target;
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    window.setContentView(rootView,params);
                }
                return binding;
            }else if (target instanceof ViewGroup){
                ViewGroup viewGroup=(ViewGroup)target;
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    viewGroup.addView(rootView,params);
                }
                return binding;
            }else if (target instanceof ViewManager){
                ViewManager manager=(ViewManager)target;
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    manager.addView(rootView,params);
                }
                return binding;
            }else if (target instanceof PopupWindow){
                PopupWindow popupWindow=(PopupWindow)target;
                ViewDataBinding binding=bind(getBinding(((View)root)),binder);
                View rootView=null!=binding?binding.getRoot():null;
                if (null!=rootView){
                    Debug.D("SSSSSSSSSSSSs  "+rootView);
                    popupWindow.setContentView(rootView);
                }
                return binding;
            }
            return setContentView(target,getBinding((View)root),params,bindingComponent,binder);
        }
        return null;
    }

    private static <T extends ViewDataBinding> T bind(T binding, final Listener binder){
        final View root=null!=binding?binding.getRoot():null;
        if (null==root){
            return null;
        }
        if (null!=binder&&binding(binder, binding)){
            return binding;
        }else if (binding(root,binding)){
            return binding;
        }
        final Context context=root.getContext();
        if (null==context||binding(context, binding)){
            return binding;
        }else if (!(context instanceof Activity)&&context instanceof ContextWrapper &&
                binding(((ContextWrapper)context).getBaseContext(), binding)){
            return binding;
        }else if (binding(context.getApplicationContext(), binding)){
            return binding;
        }
        final Activity activity=null!=context&&context instanceof Activity?((Activity)context):null;
        Context applicationContext=null!=context?context.getApplicationContext():null;
        final Application application=null!=applicationContext&&applicationContext instanceof Application? (Application)applicationContext:null;
        final OnModelCreate modelCreator=new OnModelCreate() {
            @Override
            public Object onCreate(View root1,Class<? extends Model> type) {
                if (null==type){
                    return null;
                }
                Object object=null!=binder&&binder instanceof OnModelCreate?((OnModelCreate)binder).onCreate(root1,type):null;
                if (null!=object&&type.isAssignableFrom(object.getClass())){
                    return object;
                }
                //Create inner
                Constructor[] constructors=type.getDeclaredConstructors();
                if (null==constructors||constructors.length<=0){
                    try {
                        return type.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Class[] types=null;Object modelObject=null;
                    for (Constructor construct:constructors) {
                        try {
                            if (null==construct){
                                continue;
                            }else if ((null==(types=construct.getParameterTypes())||types.length<=0)&&
                                    null!=(modelObject=construct.newInstance())){
                                return modelObject;
                            }else if (types.length==1){
                                Class singleType=types[0];//Single param
                                if (null==singleType){
                                    continue;
                                }else if (null!=activity&&Activity.class.isAssignableFrom(singleType)&&
                                        null!=(modelObject=construct.newInstance(activity))){
                                    return modelObject;
                                }else if (null!=application&&Application.class.isAssignableFrom(singleType)&&
                                        null!=(modelObject=construct.newInstance(application))){
                                    return modelObject;
                                }else if (Context.class.isAssignableFrom(singleType)&&
                                        null!=(modelObject=construct.newInstance(context))){
                                    return modelObject;
                                }
                            }
                        }catch (Exception e){
                            //Do nothing
                        }
                    }
                }
                return null;
            }
        };
        //Auto create model
        Class bindingClass=binding.getClass();
        Method[] methods=null!=bindingClass?bindingClass.getDeclaredMethods():null;
        if (null!=methods&&methods.length>0) {
            Class type = null;Class[] types;Object modelObject=null;
            for (Method child : methods) {
                if (null == (type = (null != child ? child.getReturnType() : null)) ||!type.equals(void.class)) {
                    continue;
                }
                type = null != (types = child.getParameterTypes()) && types.length == 1 ? types[0] : null;
                if (null == type ||!Model.class.isAssignableFrom(type)) {
                    continue;
                }
                //Create and set model object
                if (null!=(modelObject=modelCreator.onCreate(root,type))&&modelObject instanceof Model&&
                        type.isAssignableFrom(modelObject.getClass())){
                    try {
                        child.invoke(binding,modelObject);
                        ((Model)modelObject).attachRoot(root);
                    } catch (Exception e) {
                        Debug.E("Exception attach root.e="+e,e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return binding;
    }

    private static  <T extends ViewDataBinding> boolean binding(Object object,T binding){
        return null!=object&&object instanceof OnModelBind &&((OnModelBind)object).onModelBind(binding);
    }
}
