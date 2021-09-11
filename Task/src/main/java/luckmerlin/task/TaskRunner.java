package luckmerlin.task;

import java.util.List;

import luckmerlin.core.match.Matchable;

interface TaskRunner extends TaskGroup {
    List<Task> start(Object task);
    List<Task> cancel(boolean interrupt,Matchable matchable);
    boolean put(OnTaskUpdate callback,Matchable<Task> matchable);
    boolean remove(OnTaskUpdate callback);
}
