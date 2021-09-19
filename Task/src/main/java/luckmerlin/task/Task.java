package luckmerlin.task;

import luckmerlin.core.Result;

public interface Task<T extends Result> extends Status{

    T execute(Runner runner);

    String getName();
}
