package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;

import luckmerlin.task.Execute;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public class MoveTask extends PathsTask {

    public MoveTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Running running) {
        return null;
    }
}
