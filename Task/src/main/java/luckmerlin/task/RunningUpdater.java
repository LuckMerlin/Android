package luckmerlin.task;

import java.util.ArrayList;
import java.util.List;

import luckmerlin.core.Result;

class RunningUpdater extends Updater implements Execute {
    private List<Finisher> mFinishers;
    private final OnTaskUpdate mOnTaskUpdate;
    private Progress mProgress;
    private int mStatus;
    private Result mResult;

    protected RunningUpdater(OnTaskUpdate onTaskUpdate){
        mOnTaskUpdate=onTaskUpdate;
    }

    final OnTaskUpdate getOnTaskUpdate() {
        return mOnTaskUpdate;
    }

    protected final RunningUpdater setResult(Result result) {
        this.mResult = result;
        return this;
    }

    protected final RunningUpdater cleanFinisher(boolean run){
        List<Finisher> finishers=mFinishers;
        if (null!=finishers){
            if (run){
                for (Finisher finisher:finishers) {
                    if (null!=finisher){
                        finisher.onFinish(mResult);
                    }
                }
            }
            finishers.clear();
            mFinishers=null;
        }
        return this;
    }

    @Override
    public final Progress getProgress() {
        return mProgress;
    }

    @Override
    public final int getStatus() {
        return mStatus;
    }

    @Override
    public final Result getResult() {
        return mResult;
    }

    @Override
    protected final Updater update(int status, Task task, Progress arg) {
        mStatus=status==Status.STATUS_IDLE?null!=mResult?Status.STATUS_IDLE:mStatus:status;
        mProgress=null==arg?mProgress:arg;
        OnTaskUpdate onTaskUpdate=mOnTaskUpdate;
        if (null!=onTaskUpdate){
            onTaskUpdate.onTaskUpdate(status,task,mProgress);
        }
        return this;
    }

    @Override
    protected final Updater finisher(boolean add, Finisher runnable) {
        if (null != runnable) {
            List<Finisher> finishers = mFinishers;
            finishers=add&&null==finishers?(mFinishers=new ArrayList<>()):finishers;
            if (null!=(finishers=add&&null==finishers?(mFinishers=new ArrayList<>()):finishers)){
                if (add&&!finishers.contains(runnable)){
                    finishers.add(runnable);
                }else if (!add&&finishers.remove(runnable)&&finishers.size()<=0){
                    mFinishers=null;
                }
            }
        }
        return this;
    }
}
