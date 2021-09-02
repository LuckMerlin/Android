package luckmerlin.databinding.touch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

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
            Object src=mSrc;
            if (view instanceof ImageView){
                if (null==src){
                    ((ImageView)view).setImageDrawable(null);
                }else if (src instanceof Integer){
                    ((ImageView)view).setImageResource((Integer)src);
                }else if (src instanceof Bitmap){
                    ((ImageView)view).setImageBitmap((Bitmap)src);
                }else if (src instanceof Drawable){
                    ((ImageView)view).setImageDrawable((Drawable) src);
                }
            }
        }
    }
}
