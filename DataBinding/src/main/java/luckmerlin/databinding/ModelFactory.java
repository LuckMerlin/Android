package luckmerlin.databinding;

import android.view.View;

public interface ModelFactory {
    Object create(View view, Class<?extends Model> cls);
}
