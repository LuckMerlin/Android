package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.task.Execute;
import luckmerlin.task.TaskResult;

public class DeleteTask extends PathsTask {


    public DeleteTask(List<Path> paths) {
        this(paths,null);
    }

    public DeleteTask(List<Path> paths,Execute execute) {
        super(execute);
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Updater updater) {
        return null;
    }
}
