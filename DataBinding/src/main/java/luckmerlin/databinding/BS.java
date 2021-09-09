package luckmerlin.databinding;

import android.view.View;
import java.util.ArrayList;

public class BS implements Binding{
    private ArrayList<Binding> mBinding;

    @Override
    public void onBind(View view) {
        ArrayList<Binding> bindings=mBinding;
        if (null!=bindings){
            for (Binding child:bindings) {
                if (null!=child){
                    child.onBind(view);
                }
            }
        }
    }

    public final BS add(Binding... bindings){
        if (null!=bindings&&bindings.length>0){
            ArrayList<Binding> list=mBinding;
            for (Binding child:bindings) {
                if (null!=child&&!(null!=list?list:(list=mBinding=new ArrayList<>())).
                        contains(child)&&list.add(child)){
                    //Do nothing
                }
            }
        }
        return this;
    }

    public final BS remove(Binding binding){
        ArrayList<Binding> list=null!=binding?mBinding:null;
        if (null!=list&&list.remove(binding)){
            //Do nothing
        }
        return this;
    }

}
