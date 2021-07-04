package luckmerlin.databinding.touch;

import android.view.View;

public interface OnViewClick extends ClickListener {
    boolean onClicked(int viewId, int count, View view, Object tag);
}
