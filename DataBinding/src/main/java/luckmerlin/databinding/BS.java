package luckmerlin.databinding;

import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;

public final class BS extends ArrayList<Binding> implements Binding{

    @Override
    public final void onBind(View view) {
        for (Binding child:this) {
            if (null!=child){
                child.onBind(view);
            }
        }
    }

    public final BS append(Binding... bindings){
        if (null!=bindings&&bindings.length>0){
            super.addAll(Arrays.asList(bindings));
        }
        return this;
    }

}
