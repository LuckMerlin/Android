package luckmerlin.databinding.window;

import android.content.Context;
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

    public <T extends ViewDataBinding> PopupWindow setContentView(Context context, int contentId, Execute<T> execute){
        return setContentView(context,contentId,execute,null);
    }

    public <T extends ViewDataBinding> PopupWindow setContentView(Context context, int contentId,
                                                                  Execute<T> execute, Listener listener){
        Type[] types=null!=execute&&null!=context?execute.getClass().getGenericInterfaces():null;
        Type classType=null!=types&&types.length==1?types[0]:null;
        Class cls=null!=classType&&classType instanceof Class?(Class)classType:null;
        ViewDataBinding binding=null!=cls?M.setContentView(context, mPopupWindow,contentId,listener):null;
        if (null!=binding&&cls.isAssignableFrom(cls)){
            execute.execute((T)binding);
        }
        return this;
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

    public final int getSoftInputMode() {
        return mPopupWindow.getSoftInputMode();
    }



}
