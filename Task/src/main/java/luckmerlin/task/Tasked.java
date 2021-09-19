package luckmerlin.task;

import luckmerlin.core.Result;

public final class Tasked<T extends Result> implements Task<T>{
    protected final Task<T> mTask;
    private Runner mRunner;

    protected Tasked(Task<T> task){
        mTask=task;
    }

    protected final Tasked setRunner(Runner runner) {
        this.mRunner = runner;
        return this;
    }

    @Override
    public T execute(Runner runner) {
        Task<T> task=mTask;
        return null!=task?task.execute(runner):null;
    }

    public final String getName() {
        Task<T> task=mTask;
        return null!=task?task.getName():null;
    }

    public final int getStatus() {
        Runner runner=mRunner;
        return null!=runner?runner.getStatus():Status.STATUS_IDLE;
    }

    public final Result getResult() {
        Runner runner=mRunner;
        return null!=runner?runner.getResult():null;
    }

    public final Progress getProgress() {
        Runner runner=mRunner;
        return null!=runner?runner.getProgress():null;
    }

    public final Runner getRunner() {
        return mRunner;
    }

    public final Task<T> getTask() {
        return mTask;
    }

    @Override
    public final boolean equals(Object o) {
        if (null!=o&&o instanceof Task){
            Task<T> current=mTask;
            return (null!=current&&current.equals(o))||super.equals(o);
        }
        return super.equals(o);
    }
}
