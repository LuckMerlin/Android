package luckmerlin.task;

import java.util.Random;
import java.util.UUID;
import luckmerlin.core.Result;

public final class Tasked<T extends Result> implements Task<T>{
    protected final Task<T> mTask;
    protected final String mTaskId;
    protected final long mCreateTime;
    private Runner mRunner;

    protected Tasked(Task<T> task){
        this(null,task,0);
    }

    protected Tasked(String taskId,Task<T> task,long createTime){
        mTask=task;
        mCreateTime=createTime<=0?System.currentTimeMillis():createTime;
        mTaskId=null!=taskId&&taskId.length()>0?taskId: (null!=task?task.getClass().getName():"None")+
                UUID.randomUUID().toString() +new Random().nextLong()+System.currentTimeMillis();
    }

    protected final Tasked setRunner(Runner runner) {
        this.mRunner = runner;
        return this;
    }

    public final Class<?extends Task> getTaskClass(){
        Task<T> task=mTask;
        return null!=task?task.getClass():null;
    }

    public final long getCreateTime() {
        return mCreateTime;
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
        if (null!=o){
            if (o instanceof Task){
                Task<T> current=mTask;
                return (null!=current&&current.equals(o))||super.equals(o);
            }else if (o instanceof String){
                String taskId=mTaskId;
                return null!=taskId&&taskId.equals(o);
            }
        }
        return super.equals(o);
    }
}
