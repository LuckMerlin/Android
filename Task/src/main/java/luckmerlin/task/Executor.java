package luckmerlin.task;

import luckmerlin.core.Call;

public interface Executor<T extends Call> {
    T execute(Runnable runnable);
}
