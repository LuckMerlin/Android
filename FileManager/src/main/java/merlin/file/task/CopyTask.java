package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.task.Execute;
import luckmerlin.task.TaskResult;

public class CopyTask extends PathsTask {

    public CopyTask(List<Path> paths) {
        this(paths,null);
    }

    public CopyTask(List<Path> paths,Execute execute) {
        super(execute);
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Updater updater) {
        return null;
    }
}
