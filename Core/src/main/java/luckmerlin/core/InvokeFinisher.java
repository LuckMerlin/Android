package luckmerlin.core;

import android.app.Dialog;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TabHost;

public class InvokeFinisher{

    public final <T> boolean onInvokeFinish(int code,String note,T data,OnInvokeFinish<T> callback){
        if (null!=callback){
            PopupWindow popupWindow=new PopupWindow();
//            WindowManager manager;
//            popupWindow.setContentView();
//            callback.onInvokeFinish(code,note,data);
            return true;
        }
        TabHost host;
        return false;
    }
}
