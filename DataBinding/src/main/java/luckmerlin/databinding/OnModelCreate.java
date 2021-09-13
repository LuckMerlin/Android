package luckmerlin.databinding;

import android.view.View;

import luckmerlin.databinding.model.Model;

public interface OnModelCreate extends Listener {
    Object onCreate(View root, Class<? extends Model> cls);
}
