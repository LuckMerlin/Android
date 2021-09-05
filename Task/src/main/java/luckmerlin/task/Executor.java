package luckmerlin.task;

import luckmerlin.core.Call;

public interface Executor {
    Call execute(Runnable runnable);
}
