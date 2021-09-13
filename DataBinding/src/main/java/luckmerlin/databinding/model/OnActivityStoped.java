package luckmerlin.databinding.model;

import android.app.Activity;

public interface OnActivityStoped extends ActivityLifecycle{
    void onActivityStopped( Activity activity);
}
