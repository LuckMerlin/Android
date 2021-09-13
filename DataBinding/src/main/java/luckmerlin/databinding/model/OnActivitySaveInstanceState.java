package luckmerlin.databinding.model;

import android.app.Activity;
import android.os.Bundle;

public interface OnActivitySaveInstanceState extends ActivityLifecycle{
    void onActivitySaveInstanceState(Activity activity,Bundle outState);
}
