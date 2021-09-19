package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;

public abstract class AbstractTask<T extends Result> implements Task<T> {
    private String mName;
    private transient Runner mRunner;

    public final AbstractTask<T> setName(String name){
        mName=name;
        return this;
    }

    public final Runner getRunner() {
        return mRunner;
    }

    protected final boolean closeOnFinish(Closeable...closeables){
        return null!=closeables&&closeables.length>0&&finisher(true,(Result result)->close(closeables));
    }

    protected final boolean finisher(boolean add,Finisher finisher){
        Runner runner=null!=finisher?mRunner:null;
        return null!=finisher&&null!=runner.finisher(add,finisher);
    }

    protected final boolean update(int status,Progress progress){
        Runner runner=mRunner;
        if (null!=runner){
            runner.update(status,progress);
            return true;
        }
        return false;
    }

    public final boolean isExecuting(){
        Runner runner=getRunner();
        return null!=runner&&runner.getStatus()!=Status.STATUS_IDLE;
    }

    @Override
    public final String getName() {
        return mName;
    }

    protected abstract T onExecute(Runner runner);

    @Override
    public T execute(Runner runner) {
        if (isExecuting()){
            Debug.W("Can't execute task while already executing.");
            return null;
        }
        return onExecute(mRunner=runner);
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
