package luckmerlin.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import luckmerlin.core.Canceler;
import luckmerlin.core.JsonResult;
import luckmerlin.core.Result;

public class Runner implements Status{
    private List<Finisher> mFinishers;
    private Progress mProgress;
    private Result mResult;
    private Cancel mCanceler;
    private int mStatus;
    protected Saved mSaved;
    protected boolean mRunning=false;

    protected Runner(Progress progress,Saved saved){
        mProgress=progress;
        mSaved=saved;
    }

    public final Runner cleanResult(){
        return setResult(null);
    }

    public final boolean isCanceled(){
        Cancel cancel=mCanceler;
        return null!=cancel&&cancel.isCanceled();
    }

    protected final Runner setResult(Result result) {
        this.mResult = result;
        Saved saved=mSaved;
        if (null!=saved&&saved.setResult(null!=result&&result
                instanceof JsonResult?(JsonResult)result:null)){
            //Do nothing
        }
        return this;
    }

    protected final Saved getSaved() {
        return mSaved;
    }

    protected final Runner setProgress(Progress progress) {
        Progress current=mProgress;
        if (!((null==current&&null==progress)||(null!=current&&null!=progress&&current==progress))){
            this.mProgress = progress;
        }
        return this;
    }

    protected final Runner setStatus(int status) {
        if (mStatus!=status){
            this.mStatus = status;
        }
        return this;
    }

    protected final Runner setCanceler(Future future) {
        this.mCanceler=null!=future?new Cancel(future):null;
        return this;
    }

    public final Canceler getCanceler() {
        return mCanceler;
    }

    public final Progress getProgress() {
        return mProgress;
    }

    public final int getStatus(){
        return mStatus;
    }

    public final Result getResult() {
        return mResult;
    }

    public  Runner update(int status,Progress progress){
        return setProgress(progress).setStatus(status);
    }

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

    protected final Runner cleanFinisher(boolean run){
        List<Finisher> finishers=mFinishers;
        if (null!=finishers){
            if (run){
                for (Finisher finisher:finishers) {
                    if (null!=finisher){
                        finisher.onFinish(getResult());
                    }
                }
            }
            finishers.clear();
            mFinishers=null;
        }
        mCanceler=null;
        return this;
    }

    public final Runner finisher(boolean add, Finisher runnable) {
        if (mRunning&&null != runnable) {
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

    private static class Cancel implements luckmerlin.core.Canceler{
        private final Future mFuture;

        protected Cancel(Future future){
            mFuture=future;
        }

        public boolean isCanceled(){
            Future future=mFuture;
            return null!=future&&future.isCancelled();
        }

        @Override
        public boolean cancel(boolean interrupt) {
            Future future=mFuture;
            return !future.isCancelled()&&future.cancel(interrupt);
        }
    }

}
