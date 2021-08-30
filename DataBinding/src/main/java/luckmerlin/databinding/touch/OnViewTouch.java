package luckmerlin.databinding.touch;

import android.view.MotionEvent;
import android.view.View;

public interface OnViewTouch extends ClickListener {
    boolean onTouched(int viewId,Object tag,View v, MotionEvent event);
}
