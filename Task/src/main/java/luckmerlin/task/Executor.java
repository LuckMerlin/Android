package luckmerlin.task;

import luckmerlin.core.Caller;

public interface Executor<T extends Caller> {
    T execute(Runnable runnable);
}
