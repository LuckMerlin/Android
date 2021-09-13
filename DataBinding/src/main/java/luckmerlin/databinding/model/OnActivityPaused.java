package luckmerlin.databinding.model;

import android.app.Activity;

public interface OnActivityPaused extends ActivityLifecycle{
    void onActivityPaused( Activity activity);
}
