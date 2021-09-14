package luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;
import luckmerlin.databinding.model.Model;

public interface OnModelBind extends Listener {
    Model onModelBind(ViewDataBinding binding);
}
