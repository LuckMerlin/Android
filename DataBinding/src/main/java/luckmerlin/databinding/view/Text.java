package luckmerlin.databinding.view;

import android.view.View;
import android.widget.TextView;
import luckmerlin.databinding.BS;
import luckmerlin.databinding.Binding;

public class Text extends BS implements Binding {
    private Object mText;
    private Object mHint;

    public Text setText(Object text){
        mText=text;
        return this;
    }

    public Text hint(Object hint){
        mHint=hint;
        return this;
    }

    public static Text text(Object text){
        return new Text().setText(text);
    }

    @Override
    public void onBind(View view) {
        super.onBind(view);
        if (null!=view){
            Object text=mText;
            Object hint=mHint;
            if (view instanceof TextView){
                if (null==text){
                }else if (text instanceof Integer){
                    ((TextView)view).setText((Integer)text);
                }else if (text instanceof CharSequence){
                    ((TextView)view).setText((CharSequence)text);
                }
                if (null==hint){
                }else if (hint instanceof Integer){
                    ((TextView)view).setHint((Integer)hint);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Text{" +
                "mText=" + mText +
                '}';
    }
}
