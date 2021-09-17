package luckmerlin.task;

import luckmerlin.core.Result;

public class RunnableTask extends AbstractTask<Result>{
    private Runnable mRunnable;

    public RunnableTask(){
        this(null);
    }

    public RunnableTask(Runnable runnable){
        mRunnable=runnable;
    }

    public final RunnableTask setRunnable(Runnable runnable){
        mRunnable=runnable;
        return this;
    }

    @Override
    protected Result onExecute(Running running) {
        Runnable runnable=mRunnable;
        if (null!=runnable){
            runnable.run();
            return ()-> true;
        }
        return ()-> false;
    }

    public final Runnable getRunnable() {
        return mRunnable;
    }
}
