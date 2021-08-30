package luckmerlin.databinding.touch;

import android.view.View;

public interface OnViewClick extends ClickListener {
    boolean onClicked(View view,int id, int count, Object tag);
}
