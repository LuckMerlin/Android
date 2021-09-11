package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.task.Execute;
import luckmerlin.task.Progress;
import luckmerlin.task.Result;
import luckmerlin.task.Status;
import luckmerlin.task.TaskResult;

public class UploadTask extends PathsTask {

    public UploadTask(List<Path> paths) {
        this(paths,null);
    }

    public UploadTask(List<Path> paths,Execute execute) {
        super(execute);
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Updater updater) {

        return null;
    }
}
