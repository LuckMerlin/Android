package luckmerlin.databinding;

import android.app.Application;
import android.content.Context;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import luckmerlin.core.debug.Debug;

final class ModelBinder {

    public void bind(final View view) {
        ViewDataBinding binding= null!=view?DataBindingUtil.getBinding(view):null;
        if (null==binding){
            return;
        }
        Class dataBindingClass=binding.getClass().getSuperclass();
        Method[] methods=null!=dataBindingClass?dataBindingClass.getDeclaredMethods():null;
        if (null!=methods&&methods.length>0) {
            Class type = null;
            final Context context=view.getContext();
            for (Method child : methods) {
                if (null != (type = (null != child ? child.getReturnType() : null)) && type.equals(void.class)) {
                    Class[] types = child.getParameterTypes();
                    type = null != types && types.length == 1 ? types[0] : null;
                    if (null != type && Model.class.isAssignableFrom(type)) {
                         Model model=createModel(view,view,type);
                         if (null==model){//Try to auto create model
                             final Constructor<Model>[] constructors=(Constructor<Model>[]) type.getDeclaredConstructors();
                             Model instance=null;
                             if (null!=constructors&&constructors.length>0){
                                 for (Constructor<Model> constructor:constructors) {
                                     if (null!=(instance=create(constructor,context))){
                                         //Do nothing
                                     }else if (null!=(instance=create(constructor))){
                                         //Do nothing
                                     }
                                 }
                             }
                             final Model modelInstance=instance;
                             if (null==modelInstance){
                                 continue;
                             }
                             view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                 @Override
                                 public void onViewAttachedToWindow(View v) {
                                 }

                                 @Override
                                 public void onViewDetachedFromWindow(View v) {
                                     if (null!=v&&v ==view){
                                         view.removeOnAttachStateChangeListener(this);
                                         modelInstance.detachedRoot("While view detached from window.");
                                     }
                                 }
                             });
                             modelInstance.attachRoot(view,"While model binder bind.");
                         }
                    }
                }
            }
        }
    }

    final <T> T create(Constructor<T> constructor,Object ...args){
        if (null==constructor){
            return null;
        }
        final Class[] types=constructor.getParameterTypes();
        int length=null!=types?types.length:0;
        if (length!=(null!=args?args.length:0)){
            return null;
        }else if (length>0){//Check params type
            Class childType=null;Object arg = null;
            for (int i = 0; i < length; i++) {
                if(null==(childType=types[i])||null==(arg=args[i]) ||(arg instanceof Class)||
                        !childType.isAssignableFrom(arg.getClass())){
                    return null;
                }
            }
        }
        boolean accessible=constructor.isAccessible();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            Debug.E("Exception create instance.e="+e,e);
            e.printStackTrace();
        }finally {
            constructor.setAccessible(accessible);
        }
        return null;
    }

    private <T extends Model> T createModel(Object obj,View view,Class<T> cls){
        if (null==obj||null==view){
            return null;
        }else if (obj instanceof ModelFactory){
            ((ModelFactory)view).create(view,cls);
        }else if (obj instanceof View){
            return createModel(((View)obj).getContext(),view,cls);
        }else if (obj instanceof Context&&!(obj instanceof Application)) {
            Context context = ((Context) obj).getApplicationContext();
            return null != context && context instanceof Application ? createModel(context, view, cls) : null;
        }
        return null;
    }
}
