package luckmerlin.task;

import java.util.concurrent.Callable;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;

public class CallableTask<T extends Result> extends AbstractTask<T>{
    private Callable<T> mCallable;

    public CallableTask(){
        this(null);
    }

    public CallableTask(Callable<T> callable){
        mCallable=callable;
    }

    public final CallableTask setRunnable(Callable<T> callable){
        mCallable=callable;
        return this;
    }

    @Override
    protected T onExecute(Runner runner) {
        Callable<T> callable=mCallable;
        if (null!=callable){
            try {
                return callable.call();
            } catch (Exception e) {
                Debug.E("Exception execute call task.e="+e,e);
                e.printStackTrace();
            }
        }
        return null;
    }

    public final Callable<T> getCallable() {
        return mCallable;
    }
}
