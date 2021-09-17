package merlin.file.task;

import luckmerlin.core.Reply;
import luckmerlin.core.Result;

public interface ConfirmResult<T> extends Result {

    public interface Confirm{
        Reply confirm();
    }

    Confirm getConfirm();
}
