package luckmerlin.task;

import java.util.List;

import luckmerlin.core.match.Matchable;

public interface TaskGroup {
    List<Task> delete(Matchable matchable);
    public boolean add(Object task);
    public List<Task> getTasks(Matchable matchable);
    public int getSize();
}
