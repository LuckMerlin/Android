package merlin.file.model;

import android.os.Build;
import android.view.View;
import luckmerlin.core.debug.Debug;
import luckmerlin.databinding.model.Model;

public class BaseModel extends Model  {

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        Debug.D("onRootAttached "+this);
        enableNavigation(true);
    }

    @Override
    protected void onRootDetached(View view) {
        super.onRootDetached(view);
        Debug.D("onRootDetached "+this);
    }

    protected final boolean enableNavigation(boolean enable){
        View root=getRootView();
        if (null!=root){
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                root.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
                root.setSystemUiVisibility(root.getSystemUiVisibility()|uiOptions);
            }
            return true;
        }
        return false;
    }
}
