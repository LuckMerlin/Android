package luckmerlin.databinding.model;

import android.app.Activity;
import android.os.Bundle;

public interface OnActivityCreated extends ActivityLifecycle{
    void onActivityCreated(Activity activity, Bundle savedInstanceState);
}
