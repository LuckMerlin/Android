package merlin.file.task;

import com.merlin.file.Path;
import java.util.List;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public class ChooseTask  extends PathsTask  implements BackgroundTask{
    public ChooseTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Running running) {
        return null;
    }
}
