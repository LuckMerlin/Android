package luckmerlin.task;

import java.util.concurrent.Future;
import luckmerlin.core.Canceler;

public class Running extends RunningUpdater{
    private Canceler mCanceler;

    public Running(OnTaskUpdate onTaskUpdate) {
        super(onTaskUpdate);
    }

    protected Running setCanceler(Future future) {
        this.mCanceler = null!=future?(interrupt)->!future.isDone()&&
                !future.isCancelled()&&future.cancel(interrupt):null;
        return this;
    }

    public final Canceler getCanceler() {
        return mCanceler;
    }
}