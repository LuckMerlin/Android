package merlin.file.model;

import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.databinding.ViewDataBinding;
import com.file.manager.R;
import com.file.manager.databinding.AlertMessageBinding;

import luckmerlin.core.debug.Debug;
import luckmerlin.databinding.OnModelBind;
import luckmerlin.databinding.model.Model;
import luckmerlin.databinding.touch.OnViewClick;
import luckmerlin.databinding.window.PopupWindow;

public class BaseModel extends Model  {

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        enableNavigation(true);
        fullScreen(getWindow());
    }

    protected final boolean showAlert(AlertMessageModel model){
        return showAlert(null,model);
    }

    protected final boolean showAlert(PopupWindow popupWindow,AlertMessageModel model){
        return showAlert(null,popupWindow,model);
    }

    protected final boolean showAlert(View root,PopupWindow popupWindow,AlertMessageModel model){
        if (null==model){
            return false;
        }
        final PopupWindow finalPopupWindow=null!=popupWindow?popupWindow:new PopupWindow();
        return show(root, finalPopupWindow.setFocusable(true).setTouchable(true).setTouchModal(true).
                setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED).
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN),
                R.layout.alert_message, (ViewDataBinding binding) ->{
            if (null!=binding&&binding instanceof AlertMessageBinding){
                OnViewClick click=model.getOnViewClick();
                model.setOnViewClick((View view, int id, int count, Object tag)-> {
                    if (null!=click&&click.onClicked(view,id,count,tag)){
                        return true;
                    }
                    finalPopupWindow.dismiss();
                    return true;
                });
                ((AlertMessageBinding)binding).setVm(model);
                return model;
            }
                return null;
        });
    }

    protected final boolean show(View root,PopupWindow popupWindow,int layoutId,OnModelBind callback){
        if (null==popupWindow||null==(root=null!=root?root:getRootView())){
            Debug.W("Can't show model while arg invalid.");
            return false;
        }
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT).setHeight(WindowManager.
                LayoutParams.WRAP_CONTENT).setContentView(root.getContext(), layoutId, callback);
        return null!=popupWindow.showAtLocation(root, Gravity.CENTER,0,0)&&popupWindow.isShowing();
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

    private void fullScreen(Window window) {
        if (null==window){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

}
