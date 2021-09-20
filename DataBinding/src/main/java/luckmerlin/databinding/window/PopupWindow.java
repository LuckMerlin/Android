package luckmerlin.databinding.window;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import androidx.databinding.ViewDataBinding;
import luckmerlin.databinding.Listener;
import luckmerlin.databinding.model.M;

public class PopupWindow {
    private final android.widget.PopupWindow mPopupWindow;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = android.widget.PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
    public static final int INPUT_METHOD_NEEDED = android.widget.PopupWindow.INPUT_METHOD_NEEDED;
    public static final int INPUT_METHOD_NOT_NEEDED = android.widget.PopupWindow.INPUT_METHOD_NOT_NEEDED;
    private float mDim=0f;
    private int mDimAnimDuration=0;
    private android.widget.PopupWindow.OnDismissListener mListener;

    public PopupWindow(){
        this(null);
    }

    public PopupWindow(android.widget.PopupWindow popupWindow){
        popupWindow=mPopupWindow=null!=popupWindow?popupWindow:new android.widget.PopupWindow();
        popupWindow.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                android.widget.PopupWindow.OnDismissListener listener=mListener;
                mListener=null;
                if (null!=listener){
                    listener.onDismiss();
                }
                View contentView=mPopupWindow.getContentView();
                applyDim(null!=contentView?contentView.getContext():null,0);
            }
        });
    }

    public final PopupWindow setOnDismissListener(android.widget.PopupWindow.OnDismissListener onDismissListener) {
        if (isShowing()){
            mListener=onDismissListener;
        }
        return this;
    }

    public final PopupWindow setWidth(int width) {
        mPopupWindow.setWidth(width);
        return this;
    }

    public final PopupWindow setHeight(int height) {
        mPopupWindow.setHeight(height);
        return this;
    }

    public final PopupWindow setDimAnimDuration(int dimAnimDuration) {
        this.mDimAnimDuration = dimAnimDuration;
        return this;
    }

    public final PopupWindow setDim(float dim) {
        this.mDim = dim;
        return this;
    }

    public final int getDimAnimDuration() {
        return mDimAnimDuration;
    }

    public final float getDim() {
        return mDim;
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
        if (null!=view){
            applyDim(view.getContext(),mDim);
            mPopupWindow.showAtLocation(view,gravity,x,y);
        }
        return this;
    }

    private boolean applyDim(Context context,float dim){
        if (null==context){
            return false;
        }else if (context instanceof Activity){
            Activity activity=(Activity)context;
            final Window window=activity.getWindow();
            final WindowManager.LayoutParams params=null!=window?window.getAttributes():null;
            if (null!=params){
                float toAlpha=dim>=0&&dim<=1?1-dim:1;
                if (mDimAnimDuration<=0){
                    params.alpha=toAlpha;
                    window.setAttributes(params);
                    return true;
                }
                final ValueAnimator animator=ValueAnimator.ofFloat(params.alpha,toAlpha);
                animator.setDuration(mDimAnimDuration);
                animator.setInterpolator(new AccelerateInterpolator());
                final ValueAnimator.AnimatorUpdateListener listener=new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Object value=animation.getAnimatedValue();
                        boolean updated=false;
                        if (null!=value&&value instanceof Float){
                            updated=true;
                            params.alpha=(Float)value;
                            window.setAttributes(params);
                        }
                        if (!updated||!animation.isRunning()){
                            animator.removeUpdateListener(this);
                        }
                    }
                };
                animator.addUpdateListener(listener);
                animator.start();
                return true;
            }
            return false;
        }else if (context instanceof ContextWrapper){
            Context base=((ContextWrapper)context).getBaseContext();
            return null!=base&&base!=context&&applyDim(base,dim);
        }
        return false;
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
