package luckmerlin.task;

import luckmerlin.core.Call;

public class TaskResult<T extends Result> extends Call<T> {

    public TaskResult(int code, String note, T data) {
        super(code, note, data);
    }
}
