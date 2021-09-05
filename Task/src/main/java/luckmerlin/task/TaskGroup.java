package luckmerlin.task;

import luckmerlin.core.data.Page;

public interface TaskGroup {

    public default TaskGroup append(Task task){
        return append(task,false);
    }

    public TaskGroup append(Task task,boolean skipEqual);

    public default TaskGroup insert(int index,Task task){
        return insert(index,task,false);
    }

    public Page<Task> getTasks(Task anchor,int limit);

    public TaskGroup insert(int index,Task task,boolean skipEqual);

    public int getSize();

    public default boolean isExist(Object task){
        return null!=task&&indexFirst(task)>=0;
    }

    public Task findFirst(Object task);

    public int indexFirst(Object task);

    public boolean removeFirst(Object task);
}
