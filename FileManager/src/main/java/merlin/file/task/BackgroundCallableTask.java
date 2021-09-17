package merlin.file.task;

import java.util.concurrent.Callable;

import luckmerlin.core.Result;
import luckmerlin.task.CallableTask;

public class BackgroundCallableTask<T extends Result> extends CallableTask<T> implements BackgroundTask {
    public BackgroundCallableTask(){
        this(null);
    }

    public BackgroundCallableTask(Callable<T> callable){
        super(callable);
    }
}
