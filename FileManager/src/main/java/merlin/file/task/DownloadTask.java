package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.core.debug.Debug;
import luckmerlin.task.Execute;
import luckmerlin.task.Progress;
import luckmerlin.task.Running;
import luckmerlin.task.Status;
import luckmerlin.task.TaskResult;
import luckmerlin.task.Updater;

public class DownloadTask extends PathsTask {

    public DownloadTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Running running) {
        Debug.D("QQQQQQQQQQ  "+Thread.currentThread());
//        update(Status.STATUS_PREPARE,null,updater);
        Progress progress;
        Execute execute;
        return null;
    }
}
