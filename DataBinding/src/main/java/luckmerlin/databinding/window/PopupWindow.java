package luckmerlin.databinding.window;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.ViewDataBinding;

import java.lang.reflect.Type;

import luckmerlin.core.Execute;
import luckmerlin.databinding.Listener;
import luckmerlin.databinding.model.M;

public class PopupWindow {
    private final android.widget.PopupWindow mPopupWindow;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = android.widget.PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
    public static final int INPUT_METHOD_NEEDED = android.widget.PopupWindow.INPUT_METHOD_NEEDED;
    public static final int INPUT_METHOD_NOT_NEEDED = android.widget.PopupWindow.INPUT_METHOD_NOT_NEEDED;

    public PopupWindow(){
        this(null);
    }

    public PopupWindow(android.widget.PopupWindow popupWindow){
        mPopupWindow=null!=popupWindow?popupWindow:new android.widget.PopupWindow();
    }

    public final PopupWindow setWidth(int width) {
        mPopupWindow.setWidth(width);
        return this;
    }

    public final PopupWindow setHeight(int height) {
        mPopupWindow.setHeight(height);
        return this;
    }

    public final PopupWindow setFocusable(boolean focusable) {
        mPopupWindow.setFocusable(focusable);
        return this;
    }

    public final PopupWindow setSoftInputMode(int mode) {
        mPopupWindow.setSoftInputMode(mode);
        return this;
    }

    public ViewDataBinding setContentView(Context context, int contentId){
        return setContentView(context,contentId,null);
    }

    public ViewDataBinding setContentView(Context context, int contentId,Listener listener){
        return M.setContentView(context, mPopupWindow,contentId,listener);
    }

    public PopupWindow setContentView(View contentView){
        mPopupWindow.setContentView(contentView);
        return this;
    }

    public final PopupWindow setInputMethodMode(int mode) {
        mPopupWindow.setInputMethodMode(mode);
        return this;
    }

    public final PopupWindow setOutsideTouchable(boolean enable){
        mPopupWindow.setOutsideTouchable(enable);
        return this;
    }

    public final PopupWindow setTouchModal(boolean enable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mPopupWindow.setTouchModal(enable);
        }
        return this;
    }

    public final PopupWindow setTouchable(boolean enable){
        mPopupWindow.setTouchable(enable);
        return this;
    }

    public final PopupWindow showAtLocation(View view){
        return showAtLocation(view, Gravity.CENTER,0,0);
    }

    public final PopupWindow showAtLocation(View view, int gravity, int x, int y){
        mPopupWindow.showAtLocation(view,gravity,x,y);
        return this;
    }

    public final PopupWindow dismiss(){
        mPopupWindow.dismiss();
        return this;
    }

    public final boolean isShowing(){
        return mPopupWindow.isShowing();
    }

    public final int getSoftInputMode() {
        return mPopupWindow.getSoftInputMode();
    }



}
