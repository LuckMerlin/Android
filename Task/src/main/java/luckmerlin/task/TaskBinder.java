package luckmerlin.task;

import android.os.Binder;
import luckmerlin.core.data.Page;

public class TaskBinder extends Binder implements TaskGroup {
    private TaskGroup mTaskGroup;

    public final TaskBinder setGroup(TaskGroup group){
        mTaskGroup=group;
        return this;
    }

    @Override
    public Page<Task> getTasks(Task anchor, int limit) {
        TaskGroup taskGroup=mTaskGroup;
        if (null==taskGroup){
            return new Page<>().setTotal(0);
        }
        return taskGroup.getTasks(anchor,limit);
    }

    @Override
    public final TaskGroup append(Task task, boolean skipEqual) {
        TaskGroup taskGroup=mTaskGroup;
        if (null!=taskGroup){
            taskGroup.append(task,skipEqual);
        }
        return this;
    }

    @Override
    public final TaskGroup insert(int index, Task task, boolean skipEqual) {
        TaskGroup taskGroup=mTaskGroup;
        if (null!=taskGroup){
            taskGroup.insert(index,task,skipEqual);
        }
        return this;
    }

    @Override
    public final int getSize() {
        TaskGroup taskGroup=mTaskGroup;
        return null!=taskGroup?taskGroup.getSize():-1;
    }

    @Override
    public final Task findFirst(Object task) {
        TaskGroup taskGroup=mTaskGroup;
        return null!=taskGroup?taskGroup.findFirst(task):null;
    }

    @Override
    public final int indexFirst(Object task) {
        TaskGroup taskGroup=mTaskGroup;
        return null!=taskGroup?taskGroup.indexFirst(task):-1;
    }

    @Override
    public final boolean removeFirst(Object task) {
        TaskGroup taskGroup=mTaskGroup;
        return null!=taskGroup?taskGroup.removeFirst(task):null;
    }
}
