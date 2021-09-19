package luckmerlin.task;

import java.util.concurrent.Future;
import luckmerlin.core.Canceler;
import luckmerlin.core.Result;

public abstract class Runner implements Status{
    private Progress mProgress;
    private Result mResult;
    private Canceler mCanceler;

    protected Runner(Progress progress){
        mProgress=progress;
    }

    protected final Runner setResult(Result result) {
        this.mResult = result;
        return this;
    }

    protected final Runner setProgress(Progress progress) {
        this.mProgress = progress;
        return this;
    }

    protected final Runner setCanceler(Future future) {
        this.mCanceler = null!=future?(interrupt)->!future.isDone()&&
                !future.isCancelled()&&future.cancel(interrupt):null;
        return this;
    }


    public final Canceler getCanceler() {
        return mCanceler;
    }

    public final Progress getProgress() {
        return mProgress;
    }

    public abstract int getStatus();

    public final Result getResult() {
        return mResult;
    }

    public abstract Runner update(int status,Progress progress);

    public final boolean isSucceed(){
        Result result=getResult();
        return null!=result&&result.isSucceed();
    }

    public final boolean isExecuting(){
        return !isStatus(STATUS_IDLE);
    }

    public final boolean isStatus(int ...statuses){
        int length=null!=statuses?statuses.length:-1;
        if (length>0){
            int status=getStatus();
            for (int i = 0; i < length; i++) {
                if (statuses[0]==status){
                    return true;
                }
            }
        }
        return false;
    }

    public abstract Runner finisher(boolean add, Finisher runnable);
}
