package luckmerlin.databinding.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityLifeCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated( Activity activity,  Bundle savedInstanceState) {
//        LayoutInflater layoutInflater;
//        layoutInflater.setFactory(new LayoutInflater.Factory() {
//            @Nullable
//            @Override
//            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//                return null;
//            }
//        });
//        layoutInflater.setFactory2(new LayoutInflater.Factory2() {
//            @Nullable
//            @Override
//            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//                return null;
//            }
//
//            @Nullable
//            @Override
//            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//                return null;
//            }
//        });
    }

    @Override
    public void onActivityStarted( Activity activity) {

    }

    @Override
    public void onActivityResumed( Activity activity) {

    }

    @Override
    public void onActivityPaused( Activity activity) {

    }

    @Override
    public void onActivityStopped( Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState( Activity activity,  Bundle outState) {

    }

    @Override
    public void onActivityDestroyed( Activity activity) {

    }
}
