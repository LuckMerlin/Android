package luckmerlin.databinding.model;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import java.lang.ref.WeakReference;

class ModeLifecycleListener implements
        View.OnAttachStateChangeListener, Application.ActivityLifecycleCallbacks{
    private WeakReference<Model> mModel;

    ModeLifecycleListener(Model model){
       setModel(model);
    }

    public final void setModel(Model model){
       WeakReference<Model> reference=mModel;
      if (null!=reference){
         reference.clear();
      }
       mModel=null!=model?new WeakReference<Model>(model):null;
    }

    private Model getModel(){
       WeakReference<Model> model=mModel;
       return null!=model?model.get():null;
    }

    @Override
    public void onActivityCreated( Activity activity, Bundle savedInstanceState) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityCreated){
          ((OnActivityCreated)model).onActivityCreated(activity,savedInstanceState);
       }
    }

    @Override
    public void onActivityStarted( Activity activity) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityStarted){
          ((OnActivityStarted)model).onActivityStarted(activity);
       }
    }

    @Override
    public void onActivityResumed( Activity activity) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityResumed){
          ((OnActivityResumed)model).onActivityResumed(activity);
       }
    }

    @Override
    public void onActivityPaused( Activity activity) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityPaused){
          ((OnActivityPaused)model).onActivityPaused(activity);
       }
    }

    @Override
    public void onActivityStopped( Activity activity) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityStoped){
          ((OnActivityStoped)model).onActivityStopped(activity);
       }
    }

    @Override
    public void onActivitySaveInstanceState( Activity activity,  Bundle outState) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivitySaveInstanceState){
          ((OnActivitySaveInstanceState)model).onActivitySaveInstanceState(activity,outState);
       }
    }

    @Override
    public void onActivityDestroyed( Activity activity) {
       Model model=getModel();
       if (null!=model&&model instanceof OnActivityDestroyed){
          ((OnActivityDestroyed)model).onActivityDestroyed(activity);
       }
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }
}
