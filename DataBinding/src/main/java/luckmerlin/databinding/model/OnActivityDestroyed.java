package luckmerlin.databinding.model;

import android.app.Activity;

public interface OnActivityDestroyed extends ActivityLifecycle{
    void onActivityDestroyed(Activity activity);
}
