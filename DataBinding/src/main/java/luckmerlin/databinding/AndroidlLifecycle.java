package luckmerlin.databinding;

import android.content.Context;

import luckmerlin.core.debug.Debug;

public class AndroidlLifecycle {

    public boolean attachBaseContext(Context context){
        Debug.D("AAAAAAAAAAAAA  "+context+" "+context);
        return false;
    }

}
