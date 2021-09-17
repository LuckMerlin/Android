package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;

public abstract class AbstractTask<T extends Result> implements Task<T> {
    private transient String mName;
    private transient Running mRunning;

    public final AbstractTask setRunning(Running running){
        if (null!=mRunning){
            mRunning=running;
        }
        return this;
    }

    public final AbstractTask<T> setName(String name){
        mName=name;
        return this;
    }

    public final Running getRunning() {
        return mRunning;
    }

    protected final boolean closeOnFinish(Closeable...closeables){
        return null!=closeables&&closeables.length>0&&finisher(true,(Result result)->close(closeables));
    }

    protected final boolean finisher(boolean add,Finisher finisher){
        Running running=null!=finisher?mRunning:null;
        return null!=finisher&&null!=running.finisher(add,finisher);
    }

    protected final boolean update(int status){
        return update(status,null);
    }

    protected final boolean update(int status, Progress arg){
        Running running=mRunning;
        if (null!=running){
            running.update(status,this,arg);
            return true;
        }
        return false;
    }

    @Override
    public final String getName() {
        return mName;
    }

    @Override
    public final int getStatus() {
        Running running=mRunning;
        return null!=running?running.getStatus():Status.STATUS_IDLE;
    }

    @Override
    public final Result getResult() {
        Running running=mRunning;
        return null!=running?running.getResult():null;
    }

    @Override
    public final Progress getProgress() {
        Running running=mRunning;
        return null!=running?running.getProgress():null;
    }

    protected abstract T onExecute(Running running);

    @Override
    public final T execute(Running running) {
        if (isExecuting()){
            Debug.W("Can't execute task while already executing.");
            return null;
        }
        return onExecute(mRunning=running);
    }

    protected final boolean close(Closeable ...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables) {
                try {
                    if (null!=child){
                        child.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

}
