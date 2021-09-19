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
    public boolean remove(OnTaskUpdate callback) {
        TaskRunner runner=mTaskRunner;
        return null!=runner&&runner.remove(callback);
    }

    @Override
    public List<Task> delete(Matchable matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.delete(matchable):null;
    }

    @Override
    public boolean add(Task task) {
        TaskRunner runner=mTaskRunner;
        return null!=runner&&runner.add(task);
    }

    @Override
    public List<Task> getTasks(Matchable matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.getTasks(matchable):null;
    }

    @Override
    public final int getSize() {
        TaskGroup taskGroup=mTaskRunner;
        return null!=taskGroup?taskGroup.getSize():-1;
    }

    @Override
    public List<Tasked> start(Object matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.start(matchable):null;
    }

    @Override
    public List<Tasked> cancel(boolean interrupt, Matchable matchable) {
        TaskRunner runner=mTaskRunner;
        return null!=runner?runner.cancel(interrupt,matchable):null;
    }
}
