package luckmerlin.databinding;

import android.view.View;
import androidx.databinding.BindingAdapter;

public class MBindingAdapter {
    private MBindingAdapter(){

    }

    @BindingAdapter("m")
    public static void setViewBinding(View view, Binding binding) {
        if (null!=view&&null!=binding){
            binding.onBind(view);
        }
    }

}
