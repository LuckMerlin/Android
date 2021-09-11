package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.task.Execute;
import luckmerlin.task.Progress;
import luckmerlin.task.Result;
import luckmerlin.task.TaskResult;

public class MoveTask extends PathsTask {

    public MoveTask(List<Path> paths) {
        this(paths,null);
    }

    public MoveTask(List<Path> paths,Execute execute) {
        super(execute);
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Updater updater) {
        return null;
    }
}
