package luckmerlin.task;

import java.util.ArrayList;
import java.util.List;
import luckmerlin.core.debug.Debug;

public abstract class AbstractTask<T extends TaskResult> implements Task<T> {
    private Execute mExecute;

    public AbstractTask(Execute execute){
        mExecute=execute;
    }

    public final Execute setExecute(Execute execute){
        mExecute=execute;
        return this;
    }

    @Override
    public int getStatus() {
        Execute execute=mExecute;
        return null!=execute?execute.getStatus():Status.STATUS_WAIT;
    }

    @Override
    public Result getResult() {
        Execute execute=mExecute;
        return null!=execute?execute.getResult():null;
    }

    @Override
    public Progress getProgress() {
        Execute execute=mExecute;
        return null!=execute?execute.getProgress():null;
    }

    public final Execute getExecute() {
        return mExecute;
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
        T result=onExecute(updater);
        for (FinishCleaner<T> child:endingRunnables) {
            if (null!=child){
                child.onFinishClean(result);
            }
        }
        endingRunnables.clear();
        return result;
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
