package luckmerlin.task;

import luckmerlin.core.Call;
import luckmerlin.core.debug.Debug;

public abstract class AbstractTask<A> implements Task<A>, Status {
    private transient int mStatus=STATUS_IDLE;
    private transient Result mResult;
    private transient Progress mProgress;
    @Override
    public final Result getResult() {
        return mResult;
    }

    @Override
    public final Progress getProgress() {
        return mProgress;
    }

    protected abstract Call<R> onExecute(A arg, Executor executor, Updater updater);

    @Override
    public final Call execute(A arg, Executor executor, final OnTaskUpdate update) {
        if (isExecuting()){
            Debug.W("Can't execute task while already executing.");
            return null;
        }
        mStatus=STATUS_START;
        return onExecute(arg, executor, new Updater() {
            @Override
            public Updater update(int status, Task task, Object arg) {
                if ((null==task? AbstractTask.this:task)== AbstractTask.this){
                    mStatus=status;
                    if (null==arg){

                    }else if (arg instanceof Progress){
                        mProgress=(Progress)arg;
                    }else if (arg instanceof Result){
                        mResult=(Result) arg;
                    }
                }
                return this;
            }
        });
    }

    public final int getStatus(){
        return mStatus;
    }

    protected interface Updater{
        Updater update(int status, Task task,Object arg);
    }
}
