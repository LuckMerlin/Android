package luckmerlin.databinding.touch;

import android.view.View;

public interface OnViewLongClick extends ClickListener {
    boolean onLongClicked(int viewId, View view, Object tag);
}
