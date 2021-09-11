package luckmerlin.task;

import luckmerlin.core.Reply;
import luckmerlin.core.Result;

public class TaskResult<T extends Result> extends Reply<T> implements Result {

    public TaskResult(int code, String note, T data) {
        super(code, note, data);
    }
}
