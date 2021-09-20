package luckmerlin.task;

import java.util.List;

import luckmerlin.core.match.Matchable;

public interface TaskGroup {
    List<?extends Task> delete(Object matchable);
    public boolean add(Object task);
    public List<?extends Task> getTasks(Matchable matchable);
    public int getSize();
}
