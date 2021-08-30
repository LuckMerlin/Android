package luckmerlin.databinding;

import android.view.View;

public interface OnModelCreate extends Listener {
    Object onCreate(View root, Class<? extends Model> cls);
}
