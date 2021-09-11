package merlin.file.task;

import com.merlin.file.Path;
import java.util.List;
import luckmerlin.task.Execute;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public class CopyTask extends PathsTask {

    public CopyTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Running running) {
        return null;
    }
}
