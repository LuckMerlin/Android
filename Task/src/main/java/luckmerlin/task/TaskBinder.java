package luckmerlin.task;

import android.os.Binder;
import java.util.List;
import luckmerlin.core.match.Matchable;

public class TaskBinder extends Binder implements TaskRunner {
    private final TaskRunner mTaskRunner;

    public TaskBinder(TaskRunner runner){
        mTaskRunner=runner;
    }

    @Override
    public boolean put(OnTaskUpdate callback, Matchable<Task> matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner&&runner.put(callback,matchable);
    }

    @Override
    public List<Tasked> restart(Object matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.restart(matchable):null;
    }

    @Override
    public boolean remove(OnTaskUpdate callback) {
        TaskRunner runner=mTaskRunner;
        return null!=runner&&runner.remove(callback);
    }

    @Override
    public List<?extends Task> delete(Object matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.delete(matchable):null;
    }

    @Override
    public boolean add(Object task) {
        TaskRunner runner=mTaskRunner;
        return null!=runner&&runner.add(task);
    }

    @Override
    public List<?extends Task> getTasks(Matchable matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.getTasks(matchable):null;
    }

    @Override
    public final int getSize() {
        TaskGroup taskGroup=mTaskRunner;
        return null!=taskGroup?taskGroup.getSize():-1;
    }

    @Override
    public List<Tasked> fetch(Matchable matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.fetch(matchable):null;
    }

    @Override
    public List<Tasked> start(Object matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.start(matchable):null;
    }

    @Override
    public List<Tasked> cancel(boolean interrupt, Object matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.cancel(interrupt,matchable):null;
    }
}
