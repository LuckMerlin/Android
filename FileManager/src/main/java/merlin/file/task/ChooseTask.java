package merlin.file.task;

import com.merlin.file.Path;
import java.util.List;

import luckmerlin.core.Result;
import luckmerlin.task.ReplyResult;
import luckmerlin.task.Runner;

public class ChooseTask  extends PathsTask  implements BackgroundTask{
    public ChooseTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected Result onExecutePath(Path path, Runner runner) throws Exception {
        return null;
    }
}
