package luckmerlin.task;

import java.util.ArrayList;
import java.util.List;
import luckmerlin.core.debug.Debug;

public abstract class AbstractTask<T extends TaskResult> implements Task<T>, Status {
    private int mStatus=STATUS_IDLE;
    private Result mResult;
    private Progress mProgress;

    public AbstractTask(int status,Result result,Progress progress){
        mStatus=status;
        mResult=result;
        mProgress=progress;
    }

    @Override
    public final Result getResult() {
        return mResult;
    }

    @Override
    public final Progress getProgress() {
        return mProgress;
    }

    protected abstract T onExecute(Updater<T> updater);

    @Override
    public final T execute(final OnTaskUpdate update) {
        if (isExecuting()){
            Debug.W("Can't execute task while already executing.");
            return null;
        }
        final List<FinishCleaner<T>> endingRunnables=new ArrayList<>();
        final Updater<T> updater=new Updater<T>() {
            @Override
            public Updater update(int status, Task task, Progress arg) {
                if ((null==task? AbstractTask.this:task)== AbstractTask.this){
                    mStatus=status;
                    if (null!=arg){
                        mProgress=arg;
                    }
                }
                AbstractTask.this.update(status,task,arg,update);
                return this;
            }

            @Override
            public Updater finishCleaner(boolean add, FinishCleaner<T> runnable) {
                if (null!=runnable){
                    if (add){
                        if (!endingRunnables.contains(runnable)&&endingRunnables.add(runnable)){

                        }
                    }else if (endingRunnables.remove(runnable)){
                        //Do noting
                    }
                }
                return this;
            }
        };
        updater.update(Status.STATUS_START,this,null);
        T result=onExecute(updater);
        for (FinishCleaner<T> child:endingRunnables) {
            if (null!=child){
                child.onFinishClean(result);
            }
        }
        endingRunnables.clear();
        updater.update(Status.STATUS_FINISH,this,null);
        return result;
    }

    public final int getStatus(){
        return mStatus;
    }

    protected final boolean update(int status, Task task,Progress arg,Updater<TaskResult> updater){
        if (null!=updater){
            updater.update(status,task,arg);
            return true;
        }
        return false;
    }

    protected interface Updater<T>{
        Updater update(int status, Task task,Progress arg);
        Updater finishCleaner(boolean add,FinishCleaner<T> runnable);
    }

    protected interface FinishCleaner<T>{
        void onFinishClean(T result);
    }
}
