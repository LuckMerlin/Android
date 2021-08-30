package luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;

public interface OnModelBind extends Listener {
    boolean onModelBind(ViewDataBinding binding);
}
