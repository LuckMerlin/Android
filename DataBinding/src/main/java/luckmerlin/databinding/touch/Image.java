package luckmerlin.databinding.touch;

import android.view.View;
import luckmerlin.databinding.Binding;

public final class Image implements Binding {
    private Object mSrc;

    public Image src(Object src){
        mSrc=src;
        return this;
    }

    public static Image image(Object src){
        return new Image().src(src);
    }

    @Override
    public void onBind(View view) {
        if (null!=view){
            Object srcImage=mSrc;
        }
    }
}
