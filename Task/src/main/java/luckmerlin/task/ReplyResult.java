package luckmerlin.task;

import luckmerlin.core.Reply;
import luckmerlin.core.Result;

public class ReplyResult<T > extends Reply<T> implements Result {

    public ReplyResult(int code, String note, T data) {
        super(code, note, data);
    }
}
